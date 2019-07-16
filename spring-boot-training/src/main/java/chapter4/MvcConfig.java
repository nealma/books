package chapter4;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.util.List;

@Configuration
@EnableWebMvc // 开启相关默认配置，例如：ViewResolver、MessageConverter.
@ComponentScan("chapter4")
public class MvcConfig implements WebMvcConfigurer { // WebMvcConfigurerAdpater 注意官方已经废弃

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

    @Bean
    public MyMessageConverter converter() {
        return  new MyMessageConverter();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 重载会覆盖 Spring MVC 默认的多个 HttpMessageConverter
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 只是添加一个自定义的 HttpMessageConverter ， 比覆盖默认注册的 HttpMessageConverter。
        converters.add(converter());
    }
}
