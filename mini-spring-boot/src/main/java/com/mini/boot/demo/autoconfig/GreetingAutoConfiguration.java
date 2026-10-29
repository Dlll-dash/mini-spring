package com.mini.boot.demo.autoconfig;

import com.mini.spring.annotation.Bean;

public class GreetingAutoConfiguration {
    @Bean
    public String autoGreeting(){
        return "Hello from autoGreeting";
    }
}
