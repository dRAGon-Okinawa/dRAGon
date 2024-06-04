package ai.dragon.controller.api.ragapi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.dragon.dto.openai.completion.OpenAiChatCompletionChoice;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiCompletionChoice;
import ai.dragon.dto.openai.completion.OpenAiCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiCompletionUsage;
import ai.dragon.dto.openai.completion.OpenAiCompletionMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ragapi/v1")
@Tag(name = "Open AI Compatible", description = "Compatible Endpoints following Open AI API Format")
public class OpenAiCompatibleV1ApiController {
    @PostMapping("/completions")
    @Operation(summary = "Creates a completion", description = "Creates a completion for the provided prompt and parameters.")
    public OpenAiCompletionResponse completions(@Valid @RequestBody OpenAiCompletionRequest request) throws Exception {
        OpenAiCompletionResponse response = new OpenAiCompletionResponse();

        response.setId(UUID.randomUUID().toString());
        response.setModel(request.getModel());
        response.setCreated(System.currentTimeMillis() / 1000);
        response.setObject("text_completion");
        response.setUsage(OpenAiCompletionUsage
                .builder()
                .completion_tokens(0)
                .prompt_tokens(0)
                .total_tokens(0)
                .build());

        List<OpenAiCompletionChoice> choices = new ArrayList<>();
        choices.add(OpenAiCompletionChoice
                .builder()
                .index(0)
                .finish_reason("stop")
                .text("Hello, how can I help you today?")
                .build());

        response.setChoices(choices);

        return response;
    }

    @PostMapping("/chat/completions")
    @Operation(summary = "Creates a chat completion", description = "Creates a chat completion for the provided prompt and parameters.")
    public OpenAiChatCompletionResponse chatCompletions(@Valid @RequestBody OpenAiChatCompletionRequest request)
            throws Exception {
        OpenAiChatCompletionResponse response = new OpenAiChatCompletionResponse();

        response.setId(UUID.randomUUID().toString());
        response.setModel(request.getModel());
        response.setCreated(System.currentTimeMillis() / 1000);
        response.setObject("chat.completion");
        response.setUsage(OpenAiCompletionUsage
                .builder()
                .completion_tokens(0)
                .prompt_tokens(0)
                .total_tokens(0)
                .build());

        List<OpenAiChatCompletionChoice> choices = new ArrayList<>();
        choices.add(OpenAiChatCompletionChoice
                .builder()
                .index(0)
                .finish_reason("stop")
                .message(OpenAiCompletionMessage
                        .builder()
                        .role("assistant")
                        .content("Hello, how can I help you today?")
                        .build())
                .build());

        response.setChoices(choices);

        return response;
    }
}
