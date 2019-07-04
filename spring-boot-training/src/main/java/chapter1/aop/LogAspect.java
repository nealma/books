package chapter1.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 切面类
 *
 * @author neal.ma
 * @date 2019/7/4
 * @blog nealma.com
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
