# mini-spring

一个从零手写的轻量级 IoC 容器，零第三方依赖，用于探究 Spring Framework 的核心机制。

## 简介

`mini-spring` 实现了与 Spring 一致的核心注解风格（`@Component` / `@Autowired`）与基于反射的依赖注入，
覆盖 Spring `ApplicationContext.refresh()` 流程中**包扫描 + BeanDefinition 注册**与**预实例化**两个环节。

## 功能

- **包扫描** —— 递归遍历指定包路径，识别 `@Component` 标注的类
- **BeanDefinition 注册** —— 将候选类登记为 Bean 定义，beanName 由类名首字母小写得到
- **依赖注入** —— 基于 `@Autowired` 的字段注入，递归解析依赖
- **单例管理** —— 一级缓存存放成品实例，二级缓存存放未完成实例，可处理循环依赖
- **异常体系** —— `NoSuchBeanDefinitionException`、`BeanAlreadyExistsException`

## 环境要求

- JDK 21（`maven.compiler.release=21`）
- Maven

## 构建与运行

```bash
cd mini-spring
mvn compile
java -cp target/classes com.mini.spring.demo.Main
```

输出：

```
>>>Create Bean: orderService
>>>Create Bean: userRepository
--- 验收①：扫描结果 ---
[orderService, userRepository]
--- 验收②：拿一个注入好的 bean ---
order of user#42
order of user#21
true
```

## 项目结构

```
mini-spring/
└── src/main/java/com/mini/spring/
    ├── MiniContext.java          # 容器：refresh / scan / getBean / createBean
    ├── BeanDefinition.java       # Bean 定义：beanName + beanType
    ├── annotation/
    │   ├── Component.java        # @Component
    │   └── Autowired.java        # @Autowired
    ├── exception/
    │   ├── NoSuchBeanDefinitionException.java
    │   └── BeanAlreadyExistsException.java
    └── demo/
        ├── Main.java             # 示例入口
        ├── OrderService.java
        └── UserRepository.java
```

## 实现说明

本实现刻意保持最小，以下几处与 Spring 的做法不同：

| 本实现 | Spring | 说明 |
| --- | --- | --- |
| 扫描时通过 `Class.forName` 加载类 | 只解析 `.class` 字节码，不加载类 | 无需应对 classpath 上大量第三方类，不存在类加载失败导致扫描中断的风险 |
| beanName 直接由 `Introspector.decapitalize` 生成 | `AnnotationBeanNameGenerator`，支持 `@Component("name")` 指定 | 本实现的 `@Component` 未定义 `value()` |
| 两级缓存 | 三级缓存 | 未实现 AOP，不存在"提前暴露引用以决定返回原生对象还是代理对象"的场景 |

## 状态

尚未实现：`BeanFactoryPostProcessor` / `BeanPostProcessor`、AOP、事件机制、作用域（prototype）等。
