package ai.dragon.controller.api.backend.repository;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonMappingException;

import ai.dragon.dto.api.DataTableApiResponse;
import ai.dragon.dto.api.GenericApiResponse;
import ai.dragon.entity.SiloEntity;
import ai.dragon.repository.SiloRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/backend/repository/silo")
@Tag(name = "Silo Repository", description = "Silo Repository Management API Endpoints")
public class SiloBackendApiController extends AbstractCrudBackendApiController<SiloEntity> {
    @Autowired
    private SiloRepository siloRepository;

    @GetMapping("/")
    @ApiResponse(responseCode = "200", description = "List has been successfully retrieved.")
    @Operation(summary = "Search Silos", description = "Search Silo entities stored in the database.")
    public GenericApiResponse searchSilos(@RequestParam(name = "current", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return DataTableApiResponse.fromPager(super.page(siloRepository, page, size));
    }

    @PostMapping("/")
    @ApiResponse(responseCode = "200", description = "Silo has been successfully created.")
    @ApiResponse(responseCode = "409", description = "Constraint violation.", content = @Content)
    @Operation(summary = "Create a new Silo", description = "Creates one Silo entity in the database.")
    public SiloEntity createSilo() throws Exception {
        return super.create(siloRepository);
    }

    @GetMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Silo has been successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Silo not found.", content = @Content)
    @Operation(summary = "Retrieve one Silo", description = "Returns one Silo entity from its UUID stored in the database.")
    public SiloEntity getSilo(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Silo") String uuid) {
        return super.get(uuid, siloRepository);
    }

    @PatchMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Silo has been successfully updated.")
    @ApiResponse(responseCode = "404", description = "Silo not found.", content = @Content)
    @Operation(summary = "Update a Silo", description = "Updates one Silo entity in the database.")
    public SiloEntity updateSilo(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Silo", required = true) String uuid,
            @RequestBody Map<String, Object> fields) throws JsonMappingException {
        return super.update(uuid, fields, siloRepository);
    }

    @DeleteMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Silo has been successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Silo not found.", content = @Content)
    @Operation(summary = "Delete a Silo", description = "Deletes one Silo entity from its UUID stored in the database.")
    public void deleteSilo(@PathVariable("uuid") @Parameter(description = "Identifier of the Silo") UUID uuid)
            throws Exception {
        super.delete(uuid, siloRepository);
    }
}
