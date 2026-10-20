package com.mini.spring.demo;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.PostConstruct;
import com.mini.spring.annotation.Transactional;

public interface OrderService {

    public String orderOf(long userId);

    public String cancel(long userId);

    public String describe(long userId);
}
