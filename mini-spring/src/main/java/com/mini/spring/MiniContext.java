package com.mini.spring;

import com.mini.spring.annotation.Component;


import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


public class MiniContext {

    private final MiniBeanFactory factory = new MiniBeanFactory();

    private final String basePackages;

    public MiniContext(String basePackage){this.basePackages=basePackage;}



    public void refresh(){
        //扫包
        scan(basePackages);
//        obtainFreshBeanFactory();
//
//        postProcessBeanFactory();
//
        invokeBeanFactoryPostProcessors();

        //注册BPP
        registerFrameworkBeanPostProcessor();
        registerBeanPostProcessors();

//        onRefresh();
//
        finishBeanFactoryInitialization();
    }

    private void invokeBeanFactoryPostProcessors() {
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

    private void finishBeanFactoryInitialization() {
        //预实例化
        for(String beanName: factory.getBeanDefinitionNames()){
            factory.getBean(beanName);
        }
    }

    /**
     * 注册框架内的BPP
     */
    private void registerFrameworkBeanPostProcessor(){
        String name="initAnnotationBeanPostProcessor";
        factory.registerBeanDefinition(name,new BeanDefinition(name, InitAnnotationBeanPostProcessor.class));
        //...

    }

    /**
     * 扫包
     * @param basePackage 根扫描包
     */
    private void scan(String basePackage){
        String path=basePackage.replace('.','/');
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(path);
            if(!urls.hasMoreElements()){
                throw new IllegalStateException("Cannot find resources");
            }
            while(urls.hasMoreElements()){
                URL url=urls.nextElement();
                if(!url.getProtocol().equals("file"))continue;
                try {
                    //递归处理
                    walk(this.factory,new File(url.toURI()), basePackage);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e){
                    throw new RuntimeException("URL Syntax Error");
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    /**
     * 递归扫包
     * @param dir
     * @param packageName
     * @throws ClassNotFoundException
     */
    private void walk(BeanDefinitionRegistry registry,File dir, String packageName) throws ClassNotFoundException {
        File[] files=dir.listFiles();
        if(files==null)return;
        Arrays.sort(files);
        for(File f : files){
            String newPackageName=packageName+"."+f.getName();
            if(f.isDirectory()){
                walk(registry,f,newPackageName);
            }else if(f.getName().endsWith(".class")){
                String className=newPackageName.replace(".class","");
                //这里在扫描时就加载类，而spring不会在扫描时加载,spring只会加载注解元数据
                //目前只是mini版，没有依赖，扫描时加载不会导致崩掉
                Class<?> c=Class.forName(className);
                if(c.isAnnotationPresent(Component.class)){
                    //注册(暂时)
                    String beanName= Introspector.decapitalize(f.getName().replace(".class",""));
                    registry.registerBeanDefinition(beanName,new BeanDefinition(beanName,c));
                }
            }
        }
    }

    public <T> T getBean(String name){
        return factory.getBean(name);
    }

    public <T> T getBean(Class<T> type){
        return factory.getBean(type);
    }

    public String[] getBeanDefinitionNames(){
        return factory.getBeanDefinitionNames();
    }



}
