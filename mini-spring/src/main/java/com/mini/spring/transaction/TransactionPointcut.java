package com.mini.spring.transaction;

import com.mini.spring.annotation.Transactional;
import com.mini.spring.aop.Pointcut;

import java.lang.reflect.Method;

public class TransactionPointcut implements Pointcut {
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        try {
            Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
            return targetMethod.isAnnotationPresent(Transactional.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
