package ai.dragon.service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.dragon.entity.SiloEntity;
import ai.dragon.job.silo.ingestor.AbstractSiloIngestor;
import ai.dragon.job.silo.ingestor.FileSystemIngestor;
import ai.dragon.job.silo.ingestor.dto.SiloIngestProgress;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.activation.UnsupportedDataTypeException;

@Service
public class IngestorService {
    @Autowired
    private EmbeddingStoreService embeddingStoreService;

    public void runSiloIngestion(SiloEntity siloEntity, Consumer<SiloIngestProgress> progressCallback)
            throws Exception {
        AbstractSiloIngestor ingestor = getIngestorFromEntity(siloEntity);
        progressCallback.accept(SiloIngestProgress.builder().message("Listing documents...").build());
        List<Document> documents = ingestor.listDocuments();
        progressCallback.accept(SiloIngestProgress.builder()
                .message(String.format("Ingesting %d documents to Silo...", documents.size())).build());
        ingestDocumentsToSilo(documents, siloEntity, progressCallback);
    }

    private AbstractSiloIngestor getIngestorFromEntity(SiloEntity siloEntity) throws Exception {
        switch (siloEntity.getIngestorType()) {
            case FileSystem:
                return new FileSystemIngestor(siloEntity);
            default:
                throw new UnsupportedDataTypeException("Ingestor type not supported");
        }
    }

    private void ingestDocumentsToSilo(List<Document> documents, SiloEntity siloEntity,
            Consumer<SiloIngestProgress> progressCallback)
            throws Exception {
        EmbeddingStore<TextSegment> embeddingStore = embeddingStoreService.retrieveEmbeddingStore(siloEntity.getUuid());
        for (int i = 0; i < documents.size(); i++) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            int progress = (i * 100) / documents.size();
            Document document = documents.get(i);
            progressCallback.accept(SiloIngestProgress.builder()
                    .message(document.metadata().toString()).build());
            /*
             * TODO
             * public EmbeddingStoreIngestor(DocumentTransformer documentTransformer,
             * DocumentSplitter documentSplitter,
             * TextSegmentTransformer textSegmentTransformer,
             * EmbeddingModel embeddingModel,
             * EmbeddingStore<TextSegment> embeddingStore) {
             */
            EmbeddingStoreIngestor.ingest(documents, embeddingStore);
            progressCallback.accept(SiloIngestProgress.builder().progressPercentage(progress).build());
        }
        progressCallback.accept(SiloIngestProgress.builder()
                .message("End.").build());
        progressCallback.accept(SiloIngestProgress.builder().progressPercentage(100).build());

        // TODO
        /*
         * EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
         * String query = "How to construct a RAG system?";
         * Embedding queryEmbedding = embeddingModel.embed(query).content();
         * 
         * Filter onlyForUser1 = metadataKey("userId").isEqualTo("1");
         * 
         * EmbeddingSearchRequest embeddingSearchRequest1 =
         * EmbeddingSearchRequest.builder()
         * .queryEmbedding(queryEmbedding)
         * // .filter(onlyForUser1)
         * .build();
         * 
         * EmbeddingSearchResult<TextSegment> embeddingSearchResult1 =
         * embeddingStore.search(embeddingSearchRequest1);
         * for (EmbeddingMatch<TextSegment> embeddingMatch :
         * embeddingSearchResult1.matches()) {
         * jobContext().logger().info("=> " + embeddingMatch.score() + " : " +
         * embeddingMatch.embedded().metadata());
         * jobContext().logger().info(embeddingMatch.embedded().text());
         * jobContext().logger().info("=====");
         * }
         */
    }
}
