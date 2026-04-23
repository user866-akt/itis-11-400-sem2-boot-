package com.khubeev.service;

import com.khubeev.aop.Benchmark;
import com.khubeev.aop.Metric;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    @Benchmark("HelloService.sayHello")
    @Metric("HelloService.sayHello")
    public String sayHello(String name) {
        return "Hello, %s".formatted(name);
    }
}