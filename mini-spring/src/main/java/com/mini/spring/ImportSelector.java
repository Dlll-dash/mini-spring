package com.mini.spring;

public interface ImportSelector {

    Class<?>[] selectImports(Class<?> importClass);

}
