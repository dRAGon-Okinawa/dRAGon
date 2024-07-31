package ai.dragon.controller.api.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.dragon.dto.api.GenericApiResponse;
import ai.dragon.dto.api.SuccessApiResponse;
import ai.dragon.dto.api.app.dashboard.NumbersDashboardAppApiData;
import ai.dragon.repository.DocumentRepository;
import ai.dragon.repository.FarmRepository;
import ai.dragon.repository.SiloRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/app/dashboard")
@Tag(name = "Operations Center", description = "Dashboard API Endpoints")
public class DashboardAppApiController {
    @Autowired
    private SiloRepository siloRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping("/numbers")
    @Operation(summary = "Get Dashboard main numbers", description = "Returns the main numbers of the dashboard.")
    public GenericApiResponse numbers() {
        return SuccessApiResponse.builder().data(NumbersDashboardAppApiData
                .builder()
                .silos(siloRepository.countAll())
                .farms(farmRepository.countAll())
                .documents(documentRepository.countAll())
                .build())
                .build();
    }
}
