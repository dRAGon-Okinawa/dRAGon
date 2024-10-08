package ai.dragon.dto.api;

import ai.dragon.enumeration.ApiResponseCode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SuccessApiResponse implements GenericApiResponse {
    @Builder.Default
    private Object data = null;

    @Builder.Default
    private String code = ApiResponseCode.SUCCESS.toString();

    @Builder.Default
    private String msg = "OK";
}
