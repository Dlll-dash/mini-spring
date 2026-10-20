package com.mini.spring.aop;

public interface Advisor {

    Pointcut getPointcut();

    MethodInterceptor getAdvice();

}
