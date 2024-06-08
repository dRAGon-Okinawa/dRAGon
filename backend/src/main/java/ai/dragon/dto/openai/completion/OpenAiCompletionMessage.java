package ai.dragon.dto.openai.completion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAiCompletionMessage {
    @NotNull
    @NotBlank
    private String role;

    @NotNull
    @NotBlank
    // https://platform.openai.com/docs/api-reference/chat/create
    // string or array
    // (string) The text contents of the message.
    // (array) : Array of content parts with a defined type :
    // -> type == string : 'text' field will contain the text
    // -> type == image_url : 'url' field will contain a URL or base64 encoded image
    private Object content;

    private String name;
}
