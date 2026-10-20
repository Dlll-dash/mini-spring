package com.mini.spring.transaction;

import com.mini.spring.aop.Advisor;
import com.mini.spring.aop.MethodInterceptor;
import com.mini.spring.aop.Pointcut;

public class TransactionAdvisor implements Advisor {
    private Pointcut pointcut;
    private MethodInterceptor advice;

    @Override
    public Pointcut getPointcut() {
        if(pointcut !=null) {
            return this.pointcut;
        }
        return this.pointcut =new TransactionPointcut();
    }

    @Override
    public MethodInterceptor getAdvice() {
        if(advice!=null) {
            return this.advice;
        }
        return this.advice=new TransactionInterceptor();
    }
}
