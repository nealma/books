package chapter3.aware;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * 运行类
 *
 * @author neal.ma
 * @date 2019/7/7
 * @blog nealma.com
 */
public class Run {
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AwareConfig.class);
        AwareService awareService = context.getBean(AwareService.class);
        awareService.print();
        context.close();
    }
}
