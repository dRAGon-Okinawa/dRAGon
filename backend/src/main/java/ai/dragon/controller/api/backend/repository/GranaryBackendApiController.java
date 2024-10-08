package ai.dragon.controller.api.backend.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.dizitart.no2.filters.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonMappingException;

import ai.dragon.dto.api.DataTableApiResponse;
import ai.dragon.dto.api.GenericApiResponse;
import ai.dragon.dto.api.SuccessApiResponse;
import ai.dragon.dto.api.backend.UUIDsBatchRequest;
import ai.dragon.entity.GranaryEntity;
import ai.dragon.repository.GranaryRepository;
import ai.dragon.repository.util.Pager;
import ai.dragon.util.UUIDUtil;
import ai.dragon.util.db.filters.ExtendedFluentFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/backend/repository/granary")
@Tag(name = "Granary Repository", description = "Granary Repository Management API Endpoints")
public class GranaryBackendApiController extends AbstractCrudBackendApiController<GranaryEntity> {
    @Autowired
    private GranaryRepository granaryRepository;

    @GetMapping("/")
    @ApiResponse(responseCode = "200", description = "List has been successfully retrieved.")
    @Operation(summary = "Search Granaries", description = "Search Granary entities stored in the database.")
    public GenericApiResponse searchGranaries(@RequestParam(name = "current", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "uuid", required = false) String uuid,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "vectorStore", required = false) String vectorStore) {
        Filter filter = ExtendedFluentFilter
                .where("uuid").nonsensitiveRegex(String.format("^%s", Optional.ofNullable(uuid).orElse("")))
                .and(ExtendedFluentFilter.where("name").nonsensitiveRegex(Optional.ofNullable(name).orElse(""))
                        .and(ExtendedFluentFilter.where("engineType")
                                .nonsensitiveRegex(Optional.ofNullable(vectorStore).orElse(""))));
        Pager<GranaryEntity> pager = super.page(granaryRepository, filter, page, size);
        return DataTableApiResponse.fromPager(pager);
    }

    @PostMapping("/")
    @ApiResponse(responseCode = "200", description = "Granary has been successfully created.")
    @ApiResponse(responseCode = "409", description = "Constraint violation.", content = @Content)
    @Operation(summary = "Create a new Granary", description = "Creates one Granary entity in the database.")
    public GenericApiResponse createGranary() throws Exception {
        return SuccessApiResponse
                .builder()
                .data(super.create(granaryRepository))
                .build();
    }

    @GetMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Granary has been successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Granary not found.", content = @Content)
    @Operation(summary = "Retrieve one Granary", description = "Returns one Granary entity from its UUID stored in the database.")
    public GenericApiResponse getGranary(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Granary", required = true) String uuid) {
        return SuccessApiResponse
                .builder()
                .data(super.get(uuid, granaryRepository))
                .build();
    }

    @PatchMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Granary has been successfully updated.")
    @ApiResponse(responseCode = "404", description = "Granary not found.", content = @Content)
    @Operation(summary = "Update a Granary", description = "Updates one Granary entity in the database.")
    public GenericApiResponse updateGranary(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Granary", required = true) String uuid,
            @RequestBody Map<String, Object> fields) throws JsonMappingException {
        return SuccessApiResponse
                .builder()
                .data(super.update(uuid, fields, granaryRepository))
                .build();
    }

    @PutMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Granary has been successfully updated.")
    @ApiResponse(responseCode = "404", description = "Granary not found.", content = @Content)
    @Operation(summary = "Upsert a Granary", description = "Upsert one Granary entity in the database.")
    public GenericApiResponse upsertGranary(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Granary", required = false) String uuid,
            @RequestBody Map<String, Object> fields) throws Exception {
        if (uuid == null || UUIDUtil.zeroUUIDString().equals(uuid)) {
            fields.remove("uuid");
            uuid = super.create(granaryRepository).getUuid().toString();
        }
        return SuccessApiResponse
                .builder()
                .data(super.update(uuid, fields, granaryRepository))
                .build();
    }

    @DeleteMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Granary has been successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Granary not found.", content = @Content)
    @Operation(summary = "Delete a Granary", description = "Deletes one Granary entity from its UUID stored in the database.")
    public GenericApiResponse deleteGranary(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Granary") UUID uuid)
            throws Exception {
        super.delete(uuid, granaryRepository);
        return SuccessApiResponse.builder().build();
    }

    @DeleteMapping("/deleteMultiple")
    @ApiResponse(responseCode = "200", description = "Granaries have been successfully deleted.")
    @Operation(summary = "Delete multiple Granaries", description = "Deletes one or more Granary entity from their UUID stored in the database.")
    public GenericApiResponse deleteMultipleGranaries(@RequestBody UUIDsBatchRequest request) throws Exception {
        super.deleteMultiple(request.getUuids(), granaryRepository);
        return SuccessApiResponse.builder().build();
    }
}
