package chapter4;

import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

@Component
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
