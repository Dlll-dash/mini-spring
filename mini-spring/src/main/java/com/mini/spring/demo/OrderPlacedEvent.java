package com.mini.spring.demo;

import com.mini.spring.ApplicationEvent;

public class OrderPlacedEvent extends ApplicationEvent {

    OrderPlacedEvent(OrderService orderService){
        super(orderService);
    }

}
