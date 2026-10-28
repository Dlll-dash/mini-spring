package com.mini.spring.demo;

import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.Value;

@Component
public class DefaultGreeter implements Greeter{
    @Value("${mini.greeting:Hello from @Component}")
    private String greeting;

    @Value("${mini.hits:0}")
    private Integer hits;

    @Override
    public String greet() {
        return greeting+" hits: "+hits;
    }
}
