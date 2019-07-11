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
    

