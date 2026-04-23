package com.khubeev.dto;

public class PercentileResponse {
    private final String methodName;
    private final int percentile;
    private final long valueNanos;
    private final double valueMillis;
    private final int totalSamples;

    public PercentileResponse(String methodName, int percentile, long valueNanos, int totalSamples) {
        this.methodName = methodName;
        this.percentile = percentile;
        this.valueNanos = valueNanos;
        this.valueMillis = valueNanos / 1_000_000.0;
        this.totalSamples = totalSamples;
    }

    public String getMethodName() { return methodName; }
    public int getPercentile() { return percentile; }
    public long getValueNanos() { return valueNanos; }
    public double getValueMillis() { return valueMillis; }
    public int getTotalSamples() { return totalSamples; }
}