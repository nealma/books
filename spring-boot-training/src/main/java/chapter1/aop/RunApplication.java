package chapter1.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * AOP
 *
 * @author neal.ma
 * @date 2019/7/4
 * @blog nealma.com
 */
public class RunApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopConfig.class);

        // 2 AOP
        AnnotationService annotationService = context.getBean(AnnotationService.class);
        annotationService.play();

        MethodRuleService methodRuleService = context.getBean(MethodRuleService.class);
        methodRuleService.play();

        // 输出
        // Action name: 注解式拦截的 play 操作
        // 方法名: play
    }
}
