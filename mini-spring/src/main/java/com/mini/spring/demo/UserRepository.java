package com.mini.spring.demo;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;


public interface UserRepository {

    public String findName(long id);

    public String orderOf(long id);
}
