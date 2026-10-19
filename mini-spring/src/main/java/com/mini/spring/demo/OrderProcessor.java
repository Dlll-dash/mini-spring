package com.mini.spring.demo;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.Transactional;
import com.mini.spring.aop.AbstractAutoProxyCreator;
import com.mini.spring.aop.MethodInterceptor;

import java.lang.reflect.Method;

@Component
public class OrderProcessor extends AbstractAutoProxyCreator {

    @Autowired
    MethodInterceptor interceptor;

    @Override
    protected boolean shouldWrap(Class<?> beanClass,String beanName) {

            for(Method method:beanClass.getDeclaredMethods()){
                if(method.isAnnotationPresent(Transactional.class)){
                    return true;
                }
            }

        return false;
    }

    @Override
    protected Object createProxy(Object bean, String beanName) {
        return new WrappedOrderService((OrderService) bean,interceptor);
    }
}
