package ai.dragon.dto.api;

import ai.dragon.enumeration.ApiResponseCode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FailureApiResponse implements GenericApiResponse {
    @Builder.Default
    private Object data = null;

    @Builder.Default
    private String code = ApiResponseCode.INTERNAL_SERVER_ERROR.toString();

    @Builder.Default
    private String msg = "Internal Server Error";
}
