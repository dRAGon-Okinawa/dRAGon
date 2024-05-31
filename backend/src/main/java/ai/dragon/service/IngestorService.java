package ai.dragon.service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.embedding.EmbeddingModelSettings;
import ai.dragon.job.silo.ingestor.AbstractSiloIngestor;
import ai.dragon.job.silo.ingestor.FileSystemIngestor;
import ai.dragon.job.silo.ingestor.dto.SiloIngestLogMessage;
import ai.dragon.util.IniSettingUtil;
import dev.langchain4j.data.document.Document;
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

    public void runSiloIngestion(SiloEntity siloEntity, Consumer<Integer> progressCallback,
            Consumer<SiloIngestLogMessage> logCallback)
            throws Exception {
        AbstractSiloIngestor ingestor = getIngestorFromEntity(siloEntity);
        logCallback.accept(SiloIngestLogMessage.builder()
                .message(String.format("Listing documents using '%s' Ingestor...", ingestor.getClass())).build());
        List<Document> documents = ingestor.listDocuments();
        logCallback.accept(SiloIngestLogMessage.builder()
                .message(String.format("Will ingest %d documents to Silo...", documents.size())).build());
        ingestDocumentsToSilo(documents, siloEntity, progressCallback, logCallback);
    }

    private void ingestDocumentsToSilo(List<Document> documents, SiloEntity siloEntity,
            Consumer<Integer> progressCallback, Consumer<SiloIngestLogMessage> logCallback)
            throws Exception {
        EmbeddingStore<TextSegment> embeddingStore = embeddingStoreService.retrieveEmbeddingStore(siloEntity.getUuid());
        EmbeddingModel embeddingModel = modelForEntity(siloEntity);
        EmbeddingStoreIngestor ingestor = buildIngestor(embeddingStore, embeddingModel);
        logCallback.accept(SiloIngestLogMessage.builder()
                .message(String.format("Ingesting using '%s' Embedding Store and '%s' Embedding Model...",
                        embeddingStore.getClass(), embeddingModel.getClass()))
                .build());
        for (int i = 0; i < documents.size(); i++) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            int progress = (i * 100) / documents.size();
            Document document = documents.get(i);
            logCallback.accept(SiloIngestLogMessage.builder()
                    .message(document.metadata().toString()).build());
            ingestor.ingest(documents);
            progressCallback.accept(progress);
        }
        logCallback.accept(SiloIngestLogMessage.builder()
                .message("End.").build());
        progressCallback.accept(100);
    }

    private EmbeddingStoreIngestor buildIngestor(EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel) {
        return EmbeddingStoreIngestor.builder()
                .documentTransformer(document -> {
                    document.metadata().put("index_date", System.currentTimeMillis());
                    return document;
                })
                // TODO .documentSplitter(DocumentSplitters.recursive(1000, 200, new
                // OpenAiTokenizer()))
                .documentSplitter(DocumentSplitters.recursive(300, 50))
                .textSegmentTransformer(textSegment -> {
                    textSegment.metadata().put("index_date", System.currentTimeMillis());
                    return textSegment;
                })
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }

    private EmbeddingModel modelForEntity(SiloEntity siloEntity) throws Exception {
        EmbeddingModelSettings embeddingModelSettings = IniSettingUtil.convertIniSettingsToObject(
                siloEntity.getEmbeddingModelSettings(), EmbeddingModelSettings.class);
        return siloEntity.getEmbeddingModelType().getModelDefinition().getEmbeddingModelWithSettings()
                .apply(embeddingModelSettings);
    }

    private AbstractSiloIngestor getIngestorFromEntity(SiloEntity siloEntity) throws Exception {
        switch (siloEntity.getIngestorType()) {
            case FileSystem:
                return new FileSystemIngestor(siloEntity);
            default:
                throw new UnsupportedDataTypeException("Ingestor type not supported");
        }
    }
}
