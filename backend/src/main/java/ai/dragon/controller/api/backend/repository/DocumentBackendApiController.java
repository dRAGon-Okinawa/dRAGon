package ai.dragon.controller.api.backend.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.filters.FluentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonMappingException;

import ai.dragon.entity.DocumentEntity;
import ai.dragon.repository.DocumentRepository;
import ai.dragon.service.SiloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/backend/repository/document")
@Tag(name = "Document Repository", description = "Document Repository Management API Endpoints")
public class DocumentBackendApiController extends AbstractCrudBackendApiController<DocumentEntity> {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private SiloService siloService;

    @GetMapping("/silo/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "List has been successfully retrieved.")
    @Operation(summary = "List Documents of a Silo", description = "Returns Documents entities about a Silo.")
    public List<DocumentEntity> listDocumentsOfSilo(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Silo") String uuid,
            FindOptions findOptions) {
        return super.findWithFilter(documentRepository,
                FluentFilter.where("siloIdentifier").eq(uuid),
                findOptions)
                .toList();
    }

    @GetMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Document has been successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Document not found.", content = @Content)
    @Operation(summary = "Retrieve one Document", description = "Returns one Document entity from its UUID stored in the database.")
    public DocumentEntity getDocument(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Document") String uuid) {
        return super.get(uuid, documentRepository);
    }

    @PatchMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Document has been successfully updated.")
    @ApiResponse(responseCode = "404", description = "Document not found.", content = @Content)
    @Operation(summary = "Update a Document", description = "Updates one Document entity in the database.")
    public DocumentEntity updateSilo(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Document", required = true) String uuid,
            @RequestBody Map<String, Object> fields) throws JsonMappingException {
        // TODO Prevent from updating the Silo of the Document
        return super.update(uuid, fields, documentRepository);
    }

    @DeleteMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Document has been successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Document not found.", content = @Content)
    @Operation(summary = "Delete a Silo", description = "Deletes one Document entity from its UUID stored in the database.")
    public void deleteSilo(@PathVariable("uuid") @Parameter(description = "Identifier of the Document") UUID uuid)
            throws Exception {
        // TODO siloService.removeEmbeddings(uuid);
        super.delete(uuid, documentRepository);
    }
}
