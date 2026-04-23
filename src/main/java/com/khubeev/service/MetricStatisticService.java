package com.khubeev.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricStatisticService {

    private final Map<String, MethodMetric> metrics = new ConcurrentHashMap<>();

    public void recordSuccess(String methodName) {
        metrics.computeIfAbsent(methodName, k -> new MethodMetric())
                .incrementSuccess();
    }

    public void recordFailure(String methodName) {
        metrics.computeIfAbsent(methodName, k -> new MethodMetric())
                .incrementFailure();
    }

    public Map<String, MethodMetric> getAllMetrics() {
        return Map.copyOf(metrics);
    }

    public MethodMetric getMetric(String methodName) {
        return metrics.getOrDefault(methodName, new MethodMetric(0, 0));
    }

    public static class MethodMetric {
        private final AtomicLong successCount;
        private final AtomicLong failureCount;

        public MethodMetric() {
            this.successCount = new AtomicLong(0);
            this.failureCount = new AtomicLong(0);
        }

        public MethodMetric(long success, long failure) {
            this.successCount = new AtomicLong(success);
            this.failureCount = new AtomicLong(failure);
        }

        public void incrementSuccess() {
            successCount.incrementAndGet();
        }

        public void incrementFailure() {
            failureCount.incrementAndGet();
        }

        public long getSuccessCount() {
            return successCount.get();
        }

        public long getFailureCount() {
            return failureCount.get();
        }

        public long getTotalCount() {
            return successCount.get() + failureCount.get();
        }

        public double getSuccessRate() {
            long total = getTotalCount();
            return total == 0 ? 0.0 : (double) successCount.get() / total * 100;
        }
    }
}