package com.mini.spring.demo;


import com.mini.spring.aop.MethodInterceptor;

public class WrappedOrderService extends OrderService{
    private final OrderService target;

    private final MethodInterceptor interceptor;

    public WrappedOrderService(OrderService target, MethodInterceptor interceptor) {
        this.target = target;
        this.interceptor=interceptor;
    }

    @Override
    public String orderOf(long userId) {
        return "Wrapped@"+System.identityHashCode(this)+">>"+
                interceptor.invoke(()->target.orderOf(userId));
    }
}
