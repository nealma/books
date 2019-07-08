### 3 Spring 高级话题

#### 3.1 Spring Aware

##### 3.1.1 说明
    实际项目中，不可避免要用到 Spring 容器本身的功能资源，这时你的 Bean 必须要意识到 Spring 容器的存在，才能调用 Spring 所提供的资源，
    这就是 Spring Aware。其实 Spring Aware 本来就是 Spring 设计用来框架内部使用的。
    若使用了 Spring Aware，你的 Bean 将会和 Spring 框架耦合。
    Spring 提供的 Spring Aware 接口
   * BeanNameAware 获得到容器中 Bean 的名称 
   * BeanFactoryAware 获得当前 bean factory，这样可以调用容器的服务
   * ApplicationContextAware* 当前的 application context，这样可以调用容器的服务
   * MessageSourceAware 获得 message source，这样可以获得文本信息
   * ApplicationEventPublisherAware 应用事件发布器，可以发布事件
   * ResourceLoaderAware 获得资源加载器，可以获得外部资源文件 
    Spring Aware 目的是为了让 Bean 获得 Spring 容器的服务。因为 ApplicationContext 接口集成了 MessageSource、ApplicationEventPublisher 和 ResourceLoader，
    所以 Bean 继承 ApplicationContextAware 可以获得 Spring 容器的所有服务。
   
##### 3.1.2 示例
   **BeanNameAware、ResourceLoaderAware**
```
/**
 * aware
 */
@Slf4j
@Service
public class AwareService implements BeanNameAware, ResourceLoaderAware {// 获得 Bean 名称和资源加载的服务

    private String beanName;
    private ResourceLoader resourceLoader;
    /**
     * 继承 BeanNameAware 接口，需重写 setBeanName() 方法
     */
    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * 继承 ResourceLoaderAware 接口，需重写 setResourceLoader() 方法
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void print() throws IOException {
        log.info("beanName: {}", beanName);
        Resource resource = resourceLoader.getResource("classpath:test.txt");
        log.info("resource: {}", new String(FileCopyUtils.copyToByteArray(resource.getInputStream())));
    }
}

/**
 * 配置类
 */
@Configuration
@ComponentScan("chapter3.aware")
public class AwareConfig {

}
/**
 * 运行类
 */
public class Run {
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AwareConfig.class);
        AwareService awareService = context.getBean(AwareService.class);
        awareService.print();
        context.close();
        // beanName: awareService
        // resource: yaya
    }
}
```
#### 3.2 多线程

   Spring 通过任务执行器（TaskExecutor）来实现多线程和并发编程。
   使用 ThreadPoolTaskExecutor 可实现一个基于线程池的 TaskExecutor。而实际开发中任务一般是非阻碍的，
   即异步的，所以我们要在配置类中通过 @EnableAsync 开启对异步任务的支持，并通过在实际执行的 Bean 的方法中
   使用 @Async 注解来声明其是一个异步任务。
   
```

/**
 * task
 */
@Slf4j
@Service
public class TaskService {

    /**
     * @Async 注解表明该方法是个异步方法
     *        如果注解在类级别，则表明该类的所有方法都是异步方法
     * 注意开启 @EnableAsync
     * @Async 调用中的事务处理机制
     * 在 @Async 标注的方法，同时也适用了@Transactional进行了标注；在其调用数据库操作之时，将无法产生事务管理的控制，原因就在于其是基于异步处理的操作。
     * 那该如何给这些操作添加事务管理呢？可以将需要事务管理操作的方法放置到异步方法内部，在内部被调用的方法上添加@Transactional.
     * 例如：  方法A，使用了@Async/@Transactional来标注，但是无法产生事务控制的目的。
     *        方法B，使用了@Async来标注，B中调用了C、D，C/D分别使用@Transactional做了标注，则可实现事务控制的目的。
     */
    @Async
    public void one() throws InterruptedException {
        Thread.sleep(1000);
        log.info("-> one");
    }
    @Async
    public void two() throws InterruptedException {
        Thread.sleep(3000);
        log.info("-> two");
    }
    @Async
    public void three() throws InterruptedException {
        Thread.sleep(2000);
        log.info("-> three");
    }
}

/**
 * 配置类
 */
@Configuration
@ComponentScan("chapter3.task")
@EnableAsync
public class TaskConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setQueueCapacity(25);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}

/**
 * 运行类
 */
public class Run {
    public static void main(String[] args) throws IOException, InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TaskConfig.class);
        TaskService taskService = context.getBean(TaskService.class);
        for (int i=0; i<3; i++){
            taskService.one();
            taskService.two();
            taskService.three();
        }
        context.close();
        // 结果是不是按照顺序执行，也就证明任务是异步执行。
        // -> one
        // -> one
        // -> three
        // -> one
        // -> two
        // -> three
        // -> two
        // -> three
        // -> two
    }
}
```
#### 3.3 Bean 计划任务
    从 Spring 3.1 开始，计划任务在 Spring 中的实现变得异常的简单。首先通过在配置类注解 @EnableScheduling 来开启对计划任务的支持。
    然后在要执行计划任务的方法上注解 @Scheduled ，声明这是一个计划任务。
    
   Spring 通过 @Scheduled 可以支持多种类的计划任务，包含cron、fixDelay、fixRate 等。
