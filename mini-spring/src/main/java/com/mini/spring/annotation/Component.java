package com.mini.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 和 Spring 的 @Component 长得一样 —— 因为它的全部内容就是"被留在运行时"。
 * 真正有信息量的只有下面 @Retention 那一行：没有 RUNTIME，反射就读不到它，扫描也就无从谈起。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
}
