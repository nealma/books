package chapter3.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 运行类
 *
 * @author neal.ma
 * @date 2019/7/8
 * @blog nealma.com
 */
public class Run {
    public static void main(String[] args){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CustomAnnotationConfig.class);
        CustomConfigurationService service = context.getBean(CustomConfigurationService.class);
        service.print();
        User user = context.getBean(User.class);
        user.listRoles();
        context.close();
        // 输出
        // Get Bean from custom annotation.
    }
}
