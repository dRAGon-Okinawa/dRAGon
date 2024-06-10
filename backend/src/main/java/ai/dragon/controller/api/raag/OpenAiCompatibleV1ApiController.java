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

import ai.dragon.dto.openai.completion.OpenAiChatCompletionChoice;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiCompletionChoice;
import ai.dragon.dto.openai.completion.OpenAiCompletionMessage;
import ai.dragon.dto.openai.completion.OpenAiCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiCompletionUsage;
import ai.dragon.dto.openai.model.OpenAiModel;
import ai.dragon.dto.openai.model.OpenAiModelsReponse;
import ai.dragon.entity.SiloEntity;
import ai.dragon.repository.SiloRepository;
import ai.dragon.service.EmbeddingModelService;
import ai.dragon.service.EmbeddingStoreService;
import ai.dragon.service.SseService;
import ai.dragon.util.ai.AiAssistant;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStore;
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
    private EmbeddingStoreService embeddingStoreService;

    @Autowired
    private EmbeddingModelService embeddingModelService;

    @Autowired
    private SiloRepository siloRepository;

    @GetMapping("/models")
    @Operation(summary = "List models", description = "Lists available models.")
    public OpenAiModelsReponse models() {
        return OpenAiModelsReponse.builder()
                .data(List.of(OpenAiModel
                        .builder()
                        .created(System.currentTimeMillis() / 1000)
                        .id("dragon-ppx")
                        .owned_by("dRAGon")
                        .build()))
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
        if (Boolean.TRUE.equals(request.getStream())) {
            UUID siloUuid = UUID.fromString(request.getModel());
            SiloEntity silo = siloRepository.getByUuid(siloUuid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
            UUID emitterID = sseService.createEmitter();

            EmbeddingStore<TextSegment> embeddingStore = embeddingStoreService.retrieveEmbeddingStore(siloUuid);
            EmbeddingModel embeddingModel = embeddingModelService.modelForEntity(silo);
            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .dynamicMaxResults(query -> {
                        return 10;
                    })
                    .dynamicMinScore(query -> {
                        return 0.8;
                    })
                    .dynamicFilter(query -> {
                        return null;
                    })
                    .build();
            AiAssistant assistant = AiServices.builder(AiAssistant.class)
                    .streamingChatLanguageModel(OpenAiStreamingChatModel
                            .withApiKey("TODO"))
                    // .chatLanguageModel(
                    // OpenAiChatModel.withApiKey("TODO"))
                    // .chatMemory(MessageWindowChatMemory.withMaxMessages(10)) // it should
                    // remember 10 latest messages
                    // TODO : .retrievalAugmentor(retrievalAugmentor)
                    .contentRetriever(contentRetriever)
                    .build();
            TokenStream stream = assistant.chat((String) request.getMessages().get(0).getContent());

            stream.onNext(nextChunk -> {
                OpenAiChatCompletionResponse responseChunk = new OpenAiChatCompletionResponse();
                responseChunk.setId(emitterID.toString());
                responseChunk.setModel(request.getModel());
                responseChunk.setCreated(System.currentTimeMillis() / 1000);
                responseChunk.setObject("chat.completion.chunk");
                List<OpenAiChatCompletionChoice> choices = new ArrayList<>();
                choices.add(OpenAiChatCompletionChoice
                        .builder()
                        .index(0)
                        // .finish_reason(i == 2 ? "stop" : null)
                        .delta(OpenAiCompletionMessage
                                .builder()
                                .role("assistant")
                                .content(nextChunk)
                                .build())
                        .build());
                responseChunk.setChoices(choices);
                sseService.sendEvent(emitterID, responseChunk);
            })
                    .onComplete(response -> {
                        // response.finishReason(). // "stop"
                        sseService.sendEvent(emitterID, "[DONE]");
                        sseService.complete(emitterID);
                    })
                    .onError(Throwable::printStackTrace)
                    .start();
            return sseService.retrieveEmitter(emitterID);
        } else {
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
}
