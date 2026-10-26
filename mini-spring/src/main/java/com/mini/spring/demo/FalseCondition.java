package com.mini.spring.demo;

import com.mini.spring.BeanDefinitionRegistry;
import com.mini.spring.Condition;

public class FalseCondition implements Condition {
    @Override
    public boolean matches(Class<?> type, BeanDefinitionRegistry registry) {
        return false;
    }
}
