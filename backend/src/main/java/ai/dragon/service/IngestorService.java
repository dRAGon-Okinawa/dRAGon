package ai.dragon.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.dizitart.no2.filters.FluentFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.DocumentEntity;
import ai.dragon.entity.SiloEntity;
import ai.dragon.enumeration.SiloIngestProgressMessageLevel;
import ai.dragon.job.silo.ingestor.dto.loader.SiloIngestLoaderLogMessage;
import ai.dragon.job.silo.ingestor.loader.FileSystemIngestorLoader;
import ai.dragon.job.silo.ingestor.loader.ImplAbstractSiloIngestorLoader;
import ai.dragon.job.silo.ingestor.loader.NoneIngestorLoader;
import ai.dragon.job.silo.ingestor.loader.URLIngestorLoader;
import ai.dragon.properties.embedding.EmbeddingSettings;
import ai.dragon.properties.loader.DefaultIngestorLoaderSettings;
import ai.dragon.properties.loader.FileSystemIngestorLoaderSettings;
import ai.dragon.properties.loader.URLIngestorLoaderSettings;
import ai.dragon.repository.DocumentRepository;
import ai.dragon.util.KVSettingUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.activation.UnsupportedDataTypeException;

@Service
public class IngestorService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmbeddingStoreService embeddingStoreService;

    @Autowired
    private EmbeddingModelService embeddingModelService;

    @Autowired
    private EmbeddingSegmentService embeddingSegmentService;

    @Autowired
    private DocumentRepository documentRepository;

    public void runSiloIngestion(SiloEntity siloEntity, Consumer<Integer> progressCallback,
            Consumer<SiloIngestLoaderLogMessage> logCallback)
            throws Exception {
        long ingestionStartTime = System.currentTimeMillis();
        ImplAbstractSiloIngestorLoader ingestorLoader = getIngestorLoaderFromEntity(siloEntity);
        logCallback.accept(SiloIngestLoaderLogMessage.builder()
                .message(String.format("Listing documents using '%s' Ingestor Loader...",
                        ingestorLoader.getClass()))
                .build());
        List<Document> documents = ingestorLoader.listDocuments();
        if (documents == null || documents.isEmpty()) {
            logCallback.accept(SiloIngestLoaderLogMessage
                    .builder()
                    .messageLevel(SiloIngestProgressMessageLevel.Warning)
                    .message("No documents to ingest! Please check the Ingestor Settings.")
                    .build());
            return;
        }
        // Ingesting documents :
        logCallback.accept(SiloIngestLoaderLogMessage.builder()
                .message(String.format("Will ingest %d documents to Silo...", documents.size()))
                .build());
        ingestDocumentsToSilo(documents, siloEntity, progressCallback, logCallback);

        // Delete documents not seen since the start of the ingestion :
        logCallback.accept(SiloIngestLoaderLogMessage.builder()
                .message("Cleaning unseen documents in Silo...")
                .build());
        deleteDocumentsInSiloNotSeenSince(siloEntity, ingestionStartTime);
    }

    private void ingestDocumentsToSilo(List<Document> documents, SiloEntity siloEntity,
            Consumer<Integer> progressCallback, Consumer<SiloIngestLoaderLogMessage> logCallback)
            throws Exception {
        EmbeddingStore<TextSegment> embeddingStore = embeddingStoreService
                .retrieveEmbeddingStore(siloEntity.getUuid());
        EmbeddingModel embeddingModel = embeddingModelService.modelForSilo(siloEntity);
        EmbeddingStoreIngestor embeddingStoreIngestor = buildIngestor(embeddingStore, embeddingModel,
                siloEntity);
        DefaultIngestorLoaderSettings defaultIngestorSettings = KVSettingUtil.kvSettingsToObject(
                siloEntity.getIngestorSettings(),
                DefaultIngestorLoaderSettings.class);
        logCallback.accept(SiloIngestLoaderLogMessage.builder()
                .message(String.format(
                        "Ingesting using '%s' Embedding Store and '%s' Embedding Model...",
                        embeddingStore.getClass(), embeddingModel.getClass()))
                .build());
        int nbIndexedDocuments = 0;
        for (int i = 0; i < documents.size(); i++) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            progressCallback.accept((i * 100) / documents.size());
            boolean hasBeenIndexed = ingestDocumentToSilo(documents.get(i), siloEntity.getUuid(),
                    defaultIngestorSettings,
                    embeddingStoreIngestor, logCallback);
            if (hasBeenIndexed) {
                nbIndexedDocuments++;
            }
        }
        logCallback.accept(SiloIngestLoaderLogMessage.builder()
                .message(String.format("End of ingestion. Documents (re)indexed : %d", nbIndexedDocuments)).build());
        progressCallback.accept(100);
    }

    private boolean ingestDocumentToSilo(Document document,
            UUID siloUuid,
            DefaultIngestorLoaderSettings ingestorSettings,
            EmbeddingStoreIngestor embeddingStoreIngestor,
            Consumer<SiloIngestLoaderLogMessage> logCallback) {
        Metadata metadata = document.metadata();
        String documentLocation = metadata.getString("document_location");
        try {
            DocumentEntity documentEntity = documentRepository
                    .findUniqueWithFilter(
                            FluentFilter.where("siloIdentifier").eq(siloUuid.toString())
                                    .and(FluentFilter.where("location")
                                            .eq(documentLocation)))
                    .orElse(DocumentEntity
                            .builder()
                            .siloIdentifier(siloUuid)
                            .location(documentLocation)
                            .name(metadata.getString("document_name"))
                            .allowIndexing(Boolean.TRUE.equals(ingestorSettings.getIndexNewDiscoveredDocuments()))
                            .build());
            documentEntity.setLastSeen(new Date(System.currentTimeMillis()));

            // Save Document to the database :
            documentRepository.save(documentEntity);

            // Check if the Document could be indexed :
            if (!Boolean.TRUE.equals(documentEntity.getAllowIndexing())) {
                logger.debug(String.format(
                        "Skipping Indexing of Document (allowIndexing == false) : %s",
                        documentLocation));
                return false;
            }

            // Check if the Document should be indexed (date change) :
            long documentTime = metadata.getLong("document_date");
            Date documentDate = new Date(documentTime);
            Date lastIndexed = documentEntity.getLastIndexed();
            boolean newDocumentAvailable = lastIndexed == null || documentDate.after(lastIndexed);
            if (!newDocumentAvailable) {
                logger.debug(String.format(
                        "Skipping Indexing of Document (no change) : %s",
                        documentLocation));
                return false;
            }

            // Cleaning chunks of the document :
            embeddingStoreService.removeEmbeddingsForDocument(documentEntity);

            // Ingest the document to the embedding store :
            embeddingStoreIngestor.ingest(document);

            // Update the last indexed date :
            documentEntity.setLastIndexed(new Date(System.currentTimeMillis()));
            documentRepository.save(documentEntity);

            return true;
        } catch (Exception ex) {
            logCallback.accept(SiloIngestLoaderLogMessage.builder()
                    .messageLevel(SiloIngestProgressMessageLevel.Error)
                    .message(String.format("Unable to ingest '%s' : %s", documentLocation,
                            ex.getMessage()))
                    .build());
            return false;
        }
    }

    private EmbeddingStoreIngestor buildIngestor(EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel, SiloEntity siloEntity) {
        EmbeddingSettings embeddingSettings = KVSettingUtil.kvSettingsToObject(
                siloEntity.getEmbeddingSettings(),
                EmbeddingSettings.class);
        return EmbeddingStoreIngestor.builder()
                .documentTransformer(document -> {
                    document.metadata().put("index_date", System.currentTimeMillis());
                    return document;
                })
                .documentSplitter(DocumentSplitters.recursive(embeddingSettings.getChunkSize(),
                        embeddingSettings.getChunkOverlap()))
                .textSegmentTransformer(textSegment -> {
                    Metadata metadata = textSegment.metadata();
                    String text = embeddingSegmentService.cleanTextSegment(textSegment.text());
                    return new TextSegment(text, metadata);
                })
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }

    private ImplAbstractSiloIngestorLoader getIngestorLoaderFromEntity(SiloEntity siloEntity) throws Exception {
        switch (siloEntity.getIngestorLoader()) {
            case None:
                return new NoneIngestorLoader(siloEntity);
            case FileSystem:
                FileSystemIngestorLoaderSettings fsSettings = KVSettingUtil.kvSettingsToObject(
                        siloEntity.getIngestorSettings(),
                        FileSystemIngestorLoaderSettings.class);
                return new FileSystemIngestorLoader(siloEntity, fsSettings);
            case URL:
                URLIngestorLoaderSettings urlSettings = KVSettingUtil.kvSettingsToObject(
                        siloEntity.getIngestorSettings(),
                        URLIngestorLoaderSettings.class);
                return new URLIngestorLoader(siloEntity, urlSettings);
            default:
                throw new UnsupportedDataTypeException("Ingestor type not supported");
        }
    }

    private void deleteDocumentsInSiloNotSeenSince(SiloEntity siloEntity, long since) {
        documentRepository.findWithFilter(
                FluentFilter.where("siloIdentifier").eq(siloEntity.getUuid().toString())
                        .and(FluentFilter.where("lastSeen").lt(new Date(since))))
                .forEach(documentRepository::delete);
    }
}
