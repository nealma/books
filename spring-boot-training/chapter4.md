### 4 Spring MVC 基础

#### 4.1 MVC
    servlet2.5 + web.xml
    servlet3.0 + 零配置（无 web.xml）

#### 4.2 Spring MVC 配置
```
@Configuration
@EnableWebMvc // 开启相关默认配置，例如：ViewResolver、MessageConverter.
@ComponentScan("chapter4")
public class MvcConfig {
    
    @Bean
    public InternalResourceViewResolver viewResolver(){
        // 根接口 ViewResolver, 重写 resolveViewName(),返回值是 View ，View 使用 model、request、response 对象，将视图（HTML、json、XML、PDF等）返回给浏览器
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/classes/views");
        viewResolver.setSuffix(".jsp");
        viewResolver.setViewClass(JstlView.class);
        return viewResolver;
    }
    
}

public class WebInitializer implements WebApplicationInitializer {// WebApplicationInitializer 是 Spring 提供的用来配置 servlet3.0 + 配置的接口
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();// Web Context 注册配置类
        context.register(MvcConfig.class);
        context.setServletContext(servletContext);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("servlet", new DispatcherServlet(context));
        servlet.addMapping("/");
        servlet.setLoadOnStartup(1);
    }
}

```
    @Controller 
    @RestController 是 @Controller 和 @ResponseBody 组合注解
    
    根据 @RequestMapping 中 produces（application/xml;charset=UTF-8\application/json;charset=UTF-8等） 的媒体类型 返回相应的数据格式