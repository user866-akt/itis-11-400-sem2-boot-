package com.khubeev.controller;

import com.khubeev.dto.BenchmarkResponse;
import com.khubeev.dto.MetricsResponse;
import com.khubeev.dto.PercentileResponse;
import com.khubeev.service.BenchmarkStatisticService;
import com.khubeev.service.MetricStatisticService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final MetricStatisticService metricService;
    private final BenchmarkStatisticService benchmarkService;

    public StatisticsController(MetricStatisticService metricService,
                                BenchmarkStatisticService benchmarkService) {
        this.metricService = metricService;
        this.benchmarkService = benchmarkService;
    }

    @GetMapping("/metrics")
    public MetricsResponse getMetrics() {
        Map<String, MetricsResponse.MethodMetricDto> metrics = metricService.getAllMetrics()
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new MetricsResponse.MethodMetricDto(
                                e.getValue().getSuccessCount(),
                                e.getValue().getFailureCount()
                        )
                ));

        return new MetricsResponse(metrics, metrics.size());
    }

    @GetMapping("/metrics/{methodName}")
    public MetricsResponse.MethodMetricDto getMetricForMethod(@PathVariable String methodName) {
        MetricStatisticService.MethodMetric metric = metricService.getMetric(methodName);
        return new MetricsResponse.MethodMetricDto(
                metric.getSuccessCount(),
                metric.getFailureCount()
        );
    }

    @GetMapping("/benchmark")
    public BenchmarkResponse getAllBenchmarkStats() {
        Map<String, BenchmarkResponse.BenchmarkStatsDto> stats = benchmarkService.getAllStats()
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new BenchmarkResponse.BenchmarkStatsDto(e.getValue())
                ));

        return new BenchmarkResponse(stats, stats.size());
    }

    @GetMapping("/benchmark/{methodName}")
    public BenchmarkResponse.BenchmarkStatsDto getBenchmarkForMethod(@PathVariable String methodName) {
        BenchmarkStatisticService.BenchmarkStats stats = benchmarkService.getStatsForMethod(methodName);
        return new BenchmarkResponse.BenchmarkStatsDto(stats);
    }

    @GetMapping("/benchmark/percentile")
    public PercentileResponse getPercentile(
            @RequestParam String methodName,
            @RequestParam int percentile) {

        if (percentile < 0 || percentile > 100) {
            throw new IllegalArgumentException("Percentile must be between 0 and 100");
        }

        long value = benchmarkService.getPercentileForMethod(methodName, percentile);
        int totalSamples = benchmarkService.getStatsForMethod(methodName).getCount();

        return new PercentileResponse(methodName, percentile, value, totalSamples);
    }

    @DeleteMapping("/benchmark/clear")
    public String clearBenchmarkStats() {
        benchmarkService.clearStats();
        return "Benchmark statistics cleared";
    }

    @GetMapping("/benchmark/summary")
    public Map<String, Object> getSummary() {
        return Map.of(
                "totalMethodsWithMetrics", metricService.getAllMetrics().size(),
                "totalMethodsWithBenchmark", benchmarkService.getAllStats().size()
        );
    }
}