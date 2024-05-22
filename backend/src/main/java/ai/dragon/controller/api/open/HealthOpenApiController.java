package ai.dragon.controller.api.open;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/open/health")
public class HealthOpenApiController {
    @GetMapping("/status")
    public String health() {
        return "ALIVE";
    }
}
