import chapter1.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * main
 *
 * @author neal.ma
 * @date 2019/7/4
 * @blog nealma.com
 */
public class RunApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);

        // 1 DI
        UserService userService = context.getBean(UserService.class);
        System.out.println(userService.sayHello());

        PayService payService = context.getBean(PayService.class);
        System.out.println(payService.pay());

        // 输出
        // user
        // user pay

        // 2 AOP
        AnnocationService annocationService = context.getBean(AnnocationService.class);
        annocationService.play();

        MethodRuleService methodRuleService = context.getBean(MethodRuleService.class);
        methodRuleService.play();

        // 输出
        // user
        // user pay
    }
}
