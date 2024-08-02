package ai.dragon.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class SystemMonitoringServiceTest {
    @Autowired
    private SystemMonitoringService systemMonitoringService;

    @Test
    public void testGetSystemLoadAverage() {
        double loadAverage = systemMonitoringService.getSystemLoadAverage();
        assertTrue(loadAverage >= -1.0);
    }

    @Test
    public void testGetAvailableProcessors() {
        int processors = systemMonitoringService.getAvailableProcessors();
        assertTrue(processors > 0);
    }

    @Test
    public void testGetArch() {
        String arch = systemMonitoringService.getArch();
        assertTrue(arch != null && !arch.isEmpty());
    }

    @Test
    public void testGetUsedMemory() {
        long usedMemory = systemMonitoringService.getUsedMemory();
        assertTrue(usedMemory > 0);
    }

    @Test
    public void testGetTotalMemory() {
        long totalMemory = systemMonitoringService.getTotalMemory();
        assertTrue(totalMemory > 0);
    }

    @Test
    public void testGetFreeMemory() {
        long freeMemory = systemMonitoringService.getFreeMemory();
        assertTrue(freeMemory >= 0);
    }

    @Test
    public void testGetUsedMemoryPercentage() {
        long usedMemoryPercentage = systemMonitoringService.getUsedMemoryPercentage();
        assertTrue(usedMemoryPercentage >= 0 && usedMemoryPercentage <= 100);
    }

    @Test
    public void testGetFreeMemoryPercentage() {
        long freeMemoryPercentage = systemMonitoringService.getFreeMemoryPercentage();
        assertTrue(freeMemoryPercentage >= 0 && freeMemoryPercentage <= 100);
    }

    @Test
    public void testGetUptime() {
        long uptime = systemMonitoringService.getUptime();
        assertTrue(uptime >= 0);
    }

    @Test
    public void testGetHeapMemoryUsagePercentage() {
        long heapMemoryUsagePercentage = systemMonitoringService.getHeapMemoryUsagePercentage();
        assertTrue(heapMemoryUsagePercentage >= 0 && heapMemoryUsagePercentage <= 100);
    }

    @Test
    public void testGetHeapMemoryUsage() {
        long heapMemoryUsage = systemMonitoringService.getHeapMemoryUsage();
        assertTrue(heapMemoryUsage > 0);
    }

    @Test
    public void testGetNonHeapMemoryUsage() {
        long nonHeapMemoryUsage = systemMonitoringService.getNonHeapMemoryUsage();
        assertTrue(nonHeapMemoryUsage > 0);
    }

    @Test
    public void testGetHeapMemoryMax() {
        long heapMemoryMax = systemMonitoringService.getHeapMemoryMax();
        assertTrue(heapMemoryMax > 0);
    }

    @Test
    public void testGetHeapMemoryCommitted() {
        long heapMemoryCommitted = systemMonitoringService.getHeapMemoryCommitted();
        assertTrue(heapMemoryCommitted > 0);
    }

    @Test
    public void testGetNonHeapMemoryCommitted() {
        long nonHeapMemoryCommitted = systemMonitoringService.getNonHeapMemoryCommitted();
        assertTrue(nonHeapMemoryCommitted > 0);
    }

    @Test
    public void testGetHeapMemoryInit() {
        long heapMemoryInit = systemMonitoringService.getHeapMemoryInit();
        assertTrue(heapMemoryInit > 0);
    }

    @Test
    public void testGetNonHeapMemoryInit() {
        long nonHeapMemoryInit = systemMonitoringService.getNonHeapMemoryInit();
        assertTrue(nonHeapMemoryInit > 0);
    }
}
