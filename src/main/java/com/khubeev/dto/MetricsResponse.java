package com.khubeev.dto;

import java.util.Map;

public class MetricsResponse {
    private Map<String, MethodMetricDto> metrics;
    private int totalMethods;

    public MetricsResponse(Map<String, MethodMetricDto> metrics, int totalMethods) {
        this.metrics = metrics;
        this.totalMethods = totalMethods;
    }

    public Map<String, MethodMetricDto> getMetrics() { return metrics; }
    public int getTotalMethods() { return totalMethods; }

    public static class MethodMetricDto {
        private final long successCount;
        private final long failureCount;
        private final long totalCount;
        private final double successRate;

        public MethodMetricDto(long successCount, long failureCount) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.totalCount = successCount + failureCount;
            this.successRate = totalCount == 0 ? 0.0 : (double) successCount / totalCount * 100;
        }

        public long getSuccessCount() { return successCount; }
        public long getFailureCount() { return failureCount; }
        public long getTotalCount() { return totalCount; }
        public double getSuccessRate() { return successRate; }
    }
}