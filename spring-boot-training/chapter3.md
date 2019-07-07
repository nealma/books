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
![输出](img/scope-request-session.png)

#### 2.2 Spring EL 和 资源调用

   Spring EL 是 Spring 表达式语言，支持在 xml 和 注解中使用表达式。
   开发中经常调用各种资源的场景，包括普通文件、网址、配置文件、系统环境变量等。
* application.properties
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
![结果](img/el-use.png)
    
#### 2.3 Bean 的初始化和销毁
    实际开发中，我们会遇到需要在 Bean 的使用前、后做必要操作，Spring 对 Bean 的生命周期提供操作支持。
    
   * Java 配置方式 （@Bean(initMethod="init", destroyMethod="destroy")）
   * JSR-250 注解方式 （@PostConstruct、@PreDestroy）
```

/**
 * bean
 */
@Slf4j
public class BeanWayService {
    public BeanWayService() {
        super();
        log.info("BeanWayService construct");
    }
    public void init(){
        log.info("BeanWayService init");
    }
    public void destroy(){
        log.info("BeanWayService destroy");
    }
}

/**
 * jsr-250
 */
@Slf4j
public class JSR250WayService {
    public JSR250WayService() {
        super();
        log.info("JSR250WayService construct");
    }
    // 构造函数执行完之后执行
    @PostConstruct
    public void init(){
        log.info("JSR250WayService init");
    }

    // 在 Bean 销毁之前执行
    @PreDestroy
    public void destroy(){
        log.info("JSR250WayService destroy");
    }
}


/**
 * pre post config
 */
@Configuration
@ComponentScan("chapter2.propost")
public class PrePostConfig {
    @Bean(initMethod = "init", destroyMethod = "destroy")
    BeanWayService beanWayService(){
        return new BeanWayService();
    }
    @Bean
    JSR250WayService jsr250WayService(){
        return new JSR250WayService();
    }
}

/**
 * main
 */
@Slf4j
public class PrePostRun {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrePostConfig.class);

        BeanWayService beanWayService = context.getBean(BeanWayService.class);
        JSR250WayService jsr250WayService = context.getBean(JSR250WayService.class);

        context.close();
        // 输出
        // BeanWayService construct
        // BeanWayService init
        // JSR250WayService construct
        // JSR250WayService init
        // JSR250WayService destroy
        // BeanWayService destroy
    }
}
```
#### 2.4 环境配置 Profile
    Profile 为不同环境提供不同的配置
   * 通过设置 Environment 的 ActiveProfiles 
   * 通过设置 jvm 的 spring.profiles.active 参数（两种：配置文件中；命令行参数）
```
/**
 * Profile
 */
@Data
@AllArgsConstructor
public class ProfileService {
    private String name;
}

/**
 * config
 */
@Configuration
public class ProfileConfig {

    @Bean
    @Profile("test")
    public ProfileService test(){
        return new ProfileService("test");
    }

    @Bean
    @Profile("prod")
    public ProfileService prod(){
        return new ProfileService("prod");
    }
}

/**
 * main
 */
@Slf4j
public class Run {

    public static void main(String[] args) {
    
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 激活 test Profile
        context.getEnvironment().setActiveProfiles("test");
        // 注册 Bean 配置类
        context.register(ProfileConfig.class);
        // 刷新容器
        context.refresh();

        ProfileService profileService = context.getBean(ProfileService.class);

        log.info("name: {}", profileService.getName());

        context.close();
        
        // 输出
        // name: test
    }
}
```

#### 2.5 事件（Application Event）

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
    