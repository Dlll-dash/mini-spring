package com.mini.spring.demo;

import com.mini.spring.BeanDefinitionRegistry;
import com.mini.spring.Condition;

public class TrueCondition implements Condition {
    @Override
    public boolean matches(Class<?> type, BeanDefinitionRegistry registry) {
        return registry.contains(Main.class);
    }
}
