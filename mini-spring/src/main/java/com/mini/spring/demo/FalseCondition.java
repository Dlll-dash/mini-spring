package com.mini.spring.demo;

import com.mini.spring.BeanDefinitionRegistry;
import com.mini.spring.Condition;

import java.lang.reflect.AnnotatedElement;

public class FalseCondition implements Condition {
    @Override
    public boolean matches(AnnotatedElement source, BeanDefinitionRegistry registry) {
        return false;
    }
}
