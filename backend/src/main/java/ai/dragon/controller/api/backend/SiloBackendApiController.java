package ai.dragon.controller.api.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backend/silo")
public class SiloBackendApiController {
    @GetMapping("/test")
    public String health() {
        return "OK";
    }
}
