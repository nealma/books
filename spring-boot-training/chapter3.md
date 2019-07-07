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
neal.ma=neal.ma
```

test.properties 
```
name=neal.ma
```
* 代码
```
@Service
@Data
public class ElService {
    private String name = "tina";
}
/**
 * 配置类
 */
@Slf4j
@Configuration
@ComponentScan("chapter2.el")
@PropertySource("application.properties")
public class ElConfig {
    // 注入普通字符串
    @Value("normal")
    private String normal;

    // 注入操作系统属性
    @Value("#{systemProperties['os.name']}")
    private String osName;

    // 注入 表达式结果
    @Value("#{T(java.lang.Math).random()}")
    private double random;

    // 注入其他 Bean 属性
    @Value("#{elService.name}")
    private String elServiceName;

    // 注入文件资源
    @Value("test.properties")
    private Resource resourceFile;

    // 注入网址资源
    @Value("http://www.aliyun.com")
    private Resource resourceUrl;

    // 注入配置文件
    @Value("${neal.ma}")
    private String nealmaName;

    @Autowired
    private Environment environment;

    public void print() throws IOException {
        log.info("normal: {}", normal);
        log.info("osName: {}", osName);
        log.info("random: {}", random);
        log.info("elServiceName: {}", elServiceName);
        log.info("resourceFile: {}", FileCopyUtils.copyToByteArray(resourceFile.getInputStream()));
        log.info("resourceUrl: {}", FileCopyUtils.copyToByteArray(resourceUrl.getInputStream()));
        log.info("nealmaName: {}", nealmaName);
        log.info("neal.ma: {}", environment.getProperty("neal.ma"));
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

#### 3.5 事件（Application Event）

    Spring 事件提供 Bean 与 Bean 之间消息通信。比如一个 Bean 处理完一个任务之后，希望另一个 Bean 感知到并做相应的处理，
    这个时候就需要 这个 Bean 监听另一个 Bean 发送的事件，
   * 自定义事件，继承 ApplicationEvent
   * 定义事件监听器，实现 ApplicationListener 
   * 使用容器发布事件
   
```
/**
 * event
 */
public class MyEvent extends ApplicationEvent {
    private String msg;
    public MyEvent(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

/**
 * listener
 */
@Slf4j
@Component
public class MyEventListener implements ApplicationListener<MyEvent> { // 指定事件类

    // 对消息进行接收处理
    @Override
    public void onApplicationEvent(MyEvent event) {
        log.info("receive: {}", event.getMsg());
    }
}

/**
 * 事件发布类
 */
@Component
public class MyPublisher {

    @Autowired
    private ApplicationContext applicationContext;

    public void publish(MyEvent event){
        applicationContext.publishEvent(event);
    }
}

/**
 * config
 */
@Configuration
@ComponentScan("chapter2.event")
public class EventConfig {

}

/**
 * main
 */
@Slf4j
public class Run {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EventConfig.class);

        MyPublisher publisher = context.getBean(MyPublisher.class);

        MyEvent event = new MyEvent("tina");
        publisher.publish(event);

        context.close();
        // 输出
        // receive: tina
    }
}
```   
    