package com.mini.spring.exception;

public class NoUniqueBeanDefinitionException extends NoSuchBeanDefinitionException{
    public NoUniqueBeanDefinitionException(String msg){super(msg);}
}
