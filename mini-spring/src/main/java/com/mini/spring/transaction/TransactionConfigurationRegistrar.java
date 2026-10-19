package com.mini.spring.transaction;

import com.mini.spring.BeanDefinition;
import com.mini.spring.BeanDefinitionRegistry;
import com.mini.spring.ImportBeanDefinitionRegistrar;

public class TransactionConfigurationRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinition(Class<?> cls, BeanDefinitionRegistry registry) {
        String name="transactionInterceptor";
        registry.registerBeanDefinition(name,
                new BeanDefinition(name, TransactionInterceptor.class));
    }
}
