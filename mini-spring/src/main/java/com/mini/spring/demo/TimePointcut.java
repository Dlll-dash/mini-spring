package com.mini.spring.demo;

import com.mini.spring.aop.Pointcut;

import java.lang.reflect.Method;

public class TimePointcut implements Pointcut {
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        try {
            Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
            return targetMethod.isAnnotationPresent(Timed.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
