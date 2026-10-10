package com.mini.spring.demo;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;

@Component
public class OrderService {

    @Autowired
    UserRepository userRepository;

    public String orderOf(long userId) {
        return "order of " + userRepository.findName(userId);
    }
}
