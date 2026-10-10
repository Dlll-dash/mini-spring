package com.mini.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 同样是零魔法：这个注解自己【不会】注入任何东西。
 * 真正干活的是等下 MiniContext 里那个遍历字段、调 setAccessible(true) 的循环 —— 就是"人"。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
}
