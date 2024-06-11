package ai.dragon.dto.llm;

import java.util.function.Function;

import ai.dragon.enumeration.ProviderType;
import ai.dragon.properties.embedding.LanguageModelSettings;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StreamingChatLanguageModelDefinition {
    private Function<LanguageModelSettings, StreamingChatLanguageModel> modelWithSettings;
    private ProviderType providerType;
}
