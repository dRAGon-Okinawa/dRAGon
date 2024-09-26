package ai.dragon.dto.api;

import ai.dragon.enumeration.ApiResponseCode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DuplicatesApiResponse implements GenericApiResponse {
    @Builder.Default
    private Object data = null;

    @Builder.Default
    private String code = ApiResponseCode.DUPLICATES.toString();

    @Builder.Default
    private String msg = "Unique Constraint Exception";
}
