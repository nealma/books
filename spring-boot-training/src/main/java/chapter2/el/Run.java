package chapter2.el;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * msg
 *
 * @author neal.ma
 * @date 2019/7/5
 * @blog nealma.com
 */
@Slf4j
public class Run {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ElConfig.class);

        ElConfig config = context.getBean(ElConfig.class);
        config.print();
        // 输出
        // singleton compare: true
        // prototype compare: false
    }
}
