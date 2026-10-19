package com.mini.spring.demo;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.PostConstruct;
import com.mini.spring.annotation.Transactional;

@Component
public class OrderService {

    @Autowired
    UserRepository userRepository;

    @PostConstruct
    public void init(){
        System.out.println("@PostConstruct>>OrderService");
    }

    @Transactional
    public String orderOf(long userId) {
        return "order of " + userRepository.findName(userId);
    }
}
