package ai.dragon.controller.api.backend.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.filters.FluentFilter;
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
import ai.dragon.entity.FarmEntity;
import ai.dragon.repository.FarmRepository;
import ai.dragon.repository.util.Pager;
import ai.dragon.util.UUIDUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/backend/repository/farm")
@Tag(name = "Farm Repository", description = "Farm Repository Management API Endpoints")
public class FarmBackendApiController extends AbstractCrudBackendApiController<FarmEntity> {
    @Autowired
    private FarmRepository farmRepository;

    @GetMapping("/")
    @ApiResponse(responseCode = "200", description = "List has been successfully retrieved.")
    @Operation(summary = "Search Farms", description = "Search Farm entities stored in the database.")
    public GenericApiResponse searchFarms(@RequestParam(name = "current", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "uuid", required = false) String uuid,
            @RequestParam(name = "name", required = false) String name) {
        Filter filter = FluentFilter
                .where("uuid").regex(String.format("^%s", Optional.ofNullable(uuid).orElse("")))
                .and(FluentFilter.where("name").regex(Optional.ofNullable(name).orElse("")));
        Pager<FarmEntity> pager = super.page(farmRepository, filter, page, size);
        return DataTableApiResponse.fromPager(pager);
    }

    @PostMapping("/")
    @ApiResponse(responseCode = "200", description = "Farm has been successfully created.")
    @ApiResponse(responseCode = "409", description = "Constraint violation.", content = @Content)
    @Operation(summary = "Create a new Farm", description = "Creates one Farm entity in the database.")
    public GenericApiResponse createFarm() throws Exception {
        return SuccessApiResponse
                .builder()
                .data(super.create(farmRepository))
                .build();
    }

    @GetMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Farm has been successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Farm not found.", content = @Content)
    @Operation(summary = "Retrieve one Farm", description = "Returns one Farm entity from its UUID stored in the database.")
    public GenericApiResponse getFarm(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Farm", required = true) String uuid) {
        return SuccessApiResponse
                .builder()
                .data(super.get(uuid, farmRepository))
                .build();
    }

    @PatchMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Farm has been successfully updated.")
    @ApiResponse(responseCode = "404", description = "Farm not found.", content = @Content)
    @Operation(summary = "Update a Farm", description = "Updates one Farm entity in the database.")
    public GenericApiResponse updateFarm(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Farm", required = true) String uuid,
            @RequestBody Map<String, Object> fields) throws JsonMappingException {
        return SuccessApiResponse
                .builder()
                .data(super.update(uuid, fields, farmRepository))
                .build();
    }

    @PutMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Farm has been successfully updated.")
    @ApiResponse(responseCode = "404", description = "Farm not found.", content = @Content)
    @Operation(summary = "Upsert a Farm", description = "Upsert one Farm entity in the database.")
    public GenericApiResponse upsertFarm(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Farm", required = false) String uuid,
            @RequestBody Map<String, Object> fields) throws Exception {
        if (uuid == null || UUIDUtil.zeroUUIDString().equals(uuid)) {
            fields.remove("uuid");
            uuid = super.create(farmRepository).getUuid().toString();
        }
        return SuccessApiResponse
                .builder()
                .data(super.update(uuid, fields, farmRepository))
                .build();
    }

    @DeleteMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Farm has been successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Farm not found.", content = @Content)
    @Operation(summary = "Delete a Farm", description = "Deletes one Farm entity from its UUID stored in the database.")
    public GenericApiResponse deleteFarm(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Farm") UUID uuid)
            throws Exception {
        super.delete(uuid, farmRepository);
        return SuccessApiResponse.builder().build();
    }

    @DeleteMapping("/deleteMultiple")
    @ApiResponse(responseCode = "200", description = "Farms have been successfully deleted.")
    @Operation(summary = "Delete multiple Farms", description = "Deletes one or more Farm entity from their UUID stored in the database.")
    public GenericApiResponse deleteMultipleFarms(@RequestBody UUIDsBatchRequest request) throws Exception {
        super.deleteMultiple(request.getUuids(), farmRepository);
        return SuccessApiResponse.builder().build();
    }
}
