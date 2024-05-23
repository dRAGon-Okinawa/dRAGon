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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.dragon.enumeration.ProviderType;
import ai.dragon.model.ProviderEntity;
import ai.dragon.repository.ProviderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/backend/provider")
@Tag(name = "Provider", description = "Provider Management API Endpoints")
public class ProviderBackendApiController {
    @Autowired
    private ProviderRepository providerRepository;

    @GetMapping("/")
    @ApiResponse(responseCode = "200", description = "List has been successfully retrieved.")
    @Operation(summary = "List all Providers", description = "Returns all Provider entities stored in the database.")
    public List<ProviderEntity> list() {
        return providerRepository.find().toList();
    }

    @PostMapping("/")
    @ApiResponse(responseCode = "200", description = "Provider has been successfully created.")
    @Operation(summary = "Create a new Provider", description = "Creates one Provider entity in the database.")
    public ProviderEntity create(
            @RequestParam(name = "type", required = true) @Parameter(description = "Type of the provider") String providerType) {
        String providerName = String.format("Provider %s",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        ProviderEntity provider = new ProviderEntity();
        provider.setName(providerName);
        provider.setType(ProviderType.valueOf(providerType));
        providerRepository.save(provider);
        return provider;
    }

    @GetMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Provider has been successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Provider not found.", content = @Content)
    @Operation(summary = "Retrieve one Provider", description = "Returns one Provider entity from its UUID stored in the database.")
    public ProviderEntity get(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Provider") String uuid) {
        return Optional.ofNullable(providerRepository.getByUuid(uuid))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found."));
    }

    @PatchMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Provider has been successfully created.")
    @ApiResponse(responseCode = "404", description = "Provider not found.", content = @Content)
    @Operation(summary = "Update a Provider", description = "Updates one Provider entity in the database.")
    public ProviderEntity update(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Provider", required = true) String uuid,
            ProviderEntity provider) throws JsonMappingException {
        if (!providerRepository.exists(uuid) || provider == null || !uuid.equals(provider.getUuid().toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provider not found");
        }

        ProviderEntity providerToUpdate = providerRepository.getByUuid(uuid);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.updateValue(providerToUpdate, provider);
        providerRepository.save(providerToUpdate);

        return providerToUpdate;
    }

    @DeleteMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Provider has been successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Provider not found.", content = @Content)
    @Operation(summary = "Delete a Provider", description = "Deletes one Provider entity from its UUID stored in the database.")
    public void delete(@PathVariable("uuid") @Parameter(description = "Identifier of the Provider") String uuid) {
        if (!providerRepository.exists(uuid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found.");
        }
        providerRepository.delete(uuid);
    }
}
