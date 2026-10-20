package com.mini.spring;

public interface BeanDefinitionRegistry {

    /**
     * 注册bean定义
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName,BeanDefinition beanDefinition);

    /**
     * 获取bean定义
     * @param name
     * @return BeanDefinition
     */
    BeanDefinition getBeanDefinition(String name);

    /**
     * 获取bean名单
     * @return String[] beanDefinitionNames
     */
    String[] getBeanDefinitionNames();

}
