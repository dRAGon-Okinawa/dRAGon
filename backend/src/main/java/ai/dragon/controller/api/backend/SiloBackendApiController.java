package ai.dragon.controller.api.backend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.dragon.model.SiloEntity;
import ai.dragon.repository.SiloRepository;
import io.swagger.v3.oas.annotations.Operation;
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
}
