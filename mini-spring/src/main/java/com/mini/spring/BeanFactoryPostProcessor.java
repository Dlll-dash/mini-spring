package com.mini.spring;

public interface BeanFactoryPostProcessor {

    default void postProcessBeanFactory(BeanDefinitionRegistry registry){}
}
