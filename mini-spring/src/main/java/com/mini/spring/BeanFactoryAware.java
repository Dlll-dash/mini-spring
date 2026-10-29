package com.mini.spring;

public interface BeanFactoryAware extends Aware {

    /**
     * 将工厂容器赋值给代理类的字段
     * @param factory
     */
    void setBeanFactory(MiniBeanFactory factory);

}
