package ai.dragon.dto.llm;

import java.util.function.Function;

import ai.dragon.properties.raag.RetrievalAugmentorSettings;
import dev.langchain4j.memory.ChatMemory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMemoryStrategyDefinition {
    private Function<RetrievalAugmentorSettings, ChatMemory> strategyWithSettings;
}
