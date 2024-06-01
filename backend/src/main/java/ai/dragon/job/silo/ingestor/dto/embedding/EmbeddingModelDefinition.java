package ai.dragon.job.silo.ingestor.dto.embedding;

import java.util.List;
import java.util.function.Function;

import ai.dragon.enumeration.ProviderType;
import ai.dragon.properties.embedding.EmbeddingModelSettings;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmbeddingModelDefinition {
    private String embeddingModelClassName;
    private String embeddingModelName;
    private Function<EmbeddingModelSettings, EmbeddingModel> embeddingModelWithSettings;
    private List<String> languages;
    private ProviderType providerType;
    private int dimensions;
    private int maxTokens;
}
