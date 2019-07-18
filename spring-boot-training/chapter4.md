### 4 Spring MVC 基础

#### 4.1 MVC
    servlet2.5 + web.xml
    servlet3.0 + 零配置（无 web.xml）

#### 4.2 Spring MVC 配置
```
@Configuration
@EnableWebMvc // 开启相关默认配置，例如：ViewResolver、MessageConverter.
@ComponentScan("chapter4")
public class MvcConfig implements WebMvcConfigurer {// 注意原来 WebMvcConfigurerAdpater 适配器已经废弃

    @Bean
    public InternalResourceViewResolver viewResolver(){
        // 根接口 ViewResolver, 重写 resolveViewName(),返回值是 View ，View 使用 model、request、response 对象，将视图（HTML、json、XML、PDF等）返回给浏览器
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/classes/views");
        viewResolver.setSuffix(".jsp");
        viewResolver.setViewClass(JstlView.class);
        return viewResolver;
    }

    @Bean
    public CustomInterceptor customInterceptor(){// 注入自定义拦截器

        return new CustomInterceptor();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {// 静态资源映射
        registry
                .addResourceHandler("/asserts/**")// 文件放置目录
                .addResourceLocations("classpath:/asserts/**");// 对外暴露的访问路径
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {// 拦截器 普通 Bean 通过实现 HandlerInterceptor 接口或继承 HandlerInterceptorAdaptor 类实现自定义拦截器

        registry.addInterceptor(customInterceptor());
    }
}

/**
 * 自定义 Bean
 */
@Data
@AllArgsConstructor
public class CustomBean {
    private String name;
}

/**
 * 自定义 拦截器
 */
@Slf4j
public class CustomInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle");
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion");
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("afterConcurrentHandlingStarted");
        super.afterConcurrentHandlingStarted(request, response, handler);
    }
}

/**
 * Web 容器初始化
 */
@Component
public class WebInitializer implements WebApplicationInitializer {// WebApplicationInitializer 是 Spring 提供的用来配置 servlet3.0 + 配置的接口
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();// Web Context 注册配置类
        context.register(MvcConfig.class);
        context.setServletContext(servletContext);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("servlet", new DispatcherServlet(context));
        servlet.addMapping("/");
        // start up
        // >= 0 , 启动容器后，加载servlet
        // < 0 , 系统调用 servlet 时，加载servlet
        // 正数的值越小，启动该servlet的优先级越高
        servlet.setLoadOnStartup(1);
    }
}

```
    @Controller 
    @RestController 是 @Controller 和 @ResponseBody 组合注解
    
    根据 @RequestMapping 中 produces（application/xml;charset=UTF-8\application/json;charset=UTF-8等） 的媒体类型 返回相应的数据格式
```
/**
 * 测试 controller
 */
@RestController
public class CustomRestController {
    @GetMapping(value = "/get", produces = "application/xml;charset=UTF-8")// 需引入依赖 jackson-dataformat-xml
    public CustomBean get(){
        return new CustomBean("web");
    }
}
```    
#### 4.3 @ControllerAdvice 控制器的全局配置

    * @ControllerAdvice 控制器的全局配置
    * @ExceptionHandler 控制器的全局异常处理, value 属性是过滤拦截的条件
    * @ModelAttribute 全局对参数进行处理
    * @InitBinder 可以实现类型转换、参数绑定和过滤
