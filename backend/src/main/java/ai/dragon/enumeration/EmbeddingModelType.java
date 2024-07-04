package ai.dragon.enumeration;

import java.util.List;

import ai.dragon.job.silo.ingestor.dto.embedding.EmbeddingModelDefinition;
import dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;

public enum EmbeddingModelType {
    BgeSmallEnV15QuantizedEmbeddingModel("BgeSmallEnV15QuantizedEmbeddingModel"),
    OpenAiEmbeddingAda002Model("OpenAiEmbeddingAda002Model"),
    OpenAiEmbedding3SmallModel("OpenAiEmbedding3SmallModel"),
    OpenAiEmbedding3LargeModel("OpenAiEmbedding3LargeModel");

    private String value;

    EmbeddingModelType(String value) {
        this.value = value;
    }

    public static EmbeddingModelType fromString(String text) {
        for (EmbeddingModelType b : EmbeddingModelType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }

    public EmbeddingModelDefinition getModelDefinition() throws ClassNotFoundException {
        switch (this) {
            case BgeSmallEnV15QuantizedEmbeddingModel:
                return EmbeddingModelDefinition
                        .builder()
                        .languages(List.of("en"))
                        .embeddingModelClassName(
                                "dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15QuantizedEmbeddingModel")
                        .embeddingModelName("BgeSmallEnV15QuantizedEmbeddingModel")
                        .embeddingModelWithSettings(parameters -> {
                            // TODO : Ability to specify a different Thread Pool Size :
                            // Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
                            // => different than CPU cores
                            return new BgeSmallEnV15QuantizedEmbeddingModel();
                        })
                        .providerType(ProviderType.ONNX)
                        .dimensions(384)
                        .maxTokens(512)
                        .build();
            case OpenAiEmbeddingAda002Model:
                return EmbeddingModelDefinition
                        .builder()
                        .languages(List.of("en"))
                        .embeddingModelClassName("dev.langchain4j.model.openai.OpenAiEmbeddingModel")
                        .embeddingModelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002.toString())
                        .embeddingModelWithSettings(parameters -> {
                            return OpenAiEmbeddingModel.builder()
                                    .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002)
                                    .apiKey(parameters.getApiKey()).build();
                        })
                        .providerType(ProviderType.OpenAI)
                        .dimensions(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002.dimension())
                        .maxTokens(8191)
                        .build();
            case OpenAiEmbedding3SmallModel:
                return EmbeddingModelDefinition
                        .builder()
                        .languages(List.of("en"))
                        .embeddingModelClassName("dev.langchain4j.model.openai.OpenAiEmbeddingModel")
                        .embeddingModelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.toString())
                        .embeddingModelWithSettings(parameters -> {
                            return OpenAiEmbeddingModel.builder()
                                    .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                                    .apiKey(parameters.getApiKey()).build();
                        })
                        .providerType(ProviderType.OpenAI)
                        .dimensions(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.dimension())
                        .maxTokens(8191)
                        .build();
            case OpenAiEmbedding3LargeModel:
                return EmbeddingModelDefinition
                        .builder()
                        .languages(List.of("en"))
                        .embeddingModelClassName("dev.langchain4j.model.openai.OpenAiEmbeddingModel")
                        .embeddingModelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_LARGE.toString())
                        .embeddingModelWithSettings(parameters -> {
                            return OpenAiEmbeddingModel.builder()
                                    .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_LARGE)
                                    .apiKey(parameters.getApiKey()).build();
                        })
                        .providerType(ProviderType.OpenAI)
                        .dimensions(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_LARGE.dimension())
                        .maxTokens(8191)
                        .build();
            default:
                throw new ClassNotFoundException("Model not found");
        }
    }
}
