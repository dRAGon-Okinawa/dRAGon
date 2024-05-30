package ai.dragon.controller.api.backendapi.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonMappingException;

import ai.dragon.entity.FarmEntity;
import ai.dragon.repository.FarmRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/backendapi/repository/farm")
@Tag(name = "Farm Repository", description = "Farm Repository Management API Endpoints")
public class FarmBackendApiController extends AbstractCrudBackendApiController<FarmEntity> {
    @Autowired
    private FarmRepository farmRepository;

    @GetMapping("/")
    @ApiResponse(responseCode = "200", description = "List has been successfully retrieved.")
    @Operation(summary = "List all Farms", description = "Returns all Farm entities stored in the database.")
    public List<FarmEntity> list() {
        return super.list(farmRepository);
    }

    @PostMapping("/")
    @ApiResponse(responseCode = "200", description = "Farm has been successfully created.")
    @ApiResponse(responseCode = "409", description = "Constraint violation.", content = @Content)
    @Operation(summary = "Create a new Farm", description = "Creates one Farm entity in the database.")
    public FarmEntity create() throws Exception {
        return super.create(farmRepository);
    }

    @GetMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Farm has been successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Farm not found.", content = @Content)
    @Operation(summary = "Retrieve one Farm", description = "Returns one Farm entity from its UUID stored in the database.")
    public FarmEntity get(@PathVariable("uuid") @Parameter(description = "Identifier of the Farm") String uuid) {
        return super.get(uuid, farmRepository);
    }

    @PatchMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Farm has been successfully created.")
    @ApiResponse(responseCode = "404", description = "Farm not found.", content = @Content)
    @Operation(summary = "Update a Farm", description = "Updates one Farm entity in the database.")
    public FarmEntity update(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Farm", required = true) String uuid,
            @RequestBody Map<String, Object> fields) throws JsonMappingException {
        return super.update(uuid, fields, farmRepository);
    }

    @DeleteMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Farm has been successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Farm not found.", content = @Content)
    @Operation(summary = "Delete a Farm", description = "Deletes one Farm entity from its UUID stored in the database.")
    public void delete(@PathVariable("uuid") @Parameter(description = "Identifier of the Farm") String uuid) {
        super.delete(uuid, farmRepository);
    }
}
