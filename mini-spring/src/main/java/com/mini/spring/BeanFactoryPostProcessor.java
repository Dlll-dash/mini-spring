package com.mini.spring;

public interface BeanFactoryPostProcessor {

    /**
     * bean工厂后置处理方法
     * @param registry
     */
    default void postProcessBeanFactory(BeanDefinitionRegistry registry){}
}
