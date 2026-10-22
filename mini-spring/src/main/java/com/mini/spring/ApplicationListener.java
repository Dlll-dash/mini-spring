package com.mini.spring;

public interface ApplicationListener<E extends ApplicationEvent> {

    void onApplicationEvent(E event);

}
