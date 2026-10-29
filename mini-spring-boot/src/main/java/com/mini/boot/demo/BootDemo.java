package com.mini.boot.demo;

import com.mini.boot.context.annotation.AutoConfigurationImportSelector;
import com.mini.spring.MiniContext;
import com.mini.spring.annotation.Import;

import java.util.Arrays;

@Import(AutoConfigurationImportSelector.class)
public class BootDemo {
    public static void main(String[] args){
        MiniContext ctx = new MiniContext(BootDemo.class);
        System.out.println(Arrays.toString(ctx.getBeanDefinitionNames()));
        System.out.println((String) ctx.getBean("autoGreeting"));
    }
}
