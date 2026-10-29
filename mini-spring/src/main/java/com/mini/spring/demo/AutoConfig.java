package com.mini.spring.demo;

import com.mini.spring.annotation.Bean;

public class AutoConfig {
    @Bean
    public StringBuilder auto(){
        return new StringBuilder("auto");
    }

}
