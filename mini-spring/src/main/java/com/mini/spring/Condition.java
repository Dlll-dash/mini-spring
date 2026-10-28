package com.mini.spring;

import java.lang.reflect.AnnotatedElement;

public interface Condition {
    boolean matches(AnnotatedElement source, BeanDefinitionRegistry registry);
}
