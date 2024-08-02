package ai.dragon.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.dragon.dto.openai.completion.OpenAiCompletionMessage;
import ai.dragon.dto.openai.completion.OpenAiCompletionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;

public class ChatMessageUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageUtil.class);

    private ChatMessageUtil() {
    }

    @SuppressWarnings("unchecked")
    public static String singleTextFrom(ChatMessage message) {
        StringBuilder sb = new StringBuilder();
        Object contents = null;
        if (message instanceof UserMessage userMessage) {
            contents = userMessage.contents();
        } else if (message instanceof AiMessage aiMessage) {
            contents = aiMessage.text();
        } else if (message instanceof SystemMessage systemMessage) {
            contents = systemMessage.text();
        }
        if (contents == null) {
            return sb.toString();
        }
        if (contents instanceof String stringContent) {
            return stringContent;
        } else if (contents instanceof List listContent) {
            listContent.forEach(content -> {
                if (content instanceof TextContent textContent) {
                    sb.append(textContent.text());
                }
            });
            return sb.toString();
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static String singleTextFrom(OpenAiCompletionRequest request) {
        Object prompt = request.getPrompt();
        if (prompt instanceof String stringPrompt) {
            return stringPrompt;
        } else if (prompt instanceof List listPrompt) {
            StringBuilder sb = new StringBuilder();
            listPrompt.forEach(promptItem -> {
                sb.append(promptItem);
            });
            return sb.toString();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Optional<ChatMessage> convertToChatMessage(OpenAiCompletionMessage completionMessage) {
        ChatMessage chatMessage = null;
        switch (completionMessage.getRole()) {
            case "user":
                if (completionMessage.getContent() instanceof String stringContent) {
                    // TODO "UserMessage.name"
                    chatMessage = new UserMessage(stringContent);
                } else {
                    List<Content> contents = contentsListFrom(
                            (List<Map<String, Object>>) completionMessage.getContent());
                    // TODO "UserMessage.name"
                    chatMessage = new UserMessage(contents);
                }
                break;
            case "system":
                chatMessage = new SystemMessage((String) completionMessage.getContent());
                break;
            case "assistant":
                chatMessage = new AiMessage((String) completionMessage.getContent());
                break;
            default:
                LOGGER.error(String.format("Invalid Message Role '%s'", completionMessage.getRole()));
        }
        return Optional.ofNullable(chatMessage);
    }

    private static List<Content> contentsListFrom(List<Map<String, Object>> content) {
        List<Content> contents = new ArrayList<>();
        content.forEach(contentItem -> {
            String type = (String) contentItem.get("type");
            if (type == null) {
                LOGGER.error("Content part must have a type field!");
                return;
            }
            contents.add(createContent(type, contentItem));
        });
        return contents;
    }

    @SuppressWarnings("unchecked")
    private static Content createContent(String type, Map<String, Object> contentItem) {
        switch (type) {
            case "text":
                return new TextContent((String) contentItem.get("text"));
            case "image_url":
                return createImageContent((Map<String, Object>) contentItem.get("image_url"));
            default:
                return null;
        }
    }

    private static Content createImageContent(Map<String, Object> imageURL) {
        String url = (String) imageURL.get("url");
        if (url.startsWith("http")) {
            return new ImageContent(url);
        } else if (url.startsWith("data:")) {
            String mimetype = DataUrlUtil.getImageType(url);
            String base64String = DataUrlUtil.getDataBytesString(url);
            return ImageContent.from(base64String, mimetype);
        }
        // TODO ImageURL.detail
        return null;
    }
}
