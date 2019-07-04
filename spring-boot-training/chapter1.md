### 1 Spring 基础

#### 1.1 Spring 概述

##### 1.1.1 Spring 的简史
    在我的印象中，大学时期就已经有了 Spring ，整天为配置xml而揪头发，想想这些年，真实变化快啊
    
    第一阶段： xml 配置
    在 Spring 1.x 时代 ，使用 Spring 开发，满眼都是 xml 文件配置的 Bean，随着项目的扩大， 我们需要把 xml 配置文件
    分放到不同的配置文件里，那时候需要频繁的在开发的类和配置文件之间切换。
    
    第二阶段： 注解配置
    在 Spring 2.x 时代 ，随着 JDK 1.5 带来的注解支持，Spring 提供了声明 Bean 的注解（如：@Component、@Service），减少了配置量。
    
    第三阶段： Java 配置
    从 Spring 3.x 到现在，Spring 提供了 Java 的配置能力，使用 Java 配置让你更容易理解你配置的 Bean，我们目前放好处在这个时代，
    Spring 4.x 和 Spring Boot 都推荐使用 Java 配置。
##### 1.1.2 Spring 概述
    Spring 框架是一个轻量级的企业级开发的一站式解决方案。所谓解决方案，就是可以基于 Spring 解决 Java EE 的所有问题。
    Spring 框架主要提供了 IoC 容器、AOP、数据访问、Web 开发、消息、测试等相关技术的支持。
    
    Spring 使用简单的 POJO （Plain Old Java Object，即无任何限制的普通 Java 对象）来进行企业级开发。每一个被 Spring 管理的
    Java 对象都被称为 Bean，而 Spring 提供了一个 IoC 容器用来初始化对象，解决对象间的依赖管理和对象的使用。
    
   * Spring 模块
    Spring 是模块化的，你可以只用你需要的 Spring 模块。 
    
    1）核心容器（Core Container）
    Spring-Core：核心工具类，Spring 其他模块大量使用 Spring-Core；
    Spring-Beans：Spring 定义 Bean 的支持；
    Spring-Context：运行时 Spring 容器；
    Spring-Context-Support：Spring 对第三包的支持；
    Spring-Expression：使用表达式语言在运行时查询和操作对象。
    
    2）AOP
    Spring-AOP：基于代理的 AOP 支持；
    Spring-Aspects：基于 Aspects 的 AOP 支持；
    Spring-Context：运行时 Spring 容器；
    Spring-Context-Support：Spring 对第三包的支持；
    Spring-Expression：使用表达式语言在运行时查询和操作对象。
    
    3）消息（Message）
    Spring-Message：对消息架构和协议的支持；
    
    4）Web
    Spring-Web：提供基础的 Web 集成功能，在项目中提供 Spring 的容器；
    
    5）数据访问/集成（DataAccess/Integration）
    Spring-JDBC：提供以 JDBC 访问数据库的支持；
    Spring-TX：提供编程式和声明式的事物支持；
    Spring-ORM：提供 对象/关系映射 技术的支持；
    Spring-OXM：提供 对象/XML映射 技术的支持；
    Spring-JMS：提供对 JMS 的支持；

   * Spring 的生态
    Spring 发展到现在已经不仅仅是 Spring 框架本身的内容， Spring 目前提供了大量的基于 Spring 的项目，
    可以用来更深入地降低我们的开发难度，提供开发效率。
    
    目前 Spring 的生态里有一下项目：
    1）Spring Boot；使用默认开发配置来进行快速开发；
    2）Spring XD：用来简化大数据开发；
    3）Spring Cloud：为分布式系统开发提供工具集；
    4）Spring Data：对主流的关系型、NoSQL 数据库的支持；
    5）Spring Integration：通过消息机制对企业集成模式（EIP）的支持；
    6）Spring Batch：简化和优化大量数据的批处理操作；
    7）Spring Security：通过认证和授权保护应用； 
    8）Spring HATEOAS：基于 HATEOAS 原则简化 REST 服务开发； 
    9）Spring Social：与社交网络API的集成 
    10）Spring Mobile：提供对手机设备检测的功能
    11）Spring For Android：主要提供在 Android 消费 RESTFul API 的功能
    12）Spring Web Flow：基于 Spring MVC 提供基于向导流程式的 Web 应用开发
    13）Spring Web Services：基于协议有限的 SOAP/Web 服务
    14）Spring LDAP：简化使用 LDAP 开发
    15）Spring Session：提供一个 API及实现来管理用户会话信息

