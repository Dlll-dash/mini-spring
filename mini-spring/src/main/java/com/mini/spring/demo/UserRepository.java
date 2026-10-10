package com.mini.spring.demo;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;

@Component
public class UserRepository {
    @Autowired
    private OrderService orderService;

    public String findName(long id) {
        return "user#" + id;
    }

    public String orderOf(long id){return orderService.orderOf(id);}
}
