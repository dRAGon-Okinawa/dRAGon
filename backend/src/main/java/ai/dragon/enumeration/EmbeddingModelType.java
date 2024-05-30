package ai.dragon.enumeration;

import java.util.List;

import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import lombok.Builder;
import lombok.Data;

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

    public EmbeddingModelDefinition getModelDefinition() throws ClassNotFoundException {
        switch (this) {
            case BgeSmallEnV15QuantizedEmbeddingModel:
                return EmbeddingModelDefinition
                        .builder()
                        .languages(List.of("en"))
                        .embeddingModelClassName(
                                "dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15QuantizedEmbeddingModel")
                        .embeddingModelName("BgeSmallEnV15QuantizedEmbeddingModel")
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
                        .providerType(ProviderType.OpenAI)
                        .dimensions(1536)
                        .maxTokens(8191)
                        .build();
            case OpenAiEmbedding3SmallModel:
                return EmbeddingModelDefinition
                        .builder()
                        .languages(List.of("en"))
                        .embeddingModelClassName("dev.langchain4j.model.openai.OpenAiEmbeddingModel")
                        .embeddingModelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.toString())
                        .providerType(ProviderType.OpenAI)
                        .dimensions(1536)
                        .maxTokens(8191)
                        .build();
            case OpenAiEmbedding3LargeModel:
                return EmbeddingModelDefinition
                        .builder()
                        .languages(List.of("en"))
                        .embeddingModelClassName("dev.langchain4j.model.openai.OpenAiEmbeddingModel")
                        .embeddingModelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_LARGE.toString())
                        .providerType(ProviderType.OpenAI)
                        .dimensions(3072)
                        .maxTokens(8191)
                        .build();
            default:
                throw new ClassNotFoundException("Model not found");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

@Data
@Builder
class EmbeddingModelDefinition {
    private String embeddingModelClassName;
    private String embeddingModelName;
    private List<String> languages;
    private ProviderType providerType;
    private int dimensions;
    private int maxTokens;
}
