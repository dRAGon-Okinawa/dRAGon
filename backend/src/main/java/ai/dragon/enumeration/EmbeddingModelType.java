package ai.dragon.enumeration;

import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
                        .newInstance()
                        .embeddingModelClassName(
                                "dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15QuantizedEmbeddingModel")
                        .embeddingModelName("BgeSmallEnV15QuantizedEmbeddingModel")
                        .providerType(ProviderType.ONNX);
            case OpenAiEmbeddingAda002Model:
                return EmbeddingModelDefinition
                        .newInstance()
                        .embeddingModelClassName("dev.langchain4j.model.openai.OpenAiEmbeddingModel")
                        .embeddingModelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002.toString())
                        .providerType(ProviderType.OpenAI);
            case OpenAiEmbedding3SmallModel:
                return EmbeddingModelDefinition
                        .newInstance()
                        .embeddingModelClassName("dev.langchain4j.model.openai.OpenAiEmbeddingModel")
                        .embeddingModelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.toString())
                        .providerType(ProviderType.OpenAI);
            case OpenAiEmbedding3LargeModel:
                return EmbeddingModelDefinition
                        .newInstance()
                        .embeddingModelClassName("dev.langchain4j.model.openai.OpenAiEmbeddingModel")
                        .embeddingModelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_LARGE.toString())
                        .providerType(ProviderType.OpenAI);
            default:
                throw new ClassNotFoundException("Model not found");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

@Getter
@Setter
@Accessors(fluent = true)
class EmbeddingModelDefinition {
    private String embeddingModelClassName;
    private String embeddingModelName;
    private ProviderType providerType;

    private EmbeddingModelDefinition() {
    }

    public static EmbeddingModelDefinition newInstance() {
        return new EmbeddingModelDefinition();
    }
}