```
/**
 * 定时任务
 */
@Service
@Slf4j
public class ScheduledTaskService {
    /**
     * @Scheduled 声明该方法是计划任务，使用 fixedRate 属性每隔固定时间执行。
     */
    @Scheduled(fixedRate = 500)
    public void printRateDateTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        log.info("rate[500]: {}", LocalDateTime.now().format(formatter));
    }
    /**
     * @Scheduled 声明该方法是计划任务，使用 fixedDelay 属性延迟固定时间执行。
     */
    @Scheduled(fixedDelay = 1000)
    public void printDelayDateTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        log.info("delay[1000]: {}", LocalDateTime.now().format(formatter));
    }
    /**
     * @Scheduled 声明该方法是计划任务，使用 cron 属性可按照指定时间执行。
     * cron 是UNIX 或 类 UNIX（Linux）系统下的定时任务
     * 例子：每天 23：25 执行, 如果时间是过去时，不再执行。
     */
    @Scheduled(cron = "0 33 23 * * ?")
    public void printCronDateTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        log.info("cron[0 33 23 ？* *]: {}", LocalDateTime.now().format(formatter));
    }
}

/**
 * 配置类
 */
@Configuration
@ComponentScan("chapter3.Scheduled")
@EnableScheduling
public class ScheduledTaskConfig{

}

/**
 * 运行类
 */
public class Run {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(ScheduledTaskConfig.class);
        // 输出
        // delay[1000]: 2019-07-07 23:32:57.878
        // delay[1000]: 2019-07-07 23:32:58.884
        // rate[500]: 2019-07-07 23:32:59.325
        // rate[500]: 2019-07-07 23:32:59.827
        // cron[0 33 23 ？* *]: 2019-07-07 23:33:00.003
        // ......
    }
}
```
#### 3.4 条件注解 @Conditional
    前面我们讲过，可通过 @Profile 注解获取不同的 Bean。Spring 4.x 提供了统一的 基于条件的 Bean 的创建。即 @Conditional
```

/**
 * 判定 Linux 条件
 */
@Slf4j
public class LinuxCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        log.info("matches Linux");
        return context.getEnvironment().getProperty("os.name").contains("Linux");
    }
}

/**
 * 判定 Mac 条件
 */
@Slf4j
public class MacCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        log.info("matches Mac OS X");
        return context.getEnvironment().getProperty("os.name").contains("Mac OS X");
    }
}

/**
 * 通用接口
 */
public interface ListService {
    void os();
}

/**
 * service
 */
@Slf4j
public class LinuxOSService implements ListService{
    @Override
    public void os(){
       log.info("Linux");
    }
}

/**
 * service
 */
@Slf4j
public class MacOSService implements ListService{
    @Override
    public void os(){
       log.info("Mac OS X");
    }
}
/**
 * 配置类
 */
@Configuration
public class ConditionConfig {
    /**
     * 通过 @Conditional 注解，符合 windows 条件，则实例化 WindowListService。
     */
    @Bean
    @Conditional(MacCondition.class)
    public ListService mac(){
        return new MacOSService();
    }
    /**
     * 通过 @Conditional 注解，符合 linux 条件，则实例化 LinuxListService。
     */
    @Bean
    @Conditional(LinuxCondition.class)
    public ListService linux(){
        return new LinuxOSService();
    }
}

/**
 * 运行类
 */
public class Run {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConditionConfig.class);
        ListService listService = context.getBean(ListService.class);
        listService.os();
        System.out.println(context.getEnvironment().getProperty("os.name"));
        context.close();
        // Mac OS X
        // Mac OS X
    }
}
```
#### 3.5 组合注解与元注解
##### 3.5.1    
    顺便介绍下注解知识
   * @Retention 保留期，解释这个注解的存活时间
            取值：
            1. RetentionPolicy.SOURCE 只在源码阶段保留，在编译器编译时会被丢弃忽视。
            2. RetentionPolicy.CLASS 直到编译字节码的阶段一直保留，不会被加载到 VM。
            3. RetentionPolicy.RUNTIME 一直保留包运行时阶段，被加载到 VM，程序中可以读取。
   * @Documented 跟文档相关，可以将注解的元素包含到 javadoc 中。
   * @Target 标注这个元素可以在哪个地方使用
            1. ElementType.TYPE 可以注解在 类、接口、注解、枚举
            2. ElementType.FIELD 可以注解在 属性
            3. ElementType.METHOD 可以注解在 方法
            4. ElementType.PARAMETER 可以注解在 方法参数
            5. ElementType.CONSTRUCTOR 可以注解在 构造函数
            6. ElementType.ANNOTATION_TYPE 可以注解一个注解
            7. ElementType.PACKAGE 可以注解在 包
            8. ElementType.LOCAL_VARIABLE 可以注解在 局部变量
            9. ElementType.TYPE_PARAMETER 同 PARAMETER
            10. ElementType.TYPE_USE 同 TYPE
   * @Inherited 继承，父类使用 @Inherited 注解，则子类会继承父类的所有注解，即使子类什么注解都没有。
   * @Repeatable 可重复的
      @interface Role {
 	        User[]  value();
      }
      @Repeatable(Role.class)
      @interface User{
 	        String role default "";
      }
      @User(role="ARTIST")
      @User(role="CODER")
      @User(role="PM")
      public class DaNiu{
      }
   *  注解的属性
```

``` 
##### 3.5.2

    Spring 2.x 开始，为了响应 JDK 1.5 推出的注解功能，Spring 开始加入大量的注解来替代 xml 配置。Spring 的注解主要用来配置和注入 Bean。
    以及 AOP 相关配置（@Transitional）。注解类越来越多，使用繁琐，可以用元注解来组合新注解，消除样板代码（boilerplate code）。
    
    元注解可以理解为可以注解到其他注解的注解，感受一下元数据的定义，也可以通俗点叫组合注解，比较常用的 @Configuration 就是一个
    组合 @Component 注解，表明其就是一个 Bean。
    
    作为程序猿，你会发现，我们总是需要 @Configuration 和 @ComponentScan 两个注解组合使用，同样的代码不要重复出现（DRY: Don't repeat yourself.）
    怎么办？
    可以自定义组合注解来避免这样的尴尬。
```

```      
    