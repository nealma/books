package chapter2.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 事件发布类
 *
 * @author neal.ma
 * @date 2019/7/6
 * @blog nealma.com
 */
@Component
public class MyPublisher {

    @Autowired
    private ApplicationContext applicationContext;

    public void publish(MyEvent event){
        applicationContext.publishEvent(event);
    }
}
