package ai.dragon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ai.dragon.dto.openai.completion.OpenAiCompletionMessage;
import ai.dragon.dto.openai.completion.OpenAiCompletionRequest;
import ai.dragon.util.DataUrlUtil;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;

@Service
public class ChatMessageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String singleTextFrom(UserMessage message) {
        StringBuilder sb = new StringBuilder();
        message.contents().forEach(content -> {
            if (content instanceof TextContent) {
                sb.append(((TextContent) content).text());
            }
        });
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public String singleTextFrom(OpenAiCompletionRequest request) {
        Object prompt = request.getPrompt();
        if(prompt instanceof String) {
            return (String) prompt;
        } else if(prompt instanceof List) {
            List<String> promptList = (List<String>) prompt;
            StringBuilder sb = new StringBuilder();
            promptList.forEach(promptItem -> {
                sb.append(promptItem);
            });
            return sb.toString();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Optional<ChatMessage> convertToChatMessage(OpenAiCompletionMessage completionMessage) {
        ChatMessage chatMessage = null;
        switch (completionMessage.getRole()) {
            case "user":
                if (completionMessage.getContent() instanceof String) {
                    // TODO "UserMessage.name"
                    chatMessage = new UserMessage((String) completionMessage.getContent());
                } else {
                    List<Map<String, Object>> content = (List<Map<String, Object>>) completionMessage.getContent();
                    List<Content> contents = new ArrayList<>();
                    content.forEach(contentItem -> {
                        if (!contentItem.containsKey("type")) {
                            logger.error("Content part must have a type field!");
                            return;
                        }
                        String type = (String) contentItem.get("type");
                        if ("text".equals(type)) {
                            String text = (String) contentItem.get("text");
                            contents.add(new TextContent(text));
                        } else if ("image_url".equals(type)) {
                            Map<String, Object> imageURL = (Map<String, Object>) contentItem.get("image_url");
                            String url = (String) imageURL.get("url");
                            if (url.startsWith("http")) {
                                contents.add(new ImageContent(url));
                            } else if (url.startsWith("data:")) {
                                String mimetype = DataUrlUtil.getImageType(url);
                                String base64String = DataUrlUtil.getDataBytesString(url);
                                contents.add(ImageContent.from(base64String, mimetype));
                            }
                            // TODO String detail = (String) imageURL.get("detail");
                        }
                    });
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
                logger.error(String.format("Invalid Message Role '%s'", completionMessage.getRole()));
        }
        return Optional.ofNullable(chatMessage);
    }
}
