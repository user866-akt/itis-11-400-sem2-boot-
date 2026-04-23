package com.khubeev.dto;

import com.khubeev.service.BenchmarkStatisticService;

import java.util.Map;

public class BenchmarkResponse {
    private Map<String, BenchmarkStatsDto> stats;
    private int totalMethods;

    public BenchmarkResponse(Map<String, BenchmarkStatsDto> stats, int totalMethods) {
        this.stats = stats;
        this.totalMethods = totalMethods;
    }

    public Map<String, BenchmarkStatsDto> getStats() { return stats; }
    public int getTotalMethods() { return totalMethods; }

    public static class BenchmarkStatsDto {
        private final int count;
        private final double minMillis;
        private final double maxMillis;
        private final double avgMillis;
        private final double p50Millis;
        private final double p95Millis;
        private final double p99Millis;

        public BenchmarkStatsDto(BenchmarkStatisticService.BenchmarkStats stats) {
            this.count = stats.getCount();
            this.minMillis = stats.getMinMillis();
            this.maxMillis = stats.getMaxMillis();
            this.avgMillis = stats.getAvgMillis();
            this.p50Millis = stats.getP50Millis();
            this.p95Millis = stats.getP95Millis();
            this.p99Millis = stats.getP99Millis();
        }

        public int getCount() { return count; }
        public double getMinMillis() { return minMillis; }
        public double getMaxMillis() { return maxMillis; }
        public double getAvgMillis() { return avgMillis; }
        public double getP50Millis() { return p50Millis; }
        public double getP95Millis() { return p95Millis; }
        public double getP99Millis() { return p99Millis; }
    }
}