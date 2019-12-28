### 5 Spring Boot 基础

#### 5.1 核心功能
    * 独立运行 Spring 醒目
    * 内嵌 servlet 容器
    * 提供 starter 简化 Maven 配置
    * 自动配置 Spring
    * 提供基于 http、ssh、telnet 对运行时的项目进行监控
    * Java 配置 和 条件注解

#### 5.2 @SpringBootApplication 
    Spring Boot 项目核心注解，主要目的是开启自动配置
    
```
package chapter4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Run {
    public static void main(String[] args) {
        SpringApplication.run(Run.class, args);
    }

}
```    

看一下 @SpringBootApplication 源码, 可知 是一个组合注解。
Spring Boot 会自动扫描 @SpringBootApplication 所在类的同级包以及下级包里的 Bean；如果是 JPA 项目，还扫描标注 @Entity 的实体类。
一般情况下，推荐 入口类 放置在 groupId.artifactId 下。

```
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
    ...
}
```

#### 5.3 关闭特定的自动配置
```
// 关闭数据源自动配置
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) 
public class Run {
    public static void main(String[] args) {
        SpringApplication.run(Run.class, args);
    }
}
```

#### 5.4 自定义 banner

每次运行 Spring Boot 项目，我们都会看到启动图案。我们如果想换成自己的图案可以吗？当然。
* 在 src/main/resources 下创建 banner.txt, 里面保存好自定义内容。
* 图案生成网址：http://patorjk.com/software/taag 

```
                     .__                  
  ____   ____ _____  |  |   _____ _____   
 /    \_/ __ \\__  \ |  |  /     \\__  \  
|   |  \  ___/ / __ \|  |_|  Y Y  \/ __ \_
|___|  /\___  >____  /____/__|_|  (____  /
     \/     \/     \/           \/     \/ 
```
* 可以关闭 banner
```
public class Run {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Run.class);
        application.setBannerMode(Banner.Mode.OFF); // 关闭 banner
        application.run(args);
    }
}
```

#### 5.5 Spring Boot 配置文件

* application.properties
* application.yml


#### 5.6 starter pom

* spring-boot-starter 核心 starter，包含自动配置、日志、yaml配置文件的支持
* spring-boot-starter-actuator 用来监控和管理应用
* spring-boot-starter-remote-shell 提供基于ssh协议的监控和管理
* spring-boot-starter-amqp 使用 spring-rabbit 来支持AMQP
* spring-boot-starter-aop 使用 spring-aop 和 Aspectj 支持面向切面编程
* spring-boot-starter-batch 对 Spring Batch 的支持
* spring-boot-starter-cache 对 Spring Cache 的支持
.....

#### 5.7 引用外部 xml 配置文件 @ImportResource

```
@ImportResource({"classpath:a.xml", "classpath:b.xml"})
```

#### 5.8 日志配置

默认使用 LogBack 作为日志框架

* 路径 logging.file=/your/log/path/log.txt
* 级别 logging.level.org.springframework.web=INFO

#### 5.9 Profile 配置

全局 Profile 配置： application-{profile}.properties
* 开发 dev ： application-dev.properties
* 生产 prod ： application-prod.properties

激活指定的 Profile
```
spring.profiles.active=dev
```

#### 5.10 Spring Boot 运行原理

基于条件来配置 Bean

* 可以看源码 
org.springframework.boot.autoconfigure
* 查看日志
 1. 运行 jar 时，增加 --debug 参数
 2. 在 application.properties 中设置属性： debug=true
 3. idea VM options中设置 -Ddebug
 
```
# 已启动的自动配置
Positive matches:
-----------------

# 未启动的自动配置
Unconditional classes:
----------------------
``` 
