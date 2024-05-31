package ai.dragon.controller.api.ragapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.dragon.entity.SiloEntity;
import ai.dragon.service.EmbeddingStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ragapi/ask")
@Tag(name = "Ask", description = "Ask API Endpoints")
public class AskRagApiController {
    @Autowired
    private EmbeddingStoreService embeddingStoreService;

    // TODO Silo OR Farm
    @PostMapping("/searchDocuments")
    @ApiResponse(responseCode = "200", description = "Documents have been successfully retrieved.")
    @Operation(summary = "Search documents inside a Silo", description = "Search documents from the Silo.")
    public SiloEntity searchDocuments() {
        embeddingStoreService.query("TODO");
    }
}
