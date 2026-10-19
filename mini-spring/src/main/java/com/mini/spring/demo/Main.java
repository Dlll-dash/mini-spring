package com.mini.spring.demo;

import com.mini.spring.MiniContext;
import com.mini.spring.annotation.ComponentScan;
import com.mini.spring.transaction.EnableTransactionManagement;

import java.util.Arrays;

@EnableTransactionManagement
@ComponentScan({"com.mini.spring.exception","com.mini.spring.demo"})
public class Main {

    public static void main(String[] args) {
        var ctx = new MiniContext(Main.class);

        System.out.println("---①：配方名单---");
        System.out.println(Arrays.toString(ctx.getBeanDefinitionNames()));

        System.out.println("---②：测试循环依赖注入bean---");
        System.out.println(ctx.getBean(OrderService.class).orderOf(42)+"\n"+ctx.getBean(UserRepository.class).orderOf(21));

    }
}
