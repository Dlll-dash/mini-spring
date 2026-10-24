package com.mini.spring.demo;

import com.mini.spring.annotation.Bean;
import com.mini.spring.annotation.Component;

@Component
public class ServiceConfig {
    @Bean
    public StringBuilder builder(){
        return new StringBuilder("made by @Bean");
    }

    @Bean
    public String greeting(OrderService orderService){
        return "hello from " + orderService.getClass().getSimpleName();
    }
}
