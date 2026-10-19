package com.mini.spring;

import java.beans.Introspector;

public interface ImportBeanDefinitionRegistrar {

    default void registerBeanDefinition(Class<?>cls,BeanDefinitionRegistry registry){
        String name= Introspector.decapitalize(cls.getSimpleName());
        registry.registerBeanDefinition(name,new BeanDefinition(name,cls));
    }

}
