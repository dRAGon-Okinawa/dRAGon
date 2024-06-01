package ai.dragon.controller.api.ragapi;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.dragon.service.EmbeddingStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ragapi/ask")
@Tag(name = "Ask", description = "Ask API Endpoints")
public class AskRagApiController {
    @Autowired
    private EmbeddingStoreService embeddingStoreService;

    // TODO Silo OR Farm
    @PostMapping("/searchDocuments/silo/{uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @ApiResponse(responseCode = "200", description = "Documents have been successfully retrieved.")
    @Operation(summary = "Search documents inside a Silo", description = "Search documents from the Silo.")
    public void searchDocuments(@PathVariable("uuid") @Parameter(description = "Identifier of the Silo") UUID uuid,
            @RequestBody String query)
            throws Exception {
        embeddingStoreService.query(uuid, query);
    }
}
