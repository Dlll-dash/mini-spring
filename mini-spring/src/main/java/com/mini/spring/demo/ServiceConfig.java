package com.mini.spring.demo;

import com.mini.spring.annotation.Bean;
import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.Conditional;

@Component
public class ServiceConfig {
    @Bean
    @Conditional(TrueCondition.class)
    public StringBuilder builder(){
        return new StringBuilder("made by @Bean");
    }

    @Bean
    @Conditional(HasOrderServiceCondition.class)
    public String greeting(OrderService orderService){
        return "hello from " + orderService.getClass().getSimpleName();
    }
}
