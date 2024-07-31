package ai.dragon.dto.api.app.auth;

import ai.dragon.dto.api.GenericApiData;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginAuthAppApiData implements GenericApiData {
    private String token;
    private String refreshToken;
}
