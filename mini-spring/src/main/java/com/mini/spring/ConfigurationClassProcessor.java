package com.mini.spring;

import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.ComponentScan;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;

public class ConfigurationClassProcessor implements BeanFactoryPostProcessor{

    @Override
    public void postProcessBeanFactory(BeanDefinitionRegistry registry) {
        for(String name:registry.getBeanDefinitionNames()){
            BeanDefinition bd=registry.getBeanDefinition(name);
            ComponentScan componentScan=bd.getBeanType().getAnnotation(ComponentScan.class);
            if(componentScan!=null){
                String[] basePackages=componentScan.value();
                if(basePackages.length==0){
                    //默认配置类所在包
                    scan(registry,bd.getBeanType().getPackageName());
                }else{
                    //扫描注解标明的所有包
                    for(String basePackage:basePackages){
                        scan(registry,basePackage);
                    }
                }
            }

        }

    }

    /**
     * 扫包
     * @param basePackage 根扫描包
     */
    private void scan(BeanDefinitionRegistry registry,String basePackage){
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
                    walk(registry,new File(url.toURI()), basePackage);
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
}
