package ai.dragon.util.transformer;

import ai.dragon.util.ChatMessageUtil;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;

public class EnhancedCompressingQueryTransformer extends CompressingQueryTransformer {
    public EnhancedCompressingQueryTransformer(ChatLanguageModel chatLanguageModel) {
        super(chatLanguageModel, DEFAULT_PROMPT_TEMPLATE);
    }

    public EnhancedCompressingQueryTransformer(ChatLanguageModel chatLanguageModel, PromptTemplate promptTemplate) {
        super(chatLanguageModel, promptTemplate);
    }

    // Apply override as long as [protected String format(ChatMessage message)] :
    // langchain4j-core/src/main/java/dev/langchain4j/rag/query/transformer/CompressingQueryTransformer.java#L90
    // While langchain4j using deprecated [message.text()] to get the text of the message.
    @Override
    protected String format(ChatMessage message) {
        if (message instanceof UserMessage userMessage) {
            return "User: " + ChatMessageUtil.singleTextFrom(userMessage);
        } else if (message instanceof AiMessage aiMessage) {
            if (aiMessage.hasToolExecutionRequests()) {
                return null;
            }
            return "AI: " + aiMessage.text();
        } else {
            return null;
        }
    }
}
