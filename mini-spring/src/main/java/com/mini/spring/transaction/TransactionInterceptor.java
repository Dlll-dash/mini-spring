package com.mini.spring.transaction;

import com.mini.spring.aop.MethodInterceptor;
import com.mini.spring.aop.MethodInvocation;

public class TransactionInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation){
        System.out.println("[tx] begin");
        Object result;
        try {
            result = invocation.proceed();
        }catch(Throwable e){
            System.out.println("[tx] rollback");
            throw e;
        }
        System.out.println("[tx] commit");
        return result;
    }

}
