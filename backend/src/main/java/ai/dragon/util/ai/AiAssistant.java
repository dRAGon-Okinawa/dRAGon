package ai.dragon.util.ai;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;

public interface AiAssistant {
    Result<String> answer(String query);
    TokenStream chat(String message);
}
