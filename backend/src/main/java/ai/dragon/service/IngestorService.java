package ai.dragon.service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.SiloEntity;
import ai.dragon.enumeration.SiloIngestProgressMessageLevel;
import ai.dragon.job.silo.ingestor.dto.loader.SiloIngestLoaderLogMessage;
import ai.dragon.job.silo.ingestor.loader.ImplAbstractSiloIngestorLoader;
import ai.dragon.job.silo.ingestor.loader.filesystem.FileSystemIngestorLoader;
import ai.dragon.properties.embedding.EmbeddingSettings;
import ai.dragon.properties.loader.FileSystemIngestorLoaderSettings;
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
    private KVSettingService kvSettingService;

    @Autowired
    private EmbeddingSegmentService embeddingSegmentService;

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
        embeddingStoreService.clearEmbeddingStore(siloEntity.getUuid());
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
        EmbeddingModel embeddingModel = embeddingModelService.modelForEntity(siloEntity);
        EmbeddingStoreIngestor ingestor = buildIngestor(embeddingStore, embeddingModel, siloEntity);
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
            try {
                Document document = documents.get(i);
                logCallback.accept(SiloIngestLoaderLogMessage.builder()
                        .message(document.metadata().toString()).build());
                ingestor.ingest(document);
            } catch (Exception ex) {
                logCallback.accept(SiloIngestLoaderLogMessage.builder()
                        .messageLevel(SiloIngestProgressMessageLevel.Error)
                        .message(ex.getMessage())
                        .build());
            }
        }
        logCallback.accept(SiloIngestLoaderLogMessage.builder()
                .message("End of ingestion.").build());
        progressCallback.accept(100);
    }

    private EmbeddingStoreIngestor buildIngestor(EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel, SiloEntity siloEntity) {
        EmbeddingSettings embeddingSettings = kvSettingService.kvSettingsToObject(
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
            case FileSystem:
                FileSystemIngestorLoaderSettings settings = kvSettingService.kvSettingsToObject(
                        siloEntity.getIngestorSettings(),
                        FileSystemIngestorLoaderSettings.class);
                return new FileSystemIngestorLoader(siloEntity, settings);
            default:
                throw new UnsupportedDataTypeException("Ingestor type not supported");
        }
    }
}
