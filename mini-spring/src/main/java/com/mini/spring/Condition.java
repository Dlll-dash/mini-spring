package com.mini.spring;

public interface Condition {
    boolean matches(Class<?> type, BeanDefinitionRegistry registry);
}
