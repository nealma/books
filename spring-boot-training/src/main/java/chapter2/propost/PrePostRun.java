package chapter2.propost;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * main
 *
 * @author neal.ma
 * @date 2019/7/5
 * @blog nealma.com
 */
@Slf4j
public class PrePostRun {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrePostConfig.class);

        BeanWayService beanWayService = context.getBean(BeanWayService.class);
        JSR250WayService jsr250WayService = context.getBean(JSR250WayService.class);

        context.close();
        // 输出
        // BeanWayService construct
        // BeanWayService init
        // JSR250WayService construct
        // JSR250WayService init
        // JSR250WayService destroy
        // BeanWayService destroy
    }
}
