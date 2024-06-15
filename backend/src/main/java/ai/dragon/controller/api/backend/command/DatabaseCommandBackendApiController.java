package ai.dragon.controller.api.backend.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.dragon.service.DatabaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/backend/command/database")
@Tag(name = "Database Command", description = "Database Command API Endpoints")
public class DatabaseCommandBackendApiController {
    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/export")
    @ApiResponse(responseCode = "200", description = "Database dump has been successfully created.")
    @Operation(summary = "Create a database export", description = "Creates a JSON database dump.")
    public void exportDatabase(HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        databaseService.exportDatabase(response.getOutputStream());
        response.flushBuffer();
    }
}
