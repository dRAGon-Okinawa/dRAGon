package ai.dragon.dto.api;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SuccessApiResponse implements GenericApiResponse {
    @Builder.Default
    private GenericApiData data = null;

    @Builder.Default
    private String code = "0000";

    @Builder.Default
    private String msg = "OK";
}
