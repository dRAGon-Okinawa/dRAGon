package ai.dragon.dto.llm;

import java.util.function.Function;

import ai.dragon.enumeration.ProviderType;
import ai.dragon.properties.embedding.LanguageModelSettings;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatLanguageModelDefinition {
    private Function<LanguageModelSettings, ChatLanguageModel> modelWithSettings;
    private ProviderType providerType;
}
