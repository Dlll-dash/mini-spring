package com.mini.spring.aop;

import com.mini.spring.BeanPostProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractAutoProxyCreator implements BeanPostProcessor {

    private final Map<Object,Object> earlyBeanReferences=new ConcurrentHashMap<>(16);

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        earlyBeanReferences.put(beanName,bean);
        return wrapIfNecessary(bean,beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(earlyBeanReferences.remove(beanName)!=bean) {
            return wrapIfNecessary(bean, beanName);
        }
        return bean;
    }

    private Object wrapIfNecessary(Object bean, String beanName){
        if(shouldWrap(bean.getClass(),beanName)){
            return createProxy(bean,beanName);
        }
        return bean;
    }

    /**
     * 子类重载决定启动代理的规则
     * @param beanName
     * @return
     */
    protected abstract boolean shouldWrap(Class<?>beanClass,String beanName);

    /**
     * 子类重载决定怎么代理
     * @param bean
     * @param beanName
     * @return
     */
    protected abstract Object createProxy(Object bean,String beanName);

}
