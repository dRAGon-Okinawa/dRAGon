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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonMappingException;

import ai.dragon.entity.ProviderEntity;
import ai.dragon.repository.ProviderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/backendapi/repository/provider")
@Tag(name = "Provider Repository", description = "Provider Repository Management API Endpoints")
public class ProviderBackendApiController extends AbstractCrudBackendApiController<ProviderEntity> {
    @Autowired
    private ProviderRepository providerRepository;

    @GetMapping("/")
    @ApiResponse(responseCode = "200", description = "List has been successfully retrieved.")
    @Operation(summary = "List all Providers", description = "Returns all Provider entities stored in the database.")
    public List<ProviderEntity> list() {
        return super.list(providerRepository);
    }

    @PostMapping("/")
    @ApiResponse(responseCode = "200", description = "Provider has been successfully created.")
    @Operation(summary = "Create a new Provider", description = "Creates one Provider entity in the database.")
    public ProviderEntity create(
            @RequestParam(name = "type", required = true) @Parameter(description = "Type of the provider") String providerType)
            throws Exception {
        return super.create(providerRepository);
    }

    @GetMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Provider has been successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Provider not found.", content = @Content)
    @Operation(summary = "Retrieve one Provider", description = "Returns one Provider entity from its UUID stored in the database.")
    public ProviderEntity get(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Provider") String uuid) {
        return super.get(uuid, providerRepository);
    }

    @PatchMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Provider has been successfully created.")
    @ApiResponse(responseCode = "404", description = "Provider not found.", content = @Content)
    @Operation(summary = "Update a Provider", description = "Updates one Provider entity in the database.")
    public ProviderEntity update(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Provider", required = true) String uuid,
            @RequestBody Map<String, Object> fields) throws JsonMappingException {
        return super.update(uuid, fields, providerRepository);
    }

    @DeleteMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Provider has been successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Provider not found.", content = @Content)
    @Operation(summary = "Delete a Provider", description = "Deletes one Provider entity from its UUID stored in the database.")
    public void delete(@PathVariable("uuid") @Parameter(description = "Identifier of the Provider") String uuid) {
        super.delete(uuid, providerRepository);
    }
}
