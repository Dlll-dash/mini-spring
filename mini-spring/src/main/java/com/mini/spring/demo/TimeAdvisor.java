package com.mini.spring.demo;

import com.mini.spring.annotation.Component;
import com.mini.spring.aop.Advisor;
import com.mini.spring.aop.MethodInterceptor;
import com.mini.spring.aop.Pointcut;

@Component
public class TimeAdvisor implements Advisor {
    @Override
    public Pointcut getPointcut() {
        return new TimePointcut();
    }

    @Override
    public MethodInterceptor getAdvice() {
        return new TimeInterceptor();
    }
}
