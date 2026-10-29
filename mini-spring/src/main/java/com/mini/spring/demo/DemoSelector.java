package com.mini.spring.demo;

import com.mini.spring.DeferredImportSelector;
import com.mini.spring.ImportSelector;

public class DemoSelector implements DeferredImportSelector {
    @Override
    public Class<?>[] selectImports(Class<?> importClass) {
        return new Class[]{
                AutoConfig.class
        };
    }
}
