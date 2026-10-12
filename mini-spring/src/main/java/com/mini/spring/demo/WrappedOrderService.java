package com.mini.spring.demo;


public class WrappedOrderService extends OrderService{
    private final OrderService target;

    public WrappedOrderService(OrderService target) {
        this.target = target;
    }

    @Override
    public String orderOf(long userId) {
        return "Wrapped>>"+target.orderOf(userId);
    }
}
