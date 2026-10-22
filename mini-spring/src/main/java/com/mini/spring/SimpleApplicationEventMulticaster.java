package com.mini.spring;

import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleApplicationEventMulticaster implements ApplicationEventMulticaster{

    private Set<String> applicationListenerBeans = new LinkedHashSet<>();

    private final MiniBeanFactory factory;

    SimpleApplicationEventMulticaster(MiniBeanFactory factory){
        this.factory=factory;
    }

    @Override
    public void addApplicationListenerBean(String listenerBeanName) {
        applicationListenerBeans.add(listenerBeanName);
    }

    /**
     * 广播事件，遍历容器内的listener，分发事件
     * @param event
     */
    @Override
    public void multicastEvent(ApplicationEvent event){

        for(String name : applicationListenerBeans){
            ApplicationListener<?> listener=factory.getBean(name);
            invoke(listener,event);
        }

    }

    /**
     * 通知监听器，某事件发生，对应的监听器进行反应
     * @param listener 原始类型，不带未知类型ApplicationListener<?>
     * @param event 需要通知的事件
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    private void invoke(ApplicationListener listener,ApplicationEvent event){
        listener.onApplicationEvent(event);
    }

}
