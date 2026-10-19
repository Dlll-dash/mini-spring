package com.mini.spring.transaction;

import com.mini.spring.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(TransactionConfigurationRegistrar.class)
public @interface EnableTransactionManagement {
}
