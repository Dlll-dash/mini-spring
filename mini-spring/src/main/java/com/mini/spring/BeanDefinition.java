package com.mini.spring;

public class BeanDefinition {

    private String beanName;

    private Class<?> beanType;

    public BeanDefinition(String beanName) {
        this.beanName = beanName;
    }

    public BeanDefinition(String beanName,Class<?> beanType){
        this.beanName=beanName;
        this.beanType=beanType;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }
}
