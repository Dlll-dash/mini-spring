package com.mini.spring;

public class BeanDefinition {

    private String beanName;

    private Class<?> beanType;

    private Object[] constructArgumentValues;

    public BeanDefinition(String beanName,Class<?> beanType){
        this.beanName=beanName;
        this.beanType=beanType;
    }

    public BeanDefinition(String beanName,Class<?> beanType,Object[] args){
        this.beanName=beanName;
        this.beanType=beanType;
        this.constructArgumentValues=args;
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

    public Object[] getConstructArgumentValues(){
        return constructArgumentValues;
    }
}
