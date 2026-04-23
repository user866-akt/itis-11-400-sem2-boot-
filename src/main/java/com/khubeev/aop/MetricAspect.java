package com.khubeev.aop;

import com.khubeev.service.MetricStatisticService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MetricAspect {

    private final MetricStatisticService metricService;

    public MetricAspect(MetricStatisticService metricService) {
        this.metricService = metricService;
    }

    @Pointcut("@annotation(metric)")
    public void metricAnnotated(Metric metric) {
    }

    @Around(value = "metricAnnotated(metric)", argNames = "joinPoint,metric")
    public Object measureMetric(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = getMethodName(signature, metric);

        try {
            Object result = joinPoint.proceed();
            metricService.recordSuccess(methodName);
            return result;
        } catch (Throwable throwable) {
            metricService.recordFailure(methodName);
            throw throwable;
        }
    }

    private String getMethodName(MethodSignature signature, Metric metric) {
        if (metric.value() != null && !metric.value().isEmpty()) {
            return metric.value();
        }
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }
}