package com.mini.spring.demo;

import com.mini.spring.aop.MethodInterceptor;
import com.mini.spring.aop.MethodInvocation;

public class TimeInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) {
        System.out.println("[time] start");
        try {
            return invocation.proceed();
        } finally {
            System.out.println("[time] end");
        }
    }
}
