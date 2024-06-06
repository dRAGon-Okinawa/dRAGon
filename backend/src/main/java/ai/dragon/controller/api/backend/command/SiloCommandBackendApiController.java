package ai.dragon.controller.api.backend.command;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.dragon.service.SiloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/backend/command/silo")
@Tag(name = "Silo Command", description = "Silo Command API Endpoints")
public class SiloCommandBackendApiController {
    @Autowired
    private SiloService siloService;

    @PostMapping("/rebuild/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Silo is being rebuilt.")
    @Operation(summary = "Rebuild Silo", description = "This will recompute the embeddings of the Silo.")
    public void export(@PathVariable("uuid") @Parameter(description = "Identifier of the Silo") UUID uuid) throws Exception {
        siloService.rebuildSilo(uuid);
    }
}
