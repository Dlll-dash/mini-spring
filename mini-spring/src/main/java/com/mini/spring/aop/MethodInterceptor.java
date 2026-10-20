package com.mini.spring.aop;

public interface MethodInterceptor {
    /**
     * 拦截器调用方法
     * @param invocation
     * @return
     */
    Object invoke(MethodInvocation invocation);
}