#### 1.2 Spring 项目快速搭建
    早期，都是把依赖的 jar 包放到类路径下，以来混乱。
    为了解决依赖问题，需要项目构建工具。
    目前主流的项目构建工具：Ant、Maven、Gradle等。
##### 1.2.1 Maven 简介   
##### 1.2.2 Maven 安装 
* 安装 
* 测试安装
```
 mvn -v
```    
##### 1.2.3 Maven 的 pom.xml
  Maven 是基于项目对象模型的概念运作的，所以 Maven 都有一个 pom.xml。
  * dependencies
  * dependency 元素 groupId、artifactId、version
  * 变量定义 properties
  * 编译插件 build -> plugins -> plugin
  * Maven 运作方式
    Maven 会自动根据 dependency 中的依赖配置，直接通过互联网在 Maven 中心库下载相关依赖包到 .m2 目录下，
    .m2 是你的本地 Maven 仓库。
    如果你不知道你要依赖的 jar 包怎么引用，推荐到 mavenrepository.com 网站搜索。
    如果中心仓库没有你需要的 jar 包，需要 把 jar 包安装到本地仓库
```
mvn install:install-file -Dfile=jar包的位置 -DgroupId=上面的groupId -DartifactId=上面的artifactId -Dversion=上面的version -Dpackaging=jar
```    
##### 1.2.4 Spring 项目构建
   * Spring Tool Suite （STS）  
   * eclipse、idea、NetBeans 插件
   
#### 1.3 Spring 基础配置
    Spring 本身的四大原则：
   * 使用 POJO 进行轻量级和最小侵入式开发 
   * 通过依赖注入和基于接口编程实现松耦合
   * 通过 AOP 和默认习惯进行声明式编程
   * 使用 AOP 和模板（Template）较少模式化代码 
##### 1.3.1 依赖注入 （依赖spring-boot-starter-web）
    我们常说的控制翻转（Invention Of Control - IoC）和 依赖注入（Dependency injection - DI）在 Spring 环境下其实是等价的。
    空中翻转是通过依赖注入实现的。依赖注入其实就是容器负责创建对象和维护对象间的依赖关系。
    依赖注入目的是为了解耦，提现一种"组合"的概念。
    无论 xml 配置、注解配置，还是 Java 配置，都叫配置元数据，元数据就是描述数据的数据。
    Spring 容器解析这些配置元数据进行 Bean 初始化、配置和管理依赖。
    
    声明 Bean 的注解：
   * @Component 么有明确的角色
   * @Service 业务逻辑层
   * @Repository 数据访问层
   * @Controller 表现层
   
    注入 Bean 的注解： 下面三个可以放在 set 方法 或者属性上，我习惯放在属性上
   * @Autowired Spring 提供的注解
   * @Inject JSR-330 提供的注解
   * @Resource JSR-250 提供的注解
   
   JSR 即 Java Specification Requests，Java 规范说明
   
   * @Configuration 声明当前类是一个配置类
   * @ComponentScan 自动扫描包名下所有使用 @Service @Component @Repository @Controller 的类并注册为 Bean。
##### 1.3.2 Java 配置
    Spring 4.x 推荐使用 Java 配置，Spring Boot 样推荐使用 Java 配置。
    Java 配置通过 @Configuration 和 @Bean 来实现。
   * @Configuration 声明当前类是一个配置类，相当于 Spring 的一个 xml 文件
   * @Bean 注解在方法上，声明当前方法返回值为一个 Bean。
   * 运行
