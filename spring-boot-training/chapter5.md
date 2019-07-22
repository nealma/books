### 5 Spring Boot 基础

#### 核心功能
    * 独立运行 Spring 醒目
    * 内嵌 servlet 容器
    * 提供 starter 简化 Maven 配置
    * 自动配置 Spring
    * 提供基于 http、ssh、telnet 对运行时的项目进行监控
    * Java 配置 和 条件注解

#### @SpringBootApplication 
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

#### 关闭特定的自动配置
```
// 关闭数据源自动配置
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) 
public class Run {
    public static void main(String[] args) {
        SpringApplication.run(Run.class, args);
    }

}
```

#### 自定义 banner

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

#### 日志配置

SpringBoot 支持 Java Util Logging、Log4J、Log4J2、和 Logback最为日志框架，无论使用哪种框架，SpringBoot 已为当前使用日志框架的控制台输出及文件输出做好了配置。

* 配置级别
```
logging.level=com.xxx.yyy=DEBUG

* 配置文件
```
logging.file=/user/home/xxx/logs 
```

#### Profile 配置

Profile 是 Spring 用来针对不同的环境对不同的配置提供的支持，全局 Profile 配置使用 application-{profile}.properties

通过在 application.properties 中设置 spring.profiles.active=prod 来指定活动的 Profile。
