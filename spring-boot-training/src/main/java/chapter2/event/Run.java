package chapter2.event;

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
public class Run {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EventConfig.class);

        MyPublisher publisher = context.getBean(MyPublisher.class);

        MyEvent event = new MyEvent("tina");
        publisher.publish(event);

        context.close();
        // 输出
        // receive: tina
    }
}
