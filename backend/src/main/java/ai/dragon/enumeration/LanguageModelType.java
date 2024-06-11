package ai.dragon.enumeration;

import ai.dragon.dto.llm.StreamingChatLanguageModelDefinition;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

public enum LanguageModelType {
    OpenAiModel("OpenAiModel");

    private String value;

    LanguageModelType(String value) {
        this.value = value;
    }

    public static LanguageModelType fromString(String text) {
        for (LanguageModelType b : LanguageModelType.values()) {
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

    public StreamingChatLanguageModelDefinition getStreamingChatLanguageModel() throws ClassNotFoundException {
        switch (this) {
            case OpenAiModel:
                return StreamingChatLanguageModelDefinition
                        .builder()
                        .modelWithSettings(parameters -> {
                            return OpenAiStreamingChatModel
                                    .builder()
                                    .apiKey(parameters.getApiKey())
                                    .modelName(parameters.getModelName())
                                    .build();
                        })
                        .providerType(ProviderType.OpenAI)
                        .build();
            default:
                throw new ClassNotFoundException("Model not found");
        }
    }
}
