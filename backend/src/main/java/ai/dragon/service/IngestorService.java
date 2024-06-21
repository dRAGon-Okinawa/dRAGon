package ai.dragon.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.dizitart.no2.filters.FluentFilter;
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
        // TODO Don't clean all embeddings, just the ones that are not linked to any
        // document ->
        logCallback.accept(SiloIngestLoaderLogMessage.builder()
                .message(String.format("Cleaning all current embeddings of Silo '%s'...",
                        siloEntity.getUuid()))
                .build());
        embeddingStoreService.clearEmbeddingStore(siloEntity);
        // <- TODO Don't clean all embeddings, just the ones that are not linked to any
        // document
        logCallback.accept(SiloIngestLoaderLogMessage.builder()
                .message(String.format("Will ingest %d documents to Silo...", documents.size()))
                .build());
        ingestDocumentsToSilo(documents, siloEntity, progressCallback, logCallback);
        // TODO Need to clean embeddings unlinked to documents listing
    }

    private void ingestDocumentsToSilo(List<Document> documents, SiloEntity siloEntity,
            Consumer<Integer> progressCallback, Consumer<SiloIngestLoaderLogMessage> logCallback)
            throws Exception {
        EmbeddingStore<TextSegment> embeddingStore = embeddingStoreService
                .retrieveEmbeddingStore(siloEntity.getUuid());
        EmbeddingModel embeddingModel = embeddingModelService.modelForSilo(siloEntity);
        EmbeddingStoreIngestor embeddingStoreIngestor = buildIngestor(embeddingStore, embeddingModel, siloEntity);
        DefaultIngestorLoaderSettings defaultIngestorSettings = KVSettingUtil.kvSettingsToObject(
                siloEntity.getIngestorSettings(),
                DefaultIngestorLoaderSettings.class);
        logCallback.accept(SiloIngestLoaderLogMessage.builder()
                .message(String.format(
                        "Ingesting using '%s' Embedding Store and '%s' Embedding Model...",
                        embeddingStore.getClass(), embeddingModel.getClass()))
                .build());
        for (int i = 0; i < documents.size(); i++) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            progressCallback.accept((i * 100) / documents.size());
            ingestDocumentToSilo(documents.get(i), siloEntity.getUuid(), defaultIngestorSettings,
                    embeddingStoreIngestor, logCallback);
        }
        logCallback.accept(SiloIngestLoaderLogMessage.builder()
                .message("End of ingestion.").build());
        progressCallback.accept(100);
    }

    private void ingestDocumentToSilo(Document document,
            UUID siloUuid,
            DefaultIngestorLoaderSettings ingestorSettings,
            EmbeddingStoreIngestor embeddingStoreIngestor,
            Consumer<SiloIngestLoaderLogMessage> logCallback) {
        Metadata metadata = document.metadata();
        String documentLocation = metadata.getString("document_location");
        try {
            DocumentEntity documentEntity = documentRepository
                    .findUniqueWithFilter(FluentFilter.where("siloIdentifier").eq(siloUuid.toString())
                            .and(FluentFilter.where("location").eq(documentLocation)))
                    .orElse(DocumentEntity
                            .builder()
                            .siloIdentifier(siloUuid)
                            .location(documentLocation)
                            .name(metadata.getString("document_name"))
                            .build());
            documentEntity.setLastSeen(new Date(System.currentTimeMillis()));

            // Save Document to the database
            documentRepository.save(documentEntity);

            // Check if the document should be indexed
            if (!Boolean.TRUE.equals(ingestorSettings.getIndexNewDiscoveredDocuments())) {
                logCallback.accept(SiloIngestLoaderLogMessage.builder()
                        .message(String.format(
                                "Skipping Indexing of Document (indexNewDiscoveredDocuments == false) : %s",
                                documentLocation))
                        .build());
                return;
            }
            if (!Boolean.TRUE.equals(documentEntity.getAllowIndexing())) {
                logCallback.accept(SiloIngestLoaderLogMessage.builder()
                        .message(String.format(
                                "Skipping Indexing of Document (allowIndexing == false) : %s",
                                documentLocation))
                        .build());
                return;
            }

            // Ingest the document to the embedding store
            embeddingStoreIngestor.ingest(document);

            // Update the last indexed date
            documentEntity.setLastIndexed(new Date(System.currentTimeMillis()));
            documentRepository.save(documentEntity);
        } catch (Exception ex) {
            logCallback.accept(SiloIngestLoaderLogMessage.builder()
                    .messageLevel(SiloIngestProgressMessageLevel.Error)
                    .message(String.format("Unable to ingest '%s' : %s", documentLocation, ex.getMessage()))
                    .build());
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
}
