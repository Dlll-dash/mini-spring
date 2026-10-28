package com.mini.spring.demo;

import com.mini.spring.BeanDefinitionRegistry;
import com.mini.spring.Condition;

import java.lang.reflect.AnnotatedElement;

public class HasOrderServiceCondition implements Condition {
    @Override
    public boolean matches(AnnotatedElement source, BeanDefinitionRegistry registry) {
        return registry.contains(OrderService.class);
    }
}
