package ai.dragon.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public static String singleTextFrom(ChatMessage message) {
        StringBuilder sb = new StringBuilder();
        if (message instanceof UserMessage userMessage) {
            userMessage.contents().forEach(content -> {
                if (content instanceof TextContent textContent) {
                    sb.append(textContent.text());
                }
            });
        } else if (message instanceof AiMessage aiMessage) {
            sb.append(aiMessage.text());
        } else if (message instanceof SystemMessage systemMessage) {
            sb.append(systemMessage.text());
        }
        return sb.toString();
    }

    public static String singleTextFrom(OpenAiCompletionRequest request) {
        Object prompt = request.getPrompt();
        if (prompt instanceof String stringPrompt) {
            return stringPrompt;
        } else if (prompt instanceof List<?> listPrompt) {
            return listPrompt.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Optional<ChatMessage> convertToChatMessage(OpenAiCompletionMessage completionMessage) {
        ChatMessage chatMessage = switch (completionMessage.getRole()) {
            case "user" -> {
                if (completionMessage.getContent() instanceof String stringContent) {
                    yield new UserMessage(stringContent);
                } else {
                    List<Content> contents = contentsListFrom(
                            (List<Map<String, Object>>) completionMessage.getContent());
                    yield new UserMessage(contents);
                }
            }
            case "system" -> new SystemMessage((String) completionMessage.getContent());
            case "assistant" -> new AiMessage((String) completionMessage.getContent());
            default -> {
                LOGGER.error(String.format("Invalid Message Role '%s'", completionMessage.getRole()));
                yield null;
            }
        };
        return Optional.ofNullable(chatMessage);
    }

    private static List<Content> contentsListFrom(List<Map<String, Object>> content) {
        List<Content> contents = new ArrayList<>();
        if (content != null) {
            content.forEach(contentItem -> {
                String type = (String) contentItem.get("type");
                if (type == null) {
                    LOGGER.error("Content part must have a type field!");
                    return;
                }
                contents.add(createContent(type, contentItem));
            });
        }
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
