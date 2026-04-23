package com.khubeev.aop;

import com.khubeev.service.BenchmarkStatisticService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BenchmarkAspect {

    private final BenchmarkStatisticService benchmarkService;

    public BenchmarkAspect(BenchmarkStatisticService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @Pointcut("@annotation(benchmark)")
    public void benchmarkAnnotated(Benchmark benchmark) {
    }

    @Around(value = "benchmarkAnnotated(benchmark)", argNames = "joinPoint,benchmark")
    public Object measureTime(ProceedingJoinPoint joinPoint, Benchmark benchmark) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = getMethodName(signature, benchmark);

        long startTime = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;
            benchmarkService.recordExecutionTime(methodName, executionTime);
        }
    }

    private String getMethodName(MethodSignature signature, Benchmark benchmark) {
        if (benchmark.value() != null && !benchmark.value().isEmpty()) {
            return benchmark.value();
        }
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }
}