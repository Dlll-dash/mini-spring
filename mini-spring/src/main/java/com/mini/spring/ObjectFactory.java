package com.mini.spring;

@FunctionalInterface
public interface ObjectFactory<T> {
    /**
     * 工厂类生产方法
     * @return
     */
    T getObject();

}
