package com.mini.spring;

public interface BeanFactoryAware {

    /**
     * 将工厂容器赋值给代理类的字段
     * @param factory
     */
    void setBeanFactory(MiniBeanFactory factory);

}
