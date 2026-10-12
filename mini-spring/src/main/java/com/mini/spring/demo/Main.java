package com.mini.spring.demo;

import com.mini.spring.MiniContext;

import java.util.Arrays;

/**
 * 这就是验收标准，别改。今天只要上面那段打印出来就算过 —— 下面那段【故意会炸】。
 */
public class Main {

    public static void main(String[] args) {
        var ctx = new MiniContext("com.mini.spring.demo");
        ctx.refresh();

        System.out.println("---①：扫描结果---");
        System.out.println(Arrays.toString(ctx.getBeanDefinitionNames()));

        System.out.println("---②：拿一个注入好的 bean---");
        System.out.println(ctx.getBean(OrderService.class).orderOf(42)+"\n"+ctx.getBean(UserRepository.class).orderOf(21));
        System.out.println(ctx.getBean(OrderService.class) == ctx.getBean(OrderService.class));
    }
}
