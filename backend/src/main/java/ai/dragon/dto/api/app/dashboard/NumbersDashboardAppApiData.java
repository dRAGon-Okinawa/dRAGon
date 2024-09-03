package ai.dragon.dto.api.app.dashboard;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NumbersDashboardAppApiData {
    private Long silos;
    private Long farms;
    private Long documents;
    private Double systemLoadAverage;
    private Integer availableProcessors;
    private String arch;
    private Long usedMemory;
    private Long totalMemory;
    private Long freeMemory;
    private Long usedMemoryPercentage;
    private Long freeMemoryPercentage;
    private Long uptime;
    private Long heapMemoryUsage;
    private Long heapMemoryUsagePercentage;
    private Long nonHeapMemoryUsage;
    private Long heapMemoryMax;
    private Long heapMemoryCommitted;
    private Long nonHeapMemoryCommitted;
    private Long heapMemoryInit;
    private Long nonHeapMemoryInit;
}