```

/**
 * 自定义 Bean
 */
@Data
@AllArgsConstructor
public class CustomBean {
    private String name;
    private String id;
    private String gender;
}

@ControllerAdvice // 控制器建言，实际是一个 @Component 组合注解
@Slf4j
public class GlobalExceptionHandler {

    // 全局异常处理
    @ExceptionHandler(Exception.class)
    public ModelAndView exception(Exception exception, WebRequest request){
        ModelAndView modelAndView = new ModelAndView("error");// error 页面
        modelAndView.addObject("errorMsg", exception.getMessage());
        return modelAndView;
    }

    @ModelAttribute // 将键值对添加到全局，所有 @RequestMapping 的方法可以获取到该键值对, 每次请求都会经过该方法
    public void addAttributes(Model model){
        model.addAttribute("msg", "other message");
        log.info("global model: {}", model);
        // 输出
        // global model: {msg=other message}
    }

    // 每一次 request
    @InitBinder("a") // 定制 WebDataBinder,
    public void initBinder(WebDataBinder webDataBinder){
        log.info("Global initBinder: {}", webDataBinder.getAllowedFields());
        webDataBinder.setFieldDefaultPrefix("a.");
        webDataBinder.setBindEmptyMultipartFiles(true);
        webDataBinder.setFieldMarkerPrefix("");
        webDataBinder.setDisallowedFields("id"); // 忽略 request 请求中 id 参数，貌似不起作用，待后期研究

        // 时间格式转换
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CustomDateEditor dateEditor = new CustomDateEditor(df, true);
        webDataBinder.registerCustomEditor(Date.class,dateEditor);
    }
}


/**
 * 测试 controller
 */
@RestController
@Slf4j
public class CustomRestController {
    // 只针对当前 Controller
    @InitBinder("a")
    public void initBinder(WebDataBinder webDataBinder){
        // 让name属性无法被接收
        log.info("CustomRestController initBinder: {}", webDataBinder.getAllowedFields());
        webDataBinder.setDisallowedFields("name");
    }
    @GetMapping(value = "/error")
    public String error(){
        return "failed";
    }
    @GetMapping(value = "/get", produces = "application/xml;charset=UTF-8")// 需引入依赖 jackson-dataformat-xml
    public CustomBean get(){
        return new CustomBean("web", "id", "gender");
    }
    @GetMapping(value = "/advice")
    public CustomBean advice(CustomBean customBean){
        int i = 0;
        int j = i/i;
        return customBean;
    }
    @GetMapping(value = "/disallowId", produces = "application/json;charset=UTF-8")
    public CustomBean disallowId(@ModelAttribute("a") CustomBean customBean){
        return customBean;
    }

}
```    
#### 4.4 Web 路由处理

    * ViewController 
    
    * 配置中重写

#### 4.4 路径匹配参数配置

    在 Spring MVC 中，路径参数如果带"."的话，"."后面的参数值将被忽略。
    例如，你访问 http://localhost:8080/mvc/a.txt, ".txt"将被忽略。
    通过重写 configurePathMatch 方法可不忽略 "." 后面的参数 
```
/**
 * 解决普通文件自动匹配问题（微信验证的时候用 MP_verify_7cUlmB08NpoIPlNw.txt）
 * favorPathExtension 表示支持后缀匹配, false 取消后缀匹配
 */

@Override
public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer.setUseSuffixPatternMatch(false);
}


@Override
public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.favorPathExtension(false);
}
``` 
   
#### 4.5 文件上传

    在 Spring MVC 中，通过配置 MultipartResolver 来上传文件。
    在控制器中，通过 MultipartFile file 来接收一个文件，通过 MultipartFile[] files 来接收多个文件，
```
FileUtils.writeByteArrayToFile() 存储到磁盘
```    

#### 4.6  自定义 HttpMessageConverter 

    HttpMessageConverter 是用来处理 request 和 response 里的数据的。 Spring 为我们内置了大量的 HttpMessageConverter。
    例如：MappingJackson2HttpMessageConverter、StringHttpMessageConverter 等。
```

/**
 * 自定义消息转换器
 */
public class MyMessageConverter extends AbstractHttpMessageConverter<CustomBean> {// 继承 AbstractHttpMessageConverter，实现自定义

    public MyMessageConverter() {
        // 新建一个自定义媒体类型 application/x-nealma-1
        super(new MediaType("application", "x-nealma", Charset.forName("UTF-8")));
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return CustomBean.class.isAssignableFrom(aClass); // 表面只处理 CustomBean 这个类
    }

    /**
     * 重写 readInternal() 方法，处理请求的数据
     */
    @Override
    protected CustomBean readInternal(Class<? extends CustomBean> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        String temp = StreamUtils.copyToString(httpInputMessage.getBody(), Charset.forName("UTF-8"));
        String[] split = temp.split("-");
        return new CustomBean(split[0], split[1], split[2]);
    }

    /**
     * 重写 writeInternal() 方法，处理如何输出数据到 response 中
     */
    @Override
    protected void writeInternal(CustomBean customBean, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        httpOutputMessage.getBody().write((customBean.getId() + customBean.getName() + customBean.getGender()).getBytes());
    }
}

/**
 * 测试 controller
 */
@RestController
@Slf4j
public class ConvertRestController {
    @PostMapping(value = "/convert", produces = "application/x-nealma;charset=UTF-8")
    public @ResponseBody CustomBean convert(@RequestBody CustomBean customBean){
        return customBean;
    }
}

```

```
# 请求
curl -XPOST -i 'http://localhost:8080/convert' -d'1-2-3' -H 'Content-Type: application/x-nealma;charset=UTF-8'
```

