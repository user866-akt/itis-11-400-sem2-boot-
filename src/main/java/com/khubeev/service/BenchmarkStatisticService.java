package com.khubeev.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class BenchmarkStatisticService {

    private final Map<String, List<Long>> executionTimes = new ConcurrentHashMap<>();

    public void recordExecutionTime(String methodName, long executionTimeNanos) {
        executionTimes.computeIfAbsent(methodName, k -> new CopyOnWriteArrayList<>())
                .add(executionTimeNanos);
    }

    public Map<String, BenchmarkStats> getAllStats() {
        return executionTimes.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> calculateStats(e.getValue())
                ));
    }

    public BenchmarkStats getStatsForMethod(String methodName) {
        List<Long> times = executionTimes.getOrDefault(methodName, Collections.emptyList());
        return calculateStats(times);
    }

    public long getPercentileForMethod(String methodName, int percentile) {
        List<Long> times = executionTimes.getOrDefault(methodName, Collections.emptyList());
        if (times.isEmpty()) {
            return 0;
        }
        return calculatePercentile(new ArrayList<>(times), percentile);
    }

    private BenchmarkStats calculateStats(List<Long> times) {
        if (times.isEmpty()) {
            return new BenchmarkStats(0, 0, 0, 0, 0, 0, 0);
        }

        List<Long> sortedTimes = new ArrayList<>(times);
        Collections.sort(sortedTimes);

        long min = sortedTimes.get(0);
        long max = sortedTimes.get(sortedTimes.size() - 1);
        double avg = sortedTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long p50 = calculatePercentile(sortedTimes, 50);
        long p95 = calculatePercentile(sortedTimes, 95);
        long p99 = calculatePercentile(sortedTimes, 99);

        return new BenchmarkStats(times.size(), min, max, avg, p50, p95, p99);
    }

    private long calculatePercentile(List<Long> sortedTimes, int percentile) {
        if (sortedTimes.isEmpty()) {
            return 0;
        }

        double index = (percentile / 100.0) * (sortedTimes.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);

        if (lowerIndex == upperIndex) {
            return sortedTimes.get(lowerIndex);
        }

        double fraction = index - lowerIndex;
        return (long) (sortedTimes.get(lowerIndex) * (1 - fraction) +
                sortedTimes.get(upperIndex) * fraction);
    }

    public void clearStats() {
        executionTimes.clear();
    }

    public static class BenchmarkStats {
        private final int count;
        private final long minNanos;
        private final long maxNanos;
        private final double avgNanos;
        private final long p50Nanos;
        private final long p95Nanos;
        private final long p99Nanos;

        public BenchmarkStats(int count, long minNanos, long maxNanos, double avgNanos,
                              long p50Nanos, long p95Nanos, long p99Nanos) {
            this.count = count;
            this.minNanos = minNanos;
            this.maxNanos = maxNanos;
            this.avgNanos = avgNanos;
            this.p50Nanos = p50Nanos;
            this.p95Nanos = p95Nanos;
            this.p99Nanos = p99Nanos;
        }

        public int getCount() { return count; }
        public long getMinNanos() { return minNanos; }
        public long getMaxNanos() { return maxNanos; }
        public double getAvgNanos() { return avgNanos; }
        public long getP50Nanos() { return p50Nanos; }
        public long getP95Nanos() { return p95Nanos; }
        public long getP99Nanos() { return p99Nanos; }

        public double getMinMillis() { return minNanos / 1_000_000.0; }
        public double getMaxMillis() { return maxNanos / 1_000_000.0; }
        public double getAvgMillis() { return avgNanos / 1_000_000.0; }
        public double getP50Millis() { return p50Nanos / 1_000_000.0; }
        public double getP95Millis() { return p95Nanos / 1_000_000.0; }
        public double getP99Millis() { return p99Nanos / 1_000_000.0; }
    }
}