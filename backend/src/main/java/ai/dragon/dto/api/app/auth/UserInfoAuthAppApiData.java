package ai.dragon.dto.api.app.auth;

import java.util.List;

import ai.dragon.dto.api.GenericApiData;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserInfoAuthAppApiData implements GenericApiData {
    private String userId;
    private String userName;
    private List<String> roles;
    private List<String> buttons;
}
