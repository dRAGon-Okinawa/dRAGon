package ai.dragon.controller.api.raag;

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
import ai.dragon.dto.openai.completion.OpenAiCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiCompletionResponse;
import ai.dragon.dto.openai.model.OpenAiModelsReponse;
import ai.dragon.entity.FarmEntity;
import ai.dragon.repository.FarmRepository;
import ai.dragon.service.RaagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/raag/v1")
@Tag(name = "RaaG", description = "RAG as a GPT : Compatible Endpoints following Open AI API Format")
public class OpenAiCompatibleV1ApiController {
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
    public Object completions(@Valid @RequestBody OpenAiCompletionRequest completionRequest,
            HttpServletRequest servletRequest)
            throws Exception {
        FarmEntity farm = farmRepository
                .findUniqueByFieldValue("raagIdentifier", completionRequest.getModel())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));
        return raagService.makeCompletionResponse(farm, completionRequest, servletRequest);
    }

    @PostMapping("/chat/completions")
    @Operation(summary = "Creates a chat completion", description = "Creates a chat completion for the provided prompt and parameters.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = OpenAiChatCompletionResponse.class)))
    public Object chatCompletions(@Valid @RequestBody OpenAiChatCompletionRequest chatCompletionRequest,
            HttpServletRequest servletRequest) throws Exception {
        FarmEntity farm = farmRepository
                .findUniqueByFieldValue("raagIdentifier", chatCompletionRequest.getModel())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));
        return raagService.makeChatCompletionResponse(farm, chatCompletionRequest, servletRequest);
    }
}
