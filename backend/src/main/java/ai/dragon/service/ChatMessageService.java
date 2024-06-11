package ai.dragon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ai.dragon.dto.openai.completion.OpenAiCompletionMessage;
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

    public Optional<ChatMessage> convertToChatMessage(OpenAiCompletionMessage completionMessage) {
        ChatMessage chatMessage;
        switch (completionMessage.getRole()) {
            case "user":
                if (completionMessage.getContent() instanceof String) {
                    chatMessage = new UserMessage(completionMessage.getName(), (String) completionMessage.getContent());
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
                            // TODO String detail = (String) imageURL.get("detail");
                            contents.add(new ImageContent(url));
                        }
                    });
                    chatMessage = new UserMessage(completionMessage.getName(), contents);
                }
                break;
            case "system":
                chatMessage = new SystemMessage((String) completionMessage.getContent());
            case "assistant":
                chatMessage = new AiMessage((String) completionMessage.getContent());
            default:
                throw new IllegalArgumentException("Invalid Message Role: " + completionMessage.getRole());
        }
        return Optional.ofNullable(chatMessage);
    }
}
