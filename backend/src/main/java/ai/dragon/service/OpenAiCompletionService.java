package ai.dragon.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import ai.dragon.dto.openai.completion.OpenAiChatCompletionChoice;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiCompletionChoice;
import ai.dragon.dto.openai.completion.OpenAiCompletionMessage;
import ai.dragon.dto.openai.completion.OpenAiCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiCompletionUsage;
import dev.langchain4j.service.Result;

@Service
public class OpenAiCompletionService {
    public OpenAiCompletionResponse createCompletionResponse(OpenAiCompletionRequest request, Result<String> answer) {
        return OpenAiCompletionResponse
                .builder()
                .id(UUID.randomUUID().toString())
                .model(request.getModel())
                .created(System.currentTimeMillis() / 1000)
                .object("text_completion")
                .usage(OpenAiCompletionUsage
                        .builder()
                        .completion_tokens(0)
                        .prompt_tokens(0)
                        .total_tokens(0)
                        .build())
                .choices(List.of(OpenAiCompletionChoice
                        .builder()
                        .index(0)
                        .finish_reason("stop")
                        .text(answer.content())
                        .build()))
                .build();
    }

    public OpenAiCompletionResponse createCompletionChunkResponse(
            UUID emitterID,
            OpenAiCompletionRequest request,
            String nextChunk,
            boolean isLastChunk) {
        return OpenAiCompletionResponse
                .builder()
                .id(emitterID.toString())
                .model(request.getModel())
                .created(System.currentTimeMillis() / 1000)
                .object("text_completion")
                .choices(List.of(OpenAiCompletionChoice
                        .builder()
                        .index(0)
                        .finish_reason(isLastChunk ? "stop" : null)
                        .text(nextChunk)
                        .build()))
                .build();
    }

    public OpenAiChatCompletionResponse createChatCompletionResponse(Result<String> answer) {
        return OpenAiChatCompletionResponse
                .builder()
                .id(UUID.randomUUID().toString())
                .created(System.currentTimeMillis() / 1000)
                .object("chat.completion")
                .usage(OpenAiCompletionUsage
                        .builder()
                        .completion_tokens(0)
                        .prompt_tokens(0)
                        .total_tokens(0)
                        .build())
                .choices(List.of(OpenAiChatCompletionChoice
                        .builder()
                        .index(0)
                        .finish_reason("stop")
                        .message(OpenAiCompletionMessage
                                .builder()
                                .role("assistant")
                                .content(answer.content())
                                .build())
                        .build()))
                .build();
    }

    public OpenAiChatCompletionResponse createChatCompletionChunkResponse(
            UUID emitterID,
            OpenAiChatCompletionRequest request,
            String nextChunk,
            boolean isLastChunk) {
        return OpenAiChatCompletionResponse
                .builder()
                .id(emitterID.toString())
                .model(request.getModel())
                .created(System.currentTimeMillis() / 1000)
                .object("chat.completion.chunk")
                .choices(List.of(OpenAiChatCompletionChoice
                        .builder()
                        .index(0)
                        .finish_reason(isLastChunk ? "stop" : null)
                        .delta(OpenAiCompletionMessage
                                .builder()
                                .role("assistant")
                                .content(nextChunk)
                                .build())
                        .build()))
                .build();
    }
}
