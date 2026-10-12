package com.mini.spring.demo;

import com.mini.spring.BeanPostProcessor;
import com.mini.spring.annotation.Component;

@Component
public class OrderProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(beanName.equals("orderService")){
            return new WrappedOrderService((OrderService) bean);
        }
        return bean;
    }
}
