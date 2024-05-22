package ai.dragon.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthApiController {
    @GetMapping("/status")
    public String health() {
        return "OK";
    }
}