```
/**
 * Java 配置类
 */
@Configuration // 标注当前是一个配置类
public class JavaConfig {
    /**
     * 普通 Bean 注入
     */
    @Bean
    public UserService userService(){
        return new UserService();
    }

    /**
     * 直接将 userService 作为参数传递给 payService() 方法
     * 在 Spring 容器中，只要容器中存在某个 Bean，就可以在另一个 Bean 声明的方法的参数中注入
     */
    @Bean
    public PayService payService(UserService userService){
        return new PayService(userService);
    }
}

/**
 * user
 */
public class UserService {
    public String sayHello(){
        return "user";
    }
}

/**
 * pay
 */
public class PayService {
    private UserService userService;
    public PayService(UserService userService) {
        this.userService = userService;
    }
    public String pay(){
        return userService.sayHello() + " pay";
    }
}

/**
 * main
 */
public class RunApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);
        UserService userService = context.getBean(UserService.class);
        System.out.println(userService.sayHello());

        PayService payService = context.getBean(PayService.class);
        System.out.println(payService.pay());

        // 输出
        // user
        // user pay
    }
}
```
##### 1.3.3 AOP (依赖spring-boot-starter-aop)
    AOP 面向切面编程，相对于 OOP 面向对象编程。
    Spring AOP 的存在目的是：解耦。AOP可以让一组类共享相同的行为。在 OOP 中只能通过继承类和实现接口，
    使得代码的耦合度增强，且类继承只能是单继承，阻碍更多行为应用到一组类上，AOP 弥补 OOP 的不足。
    
   Spring 支持 AspectJ 的注解式切面编程。
   * @Aspect 声明一个切面
   * @After @Before @Around 定义建言（Advice），可直接拦截规则（切点）作为参数
   * @Pointcut 声明一个切点，定义拦截规则
   * 其中符合条件的每一个被拦截处为连接点（JoinPoint）
   
   拦截方式
   
   * 基于注解
   * 基于方法规则
   
```  

/**
 * 拦截规则的注解
 */
@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface Action {
    String name();
}

/**
 * 使用注解的被拦截类
 */
@Service
public class AnnotationService {
    @Action(name = "注解式拦截的 play 操作")
    public void play(){}
}

/**
 * 使用方法规则的被拦截类
 */
@Service
public class MethodRuleService {
    public void play(){}
}

**
 * AOP 配置类
 */
@Configuration // 标注当前是一个配置类
@EnableAspectJAutoProxy // 开启 Spring 对 AspectJ 的支持
@ComponentScan("chapter1.aop")
public class AopConfig {

}

/**
 * 切面类
 */
@Aspect // 注解声明一个切面
@Component // 注解成为 Spring 容器管理的 Bean
@Slf4j
public class LogAspect {

    /**
     * 注解声明切点
     */
    @Pointcut("@annotation(chapter1.aop.Action)")
    public void annotationPointCut(){}

    /**
     * 注解声明一个建言，并使用 @PointCut 定义的切点
     */
    @After("annotationPointCut()")
    public void after(JoinPoint joinPoint){
        // 通过反射可获取注解上的属性，执行一些业务操作
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Action action = method.getAnnotation(Action.class);
        log.info("Action name: {}", action.name());
    }

    /**
     * 注解声明一个建言，直接使用拦截规则作为参数
     */
    @Before("execution(* chapter1.aop.MethodRuleService.*(..))") // 所有 chapter1.aop.MethodRuleService 包下的所有方法
    public void before(JoinPoint joinPoint){
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        log.info("方法名: {}", method.getName());
    }
}

/**
 * AOP 运行类
 */
public class RunApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopConfig.class);

        // 2 AOP
        AnnotationService annotationService = context.getBean(AnnotationService.class);
        annotationService.play();

        MethodRuleService methodRuleService = context.getBean(MethodRuleService.class);
        methodRuleService.play();

        // 输出
        // Action name: 注解式拦截的 play 操作
        // 方法名: play
    }
}
```
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
