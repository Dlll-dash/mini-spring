package com.mini.spring;

public interface ApplicationEventMulticaster {

    void addApplicationListenerBean(String listenerBeanName);

    void multicastEvent(ApplicationEvent event);
}
