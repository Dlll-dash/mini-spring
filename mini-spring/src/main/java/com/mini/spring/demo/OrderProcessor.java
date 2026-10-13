package com.mini.spring.demo;

import com.mini.spring.annotation.Component;
import com.mini.spring.aop.AbstractAutoProxyCreator;

@Component
public class OrderProcessor extends AbstractAutoProxyCreator {

    @Override
    protected boolean shouldWrap(String beanName) {
        return beanName.equals("orderService");
    }

    @Override
    protected Object createProxy(Object bean, String beanName) {
        return new WrappedOrderService((OrderService) bean);
    }
}
