package com.mini.spring.aop;

public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation);
}
