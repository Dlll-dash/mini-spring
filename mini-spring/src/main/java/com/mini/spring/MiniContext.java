package com.mini.spring;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;
import com.mini.spring.exception.BeanAlreadyExistsException;
import com.mini.spring.exception.NoSuchBeanDefinitionException;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class MiniContext {
    private final Map<String,BeanDefinition> beanDefinitions = new ConcurrentHashMap<>(256);

    private final Map<String,Object>earlySingletonObjects = new ConcurrentHashMap<>(256);

    private final Map<String,Object>singletonObjects=new ConcurrentHashMap<>(256);

    private final List<String>beanDefinitionNames=new ArrayList<>(256);

    private final String basePackages;

    public MiniContext(String basePackage){this.basePackages=basePackage;}

    public String[] getBeanDefinitionNames(){return beanDefinitionNames.toArray(new String[0]);}

    private <T> T createBean(String beanName,Class<T> beanType){
        try {
            System.out.println(">>>Create Bean: "+beanName);

            T obj = (T)beanType.getDeclaredConstructor().newInstance();
            //先存入二级缓存，解决循环依赖

            earlySingletonObjects.putIfAbsent(beanName, obj);

            for(Field f: beanType.getDeclaredFields()){
                //拿到被@Autowired注解的属性列表
                if(f.getAnnotation(Autowired.class)!=null){
                    f.setAccessible(true);
                    try {
                        //递归注入
                        f.set(obj, getBean(f.getType()));
                    }catch (IllegalAccessException e){
                        throw new RuntimeException(e);
                    }
                }
            }
            //从二级缓存拿出来
            Object oldObject = singletonObjects.putIfAbsent(beanName,obj);
            if(oldObject!=null){
                throw new BeanAlreadyExistsException("there is already object "+beanName);
            }
            earlySingletonObjects.remove(beanName);

            return obj;
        }catch(InstantiationException | IllegalAccessException | InvocationTargetException| NoSuchMethodException e){
            throw new RuntimeException(e);
        }
    }

    public <T> T getBean(String beanName){
        //先查一级缓存
        Object cached=singletonObjects.get(beanName);
        if(cached!=null){
            return (T)cached;
        }
        //二级缓存
        Object earlyCached=earlySingletonObjects.get(beanName);
        if(earlyCached!=null){
            return (T)earlyCached;
        }
        //拿配方新造一个
        BeanDefinition bd=beanDefinitions.get(beanName);
        if(bd==null){
            throw new NoSuchBeanDefinitionException("BeanDefinition Not Found");
        }
        return (T)createBean(beanName,bd.getBeanType());
    }

    public <T> T getBean(Class<T> type){

        for(BeanDefinition bd:beanDefinitions.values()){
            Class<?> beanType=bd.getBeanType();
            if(beanType==type){
                String beanName=bd.getBeanName();
                return (T) getBean(beanName);
            }
        }

        throw new NoSuchBeanDefinitionException("BeanDefinition Not Found");
    }

    public void refresh(){
        //扫包
        scan(basePackages);
//        obtainFreshBeanFactory();
//
//        postProcessBeanFactory();
//
//        invokeBeanFactoryPostProcessors();
//
//        registerBeanPostProcessors();
//
//        onRefresh();
//
//        finishBeanFactoryInitialization();
        //预实例化
        for(String beanName:beanDefinitionNames){
            getBean(beanName);
        }
    }

    void scan(String basePackage){
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
                    walk(new File(url.toURI()), basePackage);
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

    private void walk(File dir, String packageName) throws ClassNotFoundException {
        File[] files=dir.listFiles();
        if(files==null)return;
        Arrays.sort(files);
        for(File f : files){
            String newPackageName=packageName+"."+f.getName();
            if(f.isDirectory()){
                walk(f,newPackageName);
            }else if(f.getName().endsWith(".class")){
                String className=newPackageName.replace(".class","");
                //这里在扫描时就加载类，而spring不会在扫描时加载,spring只会加载注解元数据
                //目前只是mini版，没有依赖，扫描时加载不会导致崩掉
                Class<?> c=Class.forName(className);
                if(c.isAnnotationPresent(Component.class)){
                    //注册(暂时)
                    String beanName= Introspector.decapitalize(f.getName().replace(".class",""));
                    beanDefinitions.put(beanName,new BeanDefinition(beanName,c));
                    beanDefinitionNames.add(beanName);
                }
            }
        }
    }
}
