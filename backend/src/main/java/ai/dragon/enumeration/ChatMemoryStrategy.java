package ai.dragon.enumeration;

import java.util.Optional;

import ai.dragon.dto.llm.ChatMemoryStrategyDefinition;
import ai.dragon.properties.raag.RetrievalAugmentorSettings;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiTokenizer;

public enum ChatMemoryStrategy {
    MaxMessages("MaxMessages"),
    MaxTokens("MaxTokens");

    private String value;

    ChatMemoryStrategy(String value) {
        this.value = value;
    }

    public static ChatMemoryStrategy fromString(String text) {
        for (ChatMemoryStrategy b : ChatMemoryStrategy.values()) {
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

    public ChatMemoryStrategyDefinition getChatMemoryStrategyDefinition() throws ClassNotFoundException {
        switch (this) {
            case MaxMessages:
                return ChatMemoryStrategyDefinition
                        .builder()
                        .strategyWithSettings(parameters -> {
                            return MessageWindowChatMemory
                                    .builder()
                                    .maxMessages(Optional.ofNullable(parameters.getHistoryMaxMessages())
                                            .orElse(RetrievalAugmentorSettings.DEFAULT_MAX_MESSAGES))
                                    .build();
                        })
                        .build();
            case MaxTokens:
                return ChatMemoryStrategyDefinition
                        .builder()
                        .strategyWithSettings(parameters -> {
                            return TokenWindowChatMemory
                                    .builder()
                                    .maxTokens(
                                            Optional.ofNullable(parameters.getHistoryMaxTokens())
                                                    .orElse(RetrievalAugmentorSettings.DEFAULT_MAX_TOKENS),
                                            new OpenAiTokenizer())
                                    .build();
                        })
                        .build();
            default:
                throw new ClassNotFoundException("Strategy not found");
        }
    }
}
