package com.mini.spring.exception;

public class BeanAlreadyExistsException extends RuntimeException {
    public BeanAlreadyExistsException(String message) {
        super(message);
    }
}
