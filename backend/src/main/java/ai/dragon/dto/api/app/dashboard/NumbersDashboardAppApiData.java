package ai.dragon.dto.api.app.dashboard;

import ai.dragon.dto.api.GenericApiData;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NumbersDashboardAppApiData implements GenericApiData {
    private Long silos;
    private Long farms;
    private Long documents;
}
