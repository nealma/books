package chapter4;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc // 开启相关默认配置，例如：ViewResolver、MessageConverter.
@ComponentScan("chapter4")
public class MvcConfig extends WebMvcConfigurerAdapter {

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
