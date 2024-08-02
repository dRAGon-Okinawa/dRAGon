package ai.dragon.service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SystemMonitoringService {
    private OperatingSystemMXBean operatingSystemMXBean;
    private RuntimeMXBean runtimeMXBean;
    private MemoryMXBean memoryMXBean;

    @PostConstruct
    private void postConstruct() {
        operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    public double getSystemLoadAverage() {
        return operatingSystemMXBean.getSystemLoadAverage();
    }

    public int getAvailableProcessors() {
        return operatingSystemMXBean.getAvailableProcessors();
    }

    public String getArch() {
        return operatingSystemMXBean.getArch();
    }

    public long getUsedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public long getUsedMemoryPercentage() {
        return (getUsedMemory() * 100) / getTotalMemory();
    }

    public long getFreeMemoryPercentage() {
        return (getFreeMemory() * 100) / getTotalMemory();
    }

    public long getUptime() {
        return runtimeMXBean.getUptime();
    }

    public long getHeapMemoryUsagePercentage() {
        return (getHeapMemoryUsage() * 100) / getHeapMemoryMax();
    }

    public long getHeapMemoryUsage() {
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    public long getNonHeapMemoryUsage() {
        return memoryMXBean.getNonHeapMemoryUsage().getUsed();
    }

    public long getHeapMemoryMax() {
        return memoryMXBean.getHeapMemoryUsage().getMax();
    }

    public long getHeapMemoryCommitted() {
        return memoryMXBean.getHeapMemoryUsage().getCommitted();
    }

    public long getNonHeapMemoryCommitted() {
        return memoryMXBean.getNonHeapMemoryUsage().getCommitted();
    }

    public long getHeapMemoryInit() {
        return memoryMXBean.getHeapMemoryUsage().getInit();
    }

    public long getNonHeapMemoryInit() {
        return memoryMXBean.getNonHeapMemoryUsage().getInit();
    }
}
