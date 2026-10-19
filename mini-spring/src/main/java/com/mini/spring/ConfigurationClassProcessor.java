package com.mini.spring;

import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.ComponentScan;
import com.mini.spring.annotation.Import;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class ConfigurationClassProcessor implements BeanFactoryPostProcessor{

    /**
     * 扫包，初始化BeanFactory
     * @param registry
     */
    @Override
    public void postProcessBeanFactory(BeanDefinitionRegistry registry) {
        for(String name:registry.getBeanDefinitionNames()){
            BeanDefinition bd=registry.getBeanDefinition(name);
            ComponentScan componentScan=bd.getBeanType().getAnnotation(ComponentScan.class);
            Set<Class<?>> imports=new LinkedHashSet<>();

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

            //收集Import导入类
            collectImports(bd.getBeanType(),imports,new HashSet<>());
            if(imports.size()>0){
                for(Class<?> cls:imports){
                    if(ImportBeanDefinitionRegistrar.class.isAssignableFrom(cls)){
                        try {
                            ImportBeanDefinitionRegistrar imp = (ImportBeanDefinitionRegistrar) cls.getDeclaredConstructor().newInstance();
                            imp.registerBeanDefinition(cls,registry);
                        }catch (NoSuchMethodException e){
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
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

    private void collectImports(Class<?> cls,Set<Class<?>>imports,Set<Class<?>> visited){
        if(visited.contains(cls)){
            return;
        }
        Annotation[] annotations = cls.getAnnotations();
        visited.add(cls);
        Import imp=cls.getAnnotation(Import.class);

        //递归元注解
        for(Annotation ann : annotations){
            if(ann.annotationType()!=Import.class) {
                collectImports(ann.annotationType(), imports, visited);
            }
        }

        //加入集合
        if(imp!=null){
            Class<?>[]classes=imp.value();
            imports.addAll(List.of(classes));
        }

    }

}
