package chapter3.conditional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 运行类
 *
 * @author neal.ma
 * @date 2019/7/7
 * @blog nealma.com
 */
public class Run {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConditionConfig.class);
        ListService listService = context.getBean(ListService.class);
        listService.os();
        System.out.println(context.getEnvironment().getProperty("os.name"));
        context.close();
        // Mac OS X
        // Mac OS X
    }
}
