package chapter2.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * listener
 *
 * @author neal.ma
 * @date 2019/7/6
 * @blog nealma.com
 */
@Slf4j
@Component
public class MyEventListener implements ApplicationListener<MyEvent> { // 指定事件类

    // 对消息进行接收处理
    @Override
    public void onApplicationEvent(MyEvent event) {
        log.info("receive: {}", event.getMsg());
    }
}
