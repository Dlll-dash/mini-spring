# mini-spring-boot

用"自己写一遍"的方式学透 Spring。

读源码很容易产生"我懂了"的幻觉——代码不会骗人，能写出来才算数。这个仓库就是把这个标准落地的过程：
每在 Spring 源码里挖穿一个机制，就立刻在 `mini-spring/` 里手写一个对应实现，跑通再往下走。

最终目标是两个手写版本：

| 目标 | 对应 | 状态 |
| --- | --- | --- |
| `mini-spring` | Spring Framework 的 IoC + AOP | 🚧 进行中（IoC 主干已跑通） |
| `mini-spring-boot` | Spring Boot 的自动配置 + 内嵌容器 | ⬜ 未开始 |

## 当前进度

已跑通 `mini-spring` 的最小 IoC 容器，覆盖 Spring `refresh()` 12 步里的**第 5 步**和**第 11 步**：

- ✅ **包扫描** —— 递归目录树找到 `@Component` 标注的类
- ✅ **BeanDefinition 注册** —— 类名 → `Introspector.decapitalize` 得到 beanName，登记进"配方表"
- ✅ **依赖注入** —— 遍历字段上的 `@Autowired`，反射 `setAccessible(true)` + 递归 `getBean`
- ✅ **循环依赖** —— 一级缓存（成品）+ 二级缓存（半成品）两级方案
- ⬜ **AOP** —— 还没有，所以没有三级缓存（没有代理，"提前暴露引用"就没有意义）

`refresh()` 里其余 10 步目前是**空注释钩子**，刻意的——mini 版不照抄 12 步，只立主干。

## 目录结构

```
mini-spring/
└── src/main/java/com/mini/spring/
    ├── MiniContext.java          # 容器本体：refresh / scan / getBean / createBean
    ├── BeanDefinition.java       # 配方：beanName + beanType
    ├── annotation/
    │   ├── Component.java        # 自己定义的 @Component
    │   └── Autowired.java        # 自己定义的 @Autowired
    ├── exception/
    │   ├── NoSuchBeanDefinitionException.java
    │   └── BeanAlreadyExistsException.java
    └── demo/
        ├── Main.java             # 验收标准，别改
        ├── OrderService.java     # @Autowired UserRepository
        └── UserRepository.java   # @Autowired OrderService（故意构成循环依赖）
```

## 运行

需要 **JDK 21**（`pom.xml` 里 `maven.compiler.release=21`）。

```bash
cd mini-spring
mvn compile
java -cp target/classes com.mini.spring.demo.Main
```

预期输出：

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

三个验收点：

1. **`[orderService, userRepository]`** —— 扫描 + 注册生效，而且顺序稳定（`walk()` 里做了 `Arrays.sort`）
2. **`order of user#42`** —— `OrderService` 里的 `userRepository` 字段被真的注进去了
3. **`true`** —— 单例；两次 `getBean` 拿到同一个对象

`UserRepository` 和 `OrderService` 互相 `@Autowired`，`>>>Create Bean` 只各打印一次——二级缓存在这里挡住了 `StackOverflowError`。

## 关于"零依赖"

`mini-spring/pom.xml` 里没有任何 `<dependencies>`，这是故意的：

1. 手写的容器不能借 Spring 的实现，否则分不清哪些是我写的、哪些是它干的
2. 顺便亲眼确认：`@Component` / `@Autowired` 这些注解**本身一分钱魔法都没有**，就是个标记，
   唯一有信息量的是 `@Retention(RUNTIME)`——没有它反射就读不到。真正干活的是遍历字段、
   调 `setAccessible(true)` 的那个循环。

## 与 Spring 的有意差异

手写版不是照抄，凡是简化都记下"为什么这里能省"：

| 这里 | Spring 的做法 | 为什么 mini 不用 |
| --- | --- | --- |
| 扫描时 `Class.forName` 加载类 | 只读 `.class` 字节码（ASM），不加载 | Spring 要扫整个 classpath 上万个类，加载会因一个缺依赖的类整个崩掉；mini 没有这个压力，也没有这个手段（JDK 无"读注解不加载类"的公开 API） |
| beanName 直接 `decapitalize` | `AnnotationBeanNameGenerator` 支持 `@Component("x")` | mini 的 `@Component` 没有 `value()`，永远走默认名 |
| 两级缓存 | 三级（第三级挂工厂 lambda，延迟决定交原生还是交代理） | 没有 AOP ⇒ `getEarlyBeanReference` 是恒等函数 ⇒ 挂 lambda 和挂对象结果一样 |

## 学习路线

**原则：串行，不并行。**

- **阶段一 · Spring Framework 的 IoC / AOP**（约 60% 精力）
  只读 `spring-core` / `spring-beans` / `spring-context`。主干链路：
  `AnnotationConfigApplicationContext.refresh()` → `ConfigurationClassPostProcessor` →
  `ClassPathBeanDefinitionScanner.doScan()` → `finishBeanFactoryInitialization` →
  `AbstractAutowireCapableBeanFactory.doCreateBean()`
- **阶段二 · Spring Boot 的 4 件事**（约 40% 精力）
  自动配置 / Starter / 内嵌容器 / 外部化配置。

## 环境约束

- 源码仓库 `spring-framework` / `spring-boot` 只当字典查（IDEA 全局搜索），**不在里面构建**：
  `spring-framework` 的主构建硬编码要求 JDK 17 toolchain，且启用了 `foojay-resolver-convention`，
  会去 api.foojay.io 自动下 JDK。学习调试一律在本仓库里跑。
- 本机 JDK：`jdk-21`（构建用）+ `jdk-1.8`，**没有 JDK 17**。
