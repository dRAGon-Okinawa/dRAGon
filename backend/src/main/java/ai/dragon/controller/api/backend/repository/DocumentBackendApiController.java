package ai.dragon.controller.api.backend.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.filters.FluentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonMappingException;

import ai.dragon.entity.DocumentEntity;
import ai.dragon.entity.SiloEntity;
import ai.dragon.repository.DocumentRepository;
import ai.dragon.repository.SiloRepository;
import ai.dragon.service.DocumentService;
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
    private SiloRepository siloRepository;

    @Autowired
    private DocumentService documentService;

    @GetMapping("/silo/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "List has been successfully retrieved.")
    @Operation(summary = "List Documents of a Silo", description = "Returns Documents entities about a Silo.")
    public List<DocumentEntity> listDocumentsOfSilo(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Silo") String uuid,
            @RequestParam(name = "limit", required = false, defaultValue = "10") @Parameter(description = "Limit number of results") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") @Parameter(description = "Skip x results") int offset) {
        return super.findWithFilter(documentRepository,
                FluentFilter.where("siloIdentifier").eq(uuid),
                FindOptions.limitBy(limit).skip(offset))
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
    @Operation(summary = "Update a Document", description = "Updates one Document entity in the database. Only the field 'allowIndexing' can be updated.")
    public DocumentEntity updateDocument(
            @PathVariable("uuid") @Parameter(description = "Identifier of the Document", required = true) String uuid,
            @RequestBody Map<String, Object> fields) throws JsonMappingException {
        Map<String, Object> fieldsToUpdate = new HashMap<>();
        if (fields.containsKey("allowIndexing")) {
            fieldsToUpdate.put("allowIndexing", fields.get("allowIndexing"));
        }
        return super.update(uuid, fieldsToUpdate, documentRepository);
    }

    @DeleteMapping("/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Document has been successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Document not found.", content = @Content)
    @Operation(summary = "Delete a Document", description = "Deletes one Document entity from its UUID stored in the database.")
    public void deleteDocument(@PathVariable("uuid") @Parameter(description = "Identifier of the Document") UUID uuid)
            throws Exception {
        super.delete(uuid, documentRepository);
    }

    @DeleteMapping("/silo/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Documents has been successfully deleted.")
    @Operation(summary = "Delete all Documents from a Silo", description = "Deletes all Document entities from a Silo.")
    public void deleteSiloDocuments(@PathVariable("uuid") @Parameter(description = "Identifier of the Silo") UUID uuid)
            throws Exception {
        SiloEntity silo = siloRepository.getByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Silo not found."));
        documentService.removeAllDocumentsOfSilo(silo);
    }
}
