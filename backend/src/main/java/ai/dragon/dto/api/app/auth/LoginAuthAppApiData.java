package ai.dragon.dto.api.app.auth;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginAuthAppApiData {
    private String token;
    private String refreshToken;
}
