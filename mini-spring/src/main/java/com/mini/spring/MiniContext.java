package com.mini.spring;

import java.beans.Introspector;


public class MiniContext {

    private final MiniBeanFactory factory = new MiniBeanFactory();

    private ApplicationEventMulticaster applicationEventMulticaster;

    /**
     * 初始化容器
     * @param configClass 运行主类
     */
    public MiniContext(Class<?> configClass){
        //注册包所在位置
        register(configClass);
        //注册框架工厂后置处理器
        registerFrameworkBeanFactoryPostProcessor();

        refresh();
    }

    /**
     * 注册根包路径
     * @param configClass
     */
    private void register(Class<?> configClass){
        String name= Introspector.decapitalize(configClass.getSimpleName());
        factory.registerBeanDefinition(name,new BeanDefinition(name, configClass));
    }

    /**
     * 运行方法
     */
    public void refresh(){

        //调用BFPP，扫包注册BeanDefinition
        invokeBeanFactoryPostProcessors();

        //注册BPP到 beanDefinitons 中
        registerFrameworkBeanPostProcessor();

        //注册BPP beanDefiniton 到 BPP 队列中
        registerBeanPostProcessors();

        //初始化多播器
        initApplicationEventMulticaster();

        //注册监听器
        registerListeners();

        //onRefresh();

         //完成bean初始化
        finishBeanFactoryInitialization();

        //结束refresh，广播事件
        finishRefresh();
    }



    /**
     * 调用工厂后置处理器
     */
    private void invokeBeanFactoryPostProcessors() {
        for(String beanName:factory.getBeanDefinitionNames()){
            //在调用BFPP之前，不能实例化任何Bean
            BeanDefinition bd = factory.getBeanDefinition(beanName);
            if(BeanFactoryPostProcessor.class.isAssignableFrom(bd.getBeanType())) {
                Object bean=factory.getBean(beanName);
                ((BeanFactoryPostProcessor)bean).postProcessBeanFactory(factory);
            }
        }
    }

    /**
     * 注册后置处理器
     */
    private void registerBeanPostProcessors(){
        //使用beanName遍历，因为bDNs是扫包得到的集合（确定但无语义），方便问题溯源
        for(String name:factory.getBeanDefinitionNames()){
            Class<?> type=factory.getBeanDefinition(name).getBeanType();
            if(BeanPostProcessor.class.isAssignableFrom(type)){
                factory.addBeanPostProcessor(factory.getBean(name));
            }
        }
    }


    /**
     * 注册多播器
     */
    private void initApplicationEventMulticaster() {
        ApplicationEventMulticaster multicaster=new SimpleApplicationEventMulticaster(factory);
        this.applicationEventMulticaster=multicaster;
    }

    /**
     * 注册监听器到多播器内部容器中，但不要初始化bean
     */
    private void registerListeners() {
        //只获得bean名称，不初始化bean
        String[] names = factory.getBeanNamesForType(ApplicationListener.class);
        for(String name:names){
            applicationEventMulticaster.addApplicationListenerBean(name);
        }
    }

    private void finishBeanFactoryInitialization() {
        //预实例化
        for(String beanName: factory.getBeanDefinitionNames()){
            factory.getBean(beanName);
        }
    }

    /**
     * 结束refresh前广播事件
     */
    private void finishRefresh() {
        publishEvent(new ContextRefreshedEvent(this));
    }

    /**
     * 广播事件
     * @param event
     */
    private void publishEvent(ApplicationEvent event){
        applicationEventMulticaster.multicastEvent(event);
    }

    /**
     * 注册框架内的BPP
     */
    private void registerFrameworkBeanPostProcessor(){
        String INIT_ANNOTATION="initAnnotationBeanPostProcessor";
        factory.registerBeanDefinition(INIT_ANNOTATION,new BeanDefinition(INIT_ANNOTATION, InitAnnotationBeanPostProcessor.class));
        //...

    }

    /**
     * 注册框架内的BFPP
     */
    private void registerFrameworkBeanFactoryPostProcessor(){
        String CONFIGURATION_CLASS="configurationClassPostProcessor";
        factory.registerBeanDefinition(CONFIGURATION_CLASS,new BeanDefinition(CONFIGURATION_CLASS,ConfigurationClassProcessor.class));
        //...
    }

    /**
     * 暴露给外面的接口代理
     * @param name
     * @return
     * @param <T>
     */
    public <T> T getBean(String name){
        return factory.getBean(name);
    }

    /**
     * 暴露给外面的接口代理
     * @param type
     * @return
     * @param <T>
     */
    public <T> T getBean(Class<T> type){
        return factory.getBean(type);
    }

    /**
     * 暴露给外面的接口代理
     * @return
     */
    public String[] getBeanDefinitionNames(){
        return factory.getBeanDefinitionNames();
    }



}
