package com.mini.spring;

public class ContextClosedEvent extends ApplicationEvent{
    ContextClosedEvent(Object source){
        super(source);
    }
}
