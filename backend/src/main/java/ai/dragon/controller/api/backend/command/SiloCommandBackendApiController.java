package ai.dragon.controller.api.backend.command;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.entity.SiloEntity;
import ai.dragon.repository.SiloRepository;
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

    @Autowired
    private SiloRepository siloRepository;

    @PostMapping("/rebuild/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Silo is being rebuilt.")
    @Operation(summary = "Rebuild Silo", description = "This will erase all embeddings of the Silo and re-ingest all documents.")
    public void rebuildSilo(@PathVariable("uuid") @Parameter(description = "Identifier of the Silo") UUID uuid) throws Exception {
        SiloEntity silo = siloRepository.getByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Silo not found."));
        siloService.rebuildSilo(silo);
    }

    @PostMapping("/ingest/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Silo Ingestor is now running.")
    @Operation(summary = "Start Silo Ingestor", description = "This will re-ingest the documents of the Silo.")
    public void startSiloIngestor(@PathVariable("uuid") @Parameter(description = "Identifier of the Silo") UUID uuid) throws Exception {
        SiloEntity silo = siloRepository.getByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Silo not found."));
        siloService.startSiloIngestor(silo);
    }
}
