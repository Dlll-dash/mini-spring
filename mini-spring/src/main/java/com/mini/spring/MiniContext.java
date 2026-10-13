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

    private final Map<String,ObjectFactory<?>>singletonFactories= new ConcurrentHashMap<>(256);

    private final Map<String,Object>earlySingletonObjects = new ConcurrentHashMap<>(256);

    private final Map<String,Object>singletonObjects=new ConcurrentHashMap<>(256);

    private final List<String>beanDefinitionNames=new ArrayList<>(256);

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(256);

    private final String basePackages;

    public MiniContext(String basePackage){this.basePackages=basePackage;}

    public String[] getBeanDefinitionNames(){return beanDefinitionNames.toArray(new String[0]);}

    /**
     * 创建bean
     * @param beanName
     * @param beanType
     * @return
     * @param <T>
     */
    private <T> T createBean(String beanName,Class<T> beanType){
        try {
            //System.out.println(">>>Create Bean: "+beanName);

            T obj = (T)beanType.getDeclaredConstructor().newInstance();
            T finalObj = obj;

            //lambda 实现ObjectFactory接口，重载getObject方法：返回经过PostProcessors处理过的代理
            singletonFactories.put(beanName,()-> getEarlyBeanReference(finalObj,beanName));

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

            //如果已经有代理则不会生效
            obj=applyBeanPostProcessorsAfterInitialization(obj,beanName);

            //判断二级是否已有代理，有就替换。解决的是循环依赖中代理的一致性问题
            Object earlySingleton=earlySingletonObjects.get(beanName);
            if(earlySingleton!=null && obj==finalObj){
                obj=(T)earlySingleton;
            }

            //存入一级缓存时并检查是否已存在bean
            Object oldObject = singletonObjects.putIfAbsent(beanName,obj);
            if(oldObject!=null){
                throw new BeanAlreadyExistsException("there is already object "+beanName);
            }
            //移出三级缓存
            singletonFactories.remove(beanName);
            //移出二级缓存
            earlySingletonObjects.remove(beanName);

            return obj;
        }catch(InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用后置处理器的初始后方法
     * @param obj
     * @param beanName
     * @return
     * @param <T>
     */
    private <T> T applyBeanPostProcessorsAfterInitialization(T obj, String beanName) {
        //遍历处理器
        for(BeanPostProcessor pp:beanPostProcessors){
            obj =(T)pp.postProcessAfterInitialization(obj,beanName);
        }
        return obj;
    }

    /**
     * 通过bean名称获得实例 O(1)查询，没查到就新建
     * @param beanName
     * @return
     * @param <T>
     */
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
        //三级缓存
        ObjectFactory<?> f=singletonFactories.get(beanName);
        if(f!=null){
            Object earlyBean = f.getObject();
            if(singletonFactories.remove(beanName)!=null){
                earlySingletonObjects.put(beanName,earlyBean);
                return (T)earlyBean;
            }
        }

        //拿配方新造一个
        BeanDefinition bd=beanDefinitions.get(beanName);
        if(bd==null){
            throw new NoSuchBeanDefinitionException("BeanDefinition Not Found");
        }
        return (T)createBean(beanName,bd.getBeanType());
    }

    /**
     * 通过类型获得bean实例 有O(N)的代价
     * @param type
     * @return
     * @param <T>
     */
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

        //注册BPP
        registerBeanPostProcessors();

//        onRefresh();
//
        finishBeanFactoryInitialization();
    }

    /**
     * 注册后置处理器
     */
    private void registerBeanPostProcessors(){
        //使用beanName遍历，因为bDNs是扫包得到的集合（确定但无语义），方便问题溯源
        for(String name:beanDefinitionNames){
            Class<?> type=beanDefinitions.get(name).getBeanType();
            if(BeanPostProcessor.class.isAssignableFrom(type)){
                //使用beanName查询 O(1)
                beanPostProcessors.add(getBean(name));
            }
        }
    }

    private void finishBeanFactoryInitialization() {
        //预实例化
        for(String beanName:beanDefinitionNames){
            getBean(beanName);
        }
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

    /**
     * 递归扫包
     * @param dir
     * @param packageName
     * @throws ClassNotFoundException
     */
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

    private Object getEarlyBeanReference(Object bean,String beanName){
        for(BeanPostProcessor pp:beanPostProcessors){
            bean = pp.getEarlyBeanReference(bean,beanName);
        }
        return bean;
    }
}
