package com.mini.spring;

import java.beans.Introspector;

public interface ImportBeanDefinitionRegistrar {

    /**
     * 将注入的类加入到bean容器中
     * @param cls
     * @param registry
     */
    default void registerBeanDefinition(Class<?>cls,BeanDefinitionRegistry registry){
        String name= Introspector.decapitalize(cls.getSimpleName());
        registry.registerBeanDefinition(name,new BeanDefinition(name,cls));
    }

}
