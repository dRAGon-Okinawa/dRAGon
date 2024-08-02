package ai.dragon.controller.api.app;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.dragon.dto.api.GenericApiResponse;
import ai.dragon.dto.api.SuccessApiResponse;
import ai.dragon.dto.api.app.auth.LoginAuthAppApiData;
import ai.dragon.dto.api.app.auth.UserInfoAuthAppApiData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/app/auth")
@Tag(name = "Authentication", description = "Authentication API Endpoints")
public class AuthAppApiController {
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login to the application.")
    public GenericApiResponse login() {
        return SuccessApiResponse.builder().data(LoginAuthAppApiData
                .builder()
                .token("TOKEN_WILL_BE_HERE")
                .refreshToken("REFRESH_TOKEN_WILL_BE_HERE")
                .build())
                .build();
    }

    @GetMapping("/getUserInfo")
    @Operation(summary = "Get User Info", description = "Get user info.")
    public GenericApiResponse getUserInfo() {
        return SuccessApiResponse.builder().data(UserInfoAuthAppApiData
                .builder()
                .userId("TODO")
                .userName("dRAGon")
                .roles(List.of("R_SUPER"))
                .build())
                .build();
    }
}
