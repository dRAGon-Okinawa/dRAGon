package ai.dragon.controller.api.backend;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.dragon.entity.SiloEntity;
import ai.dragon.repository.SiloRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/backend/silo")
@Tag(name = "Silo", description = "Silo Management API Endpoints")
public class SiloBackendApiController {
    @Autowired
    private SiloRepository siloRepository;

    @GetMapping("/")
    @ApiResponse(responseCode = "200", description = "List has been successfully retrieved.")
    @Operation(summary = "List all Silos", description = "Returns all Silo entities stored in the database.")
    public List<SiloEntity> list() {
        return siloRepository.find().toList();
    }

    @PostMapping("/")
    @ApiResponse(responseCode = "200", description = "Silo has been successfully created.")
    @Operation(summary = "Create a new Silo", description = "Creates one Silo entity in the database.")
    public SiloEntity create() {
        String siloName = String.format("Silo %s",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        SiloEntity silo = new SiloEntity();
        silo.setName(siloName);
        siloRepository.save(silo);
        return silo;
    }

    @GetMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Silo has been successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Silo not found.", content = @Content)
    @Operation(summary = "Retrieve one Silo", description = "Returns one Silo entity from its UUID stored in the database.")
    public SiloEntity get(@PathVariable("uuid") @Parameter(description = "Identifier of the Silo") String uuid) {
        return Optional.ofNullable(siloRepository.getByUuid(uuid))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Silo not found."));
    }

    @PatchMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Silo has been successfully created.")
    @ApiResponse(responseCode = "404", description = "Silo not found.", content = @Content)
    @Operation(summary = "Update a Silo", description = "Updates one Silo entity in the database.")
    public SiloEntity update(@PathVariable("uuid") @Parameter(description = "Identifier of the Silo", required = true) String uuid,
            SiloEntity silo) throws JsonMappingException {
        if (!siloRepository.exists(uuid) || silo == null || !uuid.equals(silo.getUuid().toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Silo not found");
        }

        SiloEntity siloToUpdate = siloRepository.getByUuid(uuid);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.updateValue(siloToUpdate, silo);
        siloRepository.save(siloToUpdate);

        return siloToUpdate;
    }

    @DeleteMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Silo has been successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Silo not found.", content = @Content)
    @Operation(summary = "Delete a Silo", description = "Deletes one Silo entity from its UUID stored in the database.")
    public void delete(@PathVariable("uuid") @Parameter(description = "Identifier of the Silo") String uuid) {
        if (!siloRepository.exists(uuid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Silo not found.");
        }
        siloRepository.delete(uuid);
    }
}
