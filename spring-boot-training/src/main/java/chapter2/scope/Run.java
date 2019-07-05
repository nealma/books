package chapter2.scope;

import chapter2.scope.PrototypeService;
import chapter2.scope.ScopeConfig;
import chapter2.scope.SingletonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * msg
 *
 * @author neal.ma
 * @date 2019/7/5
 * @blog nealma.com
 */
@Slf4j
public class Run {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ScopeConfig.class);

        SingletonService singletonService = context.getBean(SingletonService.class);
        SingletonService singletonService2 = context.getBean(SingletonService.class);

        log.info("singleton compare: {}", singletonService == singletonService2);

        PrototypeService prototypeService = context.getBean(PrototypeService.class);
        PrototypeService prototypeService2 = context.getBean(PrototypeService.class);

        log.info("prototype compare: {}", prototypeService == prototypeService2);

        // 输出
        // singleton compare: true
        // prototype compare: false
    }
}
