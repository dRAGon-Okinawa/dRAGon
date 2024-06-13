package ai.dragon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ai.dragon.dto.openai.completion.OpenAiChatCompletionChoice;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiCompletionMessage;
import ai.dragon.dto.openai.completion.OpenAiCompletionUsage;
import ai.dragon.dto.openai.model.OpenAiModel;
import ai.dragon.entity.FarmEntity;
import ai.dragon.properties.embedding.LanguageModelSettings;
import ai.dragon.repository.FarmRepository;
import ai.dragon.util.ai.AiAssistant;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStore;

@Service
public class RaagService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmbeddingStoreService embeddingStoreService;

    @Autowired
    private EmbeddingModelService embeddingModelService;

    @Autowired
    private SseService sseService;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private KVSettingService kvSettingService;

    @Autowired
    private ChatMessageService chatMessageService;

    public List<OpenAiModel> listAvailableModels() {
        return farmRepository
                .find()
                .toList()
                .stream()
                .map(farm -> {
                    return OpenAiModel
                            .builder()
                            .created(System.currentTimeMillis() / 1000)
                            .id(farm.getRaagIdentifier())
                            .owned_by("dRAGon RaaG")
                            .build();
                })
                .toList();
    }

    public Object chatCompletionResponse(FarmEntity farm, OpenAiChatCompletionRequest request, boolean stream)
            throws Exception {
        return Boolean.TRUE.equals(request.getStream()) ? this.streamChatCompletionResponse(farm, request)
                : this.chatCompletionResponse(farm, request);
    }

    public OpenAiChatCompletionResponse chatCompletionResponse(FarmEntity farm, OpenAiChatCompletionRequest request)
            throws Exception {
        AiAssistant assistant = this.makeAssistant(farm, request, false);
        OpenAiCompletionMessage lastCompletionMessage = request.getMessages().get(request.getMessages().size() - 1);
        UserMessage lastChatMessage = (UserMessage) chatMessageService.convertToChatMessage(lastCompletionMessage)
                .orElseThrow();
        Result<String> answer = assistant.answer(chatMessageService.singleTextFrom(lastChatMessage));
        return this.createChatCompletionResponse(answer);
    }

    public SseEmitter streamChatCompletionResponse(FarmEntity farm, OpenAiChatCompletionRequest request)
            throws Exception {
        AiAssistant assistant = this.makeAssistant(farm, request, true);
        OpenAiCompletionMessage lastCompletionMessage = request.getMessages().get(request.getMessages().size() - 1);
        UserMessage lastChatMessage = (UserMessage) chatMessageService.convertToChatMessage(lastCompletionMessage)
                .orElseThrow();
        TokenStream stream = assistant.chat(chatMessageService.singleTextFrom(lastChatMessage));
        UUID emitterID = sseService.createEmitter();
        stream
                .onNext(nextChunk -> {
                    sseService.sendEvent(emitterID,
                            this.createChatCompletionChunkResponse(emitterID, request, nextChunk, false));
                })
                .onComplete(response -> {
                    sseService.sendEvent(emitterID,
                            this.createChatCompletionChunkResponse(emitterID, request, "", true));
                    sseService.sendEvent(emitterID, "[DONE]");
                    sseService.complete(emitterID);
                })
                .onError(Throwable::printStackTrace)
                .start();
        return sseService.retrieveEmitter(emitterID);
    }

    private AiAssistant makeAssistant(FarmEntity farm, OpenAiChatCompletionRequest request, boolean stream)
            throws Exception {
        AiServices<AiAssistant> assistantBuilder = AiServices.builder(AiAssistant.class)
                .retrievalAugmentor(this.buildRetrievalAugmentor(farm))
                .chatMemory(this.buildChatMemory(request));
        if (stream) {
            assistantBuilder.streamingChatLanguageModel(this.buildStreamingChatLanguageModel(farm));
        } else {
            assistantBuilder.chatLanguageModel(this.buildChatLanguageModel(farm));
        }
        return assistantBuilder.build();
    }

    private MessageWindowChatMemory buildChatMemory(OpenAiChatCompletionRequest request) {
        MessageWindowChatMemory memory = MessageWindowChatMemory.withMaxMessages(10); // TODO maxMessages
        for (int i = 0; i < request.getMessages().size(); i++) {
            OpenAiCompletionMessage requestMessage = request.getMessages().get(i);
            chatMessageService.convertToChatMessage(requestMessage).ifPresent(memory::add);
        }
        return memory;
    }

    private OpenAiChatCompletionResponse createChatCompletionResponse(Result<String> answer) {
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

    private OpenAiChatCompletionResponse createChatCompletionChunkResponse(
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

    private StreamingChatLanguageModel buildStreamingChatLanguageModel(FarmEntity farm) throws Exception {
        return farm
                .getLanguageModel()
                .getStreamingChatLanguageModel()
                .getModelWithSettings()
                .apply(kvSettingService
                        .kvSettingsToObject(farm.getLanguageModelSettings(),
                                LanguageModelSettings.class));
    }

    private ChatLanguageModel buildChatLanguageModel(FarmEntity farm) throws Exception {
        return farm
                .getLanguageModel()
                .getChatLanguageModel()
                .getModelWithSettings()
                .apply(kvSettingService
                        .kvSettingsToObject(farm.getLanguageModelSettings(),
                                LanguageModelSettings.class));
    }

    private RetrievalAugmentor buildRetrievalAugmentor(FarmEntity farm) {
        // TODO Enhanced Query Router : langchain4j => LanguageModelQueryRouter
        return DefaultRetrievalAugmentor.builder()
                .queryRouter(new DefaultQueryRouter(this.buildRetrieverList(farm)))
                .build();
    }

    public List<ContentRetriever> buildRetrieverList(FarmEntity farm) {
        List<ContentRetriever> retrievers = new ArrayList<>();
        if (farm.getSilos() == null || farm.getSilos().isEmpty()) {
            logger.warn("No Silos found for Farm '{}' (RaaG Identifier '{}'), no content retrieve will be made",
                    farm.getUuid(), farm.getRaagIdentifier());
            return retrievers;
        }
        farm.getSilos().forEach(siloUuid -> {
            try {
                this.buildRetriever(siloUuid).ifPresent(retrievers::add);
            } catch (Exception ex) {
                logger.error("Error building Content Retriever for Silo '{}'", siloUuid, ex);
            }
        });
        return retrievers;
    }

    public Optional<ContentRetriever> buildRetriever(UUID siloUuid) throws Exception {
        EmbeddingModel embeddingModel = embeddingModelService.modelForSilo(siloUuid);
        EmbeddingStore<TextSegment> embeddingStore = embeddingStoreService.retrieveEmbeddingStore(siloUuid);
        return Optional.of(EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .dynamicMaxResults(query -> {
                    return 10; // TODO SiloEntity or FarmEntity settings
                })
                .dynamicMinScore(query -> {
                    return 0.8; // TODO SiloEntity or FarmEntity settings
                })
                .dynamicFilter(query -> {
                    return null; // TODO SiloEntity or FarmEntity settings
                })
                .build());
    }
}
