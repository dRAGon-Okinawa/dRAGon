package ai.dragon.controller.api.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.dragon.util.VersionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/app/info")
@Tag(name = "App Information", description = "Information about the dRAGon app.")
public class InfoAppApiController {
    @GetMapping("/version")
    @ApiResponse(responseCode = "200", description = "dRAGon app version.")
    @Operation(summary = "Get dRAGon app version", description = "Returns the version of the dRAGon app.")
    public String version() {
        return VersionUtil.getVersion();
    }
}
