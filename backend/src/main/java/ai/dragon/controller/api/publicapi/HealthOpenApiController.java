package ai.dragon.controller.api.publicapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/publicapi/health")
@Tag(name = "Health", description = "Health Check API Endpoints")
public class HealthOpenApiController {
    @GetMapping("/status")
    @ApiResponse(responseCode = "200", description = "dRAGon app is alive.")
    @Operation(summary = "Check dRAGon app health", description = "Returns a simple message to confirm that the app is alive.")
    public String health() {
        return "ALIVE";
    }
}
