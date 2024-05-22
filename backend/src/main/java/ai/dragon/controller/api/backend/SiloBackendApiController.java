package ai.dragon.controller.api.backend;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ai.dragon.model.SiloEntity;
import ai.dragon.repository.SiloRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/backend/silo")
@Tag(name = "Silo", description = "Silo Management API Endpoints")
public class SiloBackendApiController {
    @Autowired
    private SiloRepository siloRepository;

    @GetMapping("/list")
    @ApiResponse(responseCode = "200", description = "List has been successfully retrieved.")
    @Operation(summary = "List all Silos", description = "Returns all Silo entities stored in the database.")
    public List<SiloEntity> list() {
        return siloRepository.find().toList();
    }

    @GetMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Silo has been successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Silo not found.", content = @Content)
    @Operation(summary = "Retrieve one Silo", description = "Returns one Silo entity from its UUID stored in the database.")
    public SiloEntity get(@PathVariable("uuid") String uuid) {
        return Optional.ofNullable(siloRepository.getByUuid(uuid))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Silo not found."));
    }
}
