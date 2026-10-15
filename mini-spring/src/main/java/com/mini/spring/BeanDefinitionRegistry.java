package com.mini.spring;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName,BeanDefinition beanDefinition);

    BeanDefinition getBeanDefinition(String name);

    String[] getBeanDefinitionNames();

}
