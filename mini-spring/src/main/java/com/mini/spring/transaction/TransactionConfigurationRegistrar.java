package com.mini.spring.transaction;

import com.mini.spring.BeanDefinition;
import com.mini.spring.BeanDefinitionRegistry;
import com.mini.spring.ImportBeanDefinitionRegistrar;

public class TransactionConfigurationRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinition(Class<?> cls, BeanDefinitionRegistry registry) {
        String PROXY_CREATOR="autoProxyCreator";
        registry.registerBeanDefinition(PROXY_CREATOR,new BeanDefinition(PROXY_CREATOR,InternalAutoProxyCreator.class));

        String ADVISOR="transactionAdvisor";
        registry.registerBeanDefinition(ADVISOR,new BeanDefinition(ADVISOR,TransactionAdvisor.class));


    }
}
