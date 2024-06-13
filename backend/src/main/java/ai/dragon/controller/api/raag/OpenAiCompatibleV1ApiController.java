package ai.dragon.controller.api.raag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.dto.openai.completion.OpenAiChatCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiCompletionChoice;
import ai.dragon.dto.openai.completion.OpenAiCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiCompletionUsage;
import ai.dragon.dto.openai.model.OpenAiModelsReponse;
import ai.dragon.entity.FarmEntity;
import ai.dragon.repository.FarmRepository;
import ai.dragon.service.RaagService;
import ai.dragon.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/raag/v1")
@Tag(name = "RaaG", description = "RAG as a GPT : Compatible Endpoints following Open AI API Format")
public class OpenAiCompatibleV1ApiController {
    @Autowired
    private SseService sseService;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private RaagService raagService;

    @GetMapping("/models")
    @Operation(summary = "List models", description = "Lists available models.")
    public OpenAiModelsReponse models() {
        return OpenAiModelsReponse.builder()
                .data(raagService.listAvailableModels())
                .build();
    }

    @PostMapping("/completions")
    @Operation(summary = "Creates a completion", description = "Creates a completion for the provided prompt and parameters.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = OpenAiCompletionResponse.class)))
    public Object completions(@Valid @RequestBody OpenAiCompletionRequest request)
            throws Exception {
        if (Boolean.TRUE.equals(request.getStream())) {
            UUID emitterID = sseService.createEmitter();
            for (int i = 0; i < 3; i++) {
                OpenAiCompletionResponse responseChunk = new OpenAiCompletionResponse();
                responseChunk.setId(emitterID.toString());
                responseChunk.setModel(request.getModel());
                responseChunk.setCreated(System.currentTimeMillis() / 1000);
                responseChunk.setObject("text_completion");
                List<OpenAiCompletionChoice> choices = new ArrayList<>();
                choices.add(OpenAiCompletionChoice
                        .builder()
                        .index(0)
                        .finish_reason(i == 2 ? "stop" : null)
                        .text("Chunk : " + i + "\r\n")
                        .build());
                responseChunk.setChoices(choices);
                sseService.sendEvent(emitterID, responseChunk);
            }
            sseService.sendEvent(emitterID, "[DONE]");
            new Thread(() -> {
                sseService.complete(emitterID);
            }).start();
            return sseService.retrieveEmitter(emitterID);
        } else {
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
    }

    @PostMapping("/chat/completions")
    @Operation(summary = "Creates a chat completion", description = "Creates a chat completion for the provided prompt and parameters.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = OpenAiChatCompletionResponse.class)))
    public Object chatCompletions(@Valid @RequestBody OpenAiChatCompletionRequest request) throws Exception {
        FarmEntity farm = farmRepository
                .findUniqueByFieldValue("raagIdentifier", request.getModel())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));
        return raagService.chatCompletionResponse(farm, request, request.getStream());
    }
}
