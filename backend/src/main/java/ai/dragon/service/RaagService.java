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

import ai.dragon.dto.embedding.store.EmbeddingStoreSearchRequest;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiChatCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiCompletionMessage;
import ai.dragon.dto.openai.completion.OpenAiCompletionRequest;
import ai.dragon.dto.openai.completion.OpenAiCompletionResponse;
import ai.dragon.dto.openai.completion.OpenAiRequest;
import ai.dragon.dto.openai.model.OpenAiModel;
import ai.dragon.entity.FarmEntity;
import ai.dragon.properties.embedding.LanguageModelSettings;
import ai.dragon.properties.raag.RetrievalAugmentorSettings;
import ai.dragon.repository.FarmRepository;
import ai.dragon.util.KVSettingUtil;
import ai.dragon.util.ai.AiAssistant;
import ai.dragon.util.spel.MetadataHeaderFilterExpressionParserUtil;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.DefaultRetrievalAugmentor.DefaultRetrievalAugmentorBuilder;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.servlet.http.HttpServletRequest;

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
    private ChatMessageService chatMessageService;

    @Autowired
    private OpenAiCompletionService openAiCompletionService;

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

    public Object makeCompletionResponse(FarmEntity farm, OpenAiCompletionRequest compleationRequest,
            HttpServletRequest servletRequest) throws Exception {
        return Boolean.TRUE.equals(compleationRequest.getStream())
                ? this.streamCompletionResponse(farm, compleationRequest, servletRequest)
                : this.completionResponse(farm, compleationRequest, servletRequest);
    }

    public Object makeChatCompletionResponse(FarmEntity farm, OpenAiChatCompletionRequest chatCompletionRequest,
            HttpServletRequest servletRequest) throws Exception {
        return Boolean.TRUE.equals(chatCompletionRequest.getStream())
                ? this.streamChatCompletionResponse(farm, chatCompletionRequest, servletRequest)
                : this.chatCompletionResponse(farm, chatCompletionRequest, servletRequest);
    }

    public List<ContentRetriever> buildRetrieverList(FarmEntity farm, HttpServletRequest servletRequest) {
        List<ContentRetriever> retrievers = new ArrayList<>();
        if (farm.getSilos() == null || farm.getSilos().isEmpty()) {
            logger.info("No Silos found for Farm '{}' (RaaG Identifier '{}'), no content retrieve will be made",
                    farm.getUuid(), farm.getRaagIdentifier());
            return retrievers;
        }
        farm.getSilos().forEach(siloUuid -> {
            try {
                this.buildRetriever(siloUuid, servletRequest).ifPresent(retrievers::add);
            } catch (Exception ex) {
                logger.error("Error building Content Retriever for Silo '{}'", siloUuid, ex);
            }
        });
        return retrievers;
    }

    public Optional<ContentRetriever> buildRetriever(UUID siloUuid, HttpServletRequest servletRequest)
            throws Exception {
        EmbeddingModel embeddingModel = embeddingModelService.modelForSilo(siloUuid);
        EmbeddingStore<TextSegment> embeddingStore = embeddingStoreService.retrieveEmbeddingStore(siloUuid);
        return Optional.of(EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .dynamicMaxResults(query -> {
                    return EmbeddingStoreSearchRequest.DEFAULT_MAX_RESULTS; // TODO SiloEntity or FarmEntity settings
                })
                .dynamicMinScore(query -> {
                    return EmbeddingStoreSearchRequest.DEFAULT_MIN_SCORE; // TODO SiloEntity or FarmEntity settings
                })
                .dynamicFilter(query -> {
                    // TODO Initial make Filter from : SiloEntity or FarmEntity settings
                    // TODO Then overwrite by header if provided :
                    return MetadataHeaderFilterExpressionParserUtil
                            .parse(servletRequest.getHeader("X-RAG-FILTER-METADATA"));
                })
                .build());
    }

    private OpenAiCompletionResponse completionResponse(FarmEntity farm, OpenAiCompletionRequest completionRequest,
            HttpServletRequest servletRequest)
            throws Exception {
        AiAssistant assistant = this.makeCompletionAssistant(farm, completionRequest, servletRequest, false);
        Result<String> answer = assistant.answer(chatMessageService.singleTextFrom(completionRequest));
        return openAiCompletionService.createCompletionResponse(completionRequest, answer);
    }

    private SseEmitter streamCompletionResponse(FarmEntity farm, OpenAiCompletionRequest completionRequest,
            HttpServletRequest servletRequest) throws Exception {
        AiAssistant assistant = this.makeCompletionAssistant(farm, completionRequest, servletRequest, true);
        TokenStream stream = assistant.chat(chatMessageService.singleTextFrom(completionRequest));
        UUID emitterID = sseService.createEmitter();
        stream
                .onNext(nextChunk -> {
                    sseService.sendEvent(emitterID,
                            openAiCompletionService.createCompletionChunkResponse(emitterID, completionRequest,
                                    nextChunk,
                                    false));
                })
                .onComplete(response -> {
                    sseService.sendEvent(emitterID,
                            openAiCompletionService.createCompletionChunkResponse(emitterID, completionRequest, "",
                                    true));
                    sseService.sendEvent(emitterID, "[DONE]");
                    sseService.complete(emitterID);
                })
                .onError(Throwable::printStackTrace)
                .start();
        return sseService.retrieveEmitter(emitterID);
    }

    private OpenAiChatCompletionResponse chatCompletionResponse(FarmEntity farm,
            OpenAiChatCompletionRequest chatCompletionRequest, HttpServletRequest servletRequest)
            throws Exception {
        AiAssistant assistant = this.makeChatAssistant(farm, chatCompletionRequest, servletRequest, false);
        OpenAiCompletionMessage lastCompletionMessage = chatCompletionRequest.getMessages()
                .get(chatCompletionRequest.getMessages().size() - 1);
        ChatMessage lastChatMessage = chatMessageService.convertToChatMessage(lastCompletionMessage)
                .orElseThrow();
        Result<String> answer = assistant.answer(chatMessageService.singleTextFrom(lastChatMessage));
        return openAiCompletionService.createChatCompletionResponse(answer);
    }

    private SseEmitter streamChatCompletionResponse(FarmEntity farm, OpenAiChatCompletionRequest chatCompletionRequest,
            HttpServletRequest servletRequest)
            throws Exception {
        AiAssistant assistant = this.makeChatAssistant(farm, chatCompletionRequest, servletRequest, true);
        OpenAiCompletionMessage lastCompletionMessage = chatCompletionRequest.getMessages()
                .get(chatCompletionRequest.getMessages().size() - 1);
        ChatMessage lastChatMessage = chatMessageService.convertToChatMessage(lastCompletionMessage)
                .orElseThrow();
        TokenStream stream = assistant.chat(chatMessageService.singleTextFrom(lastChatMessage));
        UUID emitterID = sseService.createEmitter();
        stream
                .onNext(nextChunk -> {
                    sseService.sendEvent(emitterID,
                            openAiCompletionService.createChatCompletionChunkResponse(emitterID, chatCompletionRequest,
                                    nextChunk,
                                    false));
                })
                .onComplete(response -> {
                    sseService.sendEvent(emitterID,
                            openAiCompletionService.createChatCompletionChunkResponse(emitterID, chatCompletionRequest,
                                    "", true));
                    sseService.sendEvent(emitterID, "[DONE]");
                    sseService.complete(emitterID);
                })
                .onError(Throwable::printStackTrace)
                .start();
        return sseService.retrieveEmitter(emitterID);
    }

    private AiAssistant makeChatAssistant(FarmEntity farm, OpenAiChatCompletionRequest chatCompletionRequest,
            HttpServletRequest servletRequest, boolean stream)
            throws Exception {
        AiServices<AiAssistant> assistantBuilder = this.makeAssistantBuilder(farm, chatCompletionRequest,
                servletRequest, stream);
        assistantBuilder.chatMemory(this.buildChatMemory(farm, chatCompletionRequest));
        if (stream) {
            assistantBuilder
                    .streamingChatLanguageModel(this.buildStreamingChatLanguageModel(farm, chatCompletionRequest));
        } else {
            assistantBuilder.chatLanguageModel(this.buildChatLanguageModel(farm, chatCompletionRequest));
        }
        return assistantBuilder.build();
    }

    private AiAssistant makeCompletionAssistant(FarmEntity farm, OpenAiCompletionRequest completionRequest,
            HttpServletRequest servletRequest, boolean stream)
            throws Exception {
        return this.makeAssistantBuilder(farm, completionRequest, servletRequest, stream).build();
    }

    private AiServices<AiAssistant> makeAssistantBuilder(FarmEntity farm, OpenAiRequest openAiRequest,
            HttpServletRequest servletRequest, boolean stream)
            throws Exception {
        AiServices<AiAssistant> assistantBuilder = AiServices.builder(AiAssistant.class);
        this.buildRetrievalAugmentor(assistantBuilder, farm, openAiRequest, servletRequest);
        if (stream) {
            assistantBuilder.streamingChatLanguageModel(this.buildStreamingChatLanguageModel(farm, openAiRequest));
        } else {
            assistantBuilder.chatLanguageModel(this.buildChatLanguageModel(farm, openAiRequest));
        }
        return assistantBuilder;
    }

    private ChatMemory buildChatMemory(FarmEntity farm, OpenAiChatCompletionRequest request) throws Exception {
        RetrievalAugmentorSettings retrievalSettings = KVSettingUtil.kvSettingsToObject(
                farm.getRetrievalAugmentorSettings(),
                RetrievalAugmentorSettings.class);
        ChatMemory memory = farm.getChatMemoryStrategy()
                .getChatMemoryStrategyDefinition()
                .getStrategyWithSettings()
                .apply(retrievalSettings);
        for (int i = 0; i < request.getMessages().size(); i++) {
            OpenAiCompletionMessage requestMessage = request.getMessages().get(i);
            chatMessageService.convertToChatMessage(requestMessage).ifPresent(memory::add);
        }
        return memory;
    }

    private StreamingChatLanguageModel buildStreamingChatLanguageModel(FarmEntity farm, OpenAiRequest request)
            throws Exception {
        return farm
                .getLanguageModel()
                .getStreamingChatLanguageModel()
                .getModelWithSettings()
                .apply(buildLanguageModelSettings(farm, request));
    }

    private ChatLanguageModel buildChatLanguageModel(FarmEntity farm, OpenAiRequest request) throws Exception {
        return farm
                .getLanguageModel()
                .getChatLanguageModel()
                .getModelWithSettings()
                .apply(buildLanguageModelSettings(farm, request));
    }

    private LanguageModelSettings buildLanguageModelSettings(FarmEntity farm, OpenAiRequest request) {
        LanguageModelSettings languageModelSettings = KVSettingUtil
                .kvSettingsToObject(farm.getLanguageModelSettings(),
                        LanguageModelSettings.class);
        languageModelSettings.setMaxTokens(
                Optional.ofNullable(request.getMax_tokens()).orElse(languageModelSettings.getMaxTokens()));
        languageModelSettings.setTemperature(
                Optional.ofNullable(request.getTemperature()).orElse(languageModelSettings.getTemperature()));
        languageModelSettings.setUserIdentifier(
                Optional.ofNullable(request.getUser()).orElse(languageModelSettings.getUserIdentifier()));
        return languageModelSettings;
    }

    private void buildRetrievalAugmentor(AiServices<AiAssistant> assistantBuilder, FarmEntity farm,
            OpenAiRequest openAiRequest, HttpServletRequest servletRequest) throws Exception {
        RetrievalAugmentorSettings retrievalSettings = KVSettingUtil.kvSettingsToObject(
                farm.getRetrievalAugmentorSettings(),
                RetrievalAugmentorSettings.class);
        // TODO Enhanced Query Router : langchain4j => LanguageModelQueryRouter
        DefaultRetrievalAugmentorBuilder retrievalAugmentorBuilder = DefaultRetrievalAugmentor.builder();
        List<ContentRetriever> retrievers = this.buildRetrieverList(farm, servletRequest);
        if (retrievers != null && !retrievers.isEmpty()) {
            retrievalAugmentorBuilder.queryRouter(new DefaultQueryRouter(retrievers));
            if (Boolean.TRUE.equals(retrievalSettings.getRewriteQuery())
                    && openAiRequest instanceof OpenAiChatCompletionRequest) {
                // Query Rewriting => Improve RAG Performance and Accuracy
                // => Uses Chat History.
                retrievalAugmentorBuilder.queryTransformer(CompressingQueryTransformer.builder()
                        .chatLanguageModel(this.buildChatLanguageModel(farm, openAiRequest)).build());
            }
            assistantBuilder.retrievalAugmentor(retrievalAugmentorBuilder.build());
        }
    }
}
