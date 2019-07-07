package chapter3.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * task
 *
 * @author neal.ma
 * @date 2019/7/7
 * @blog nealma.com
 */
@Slf4j
@Service
public class TaskService {

    /**
     * @Async 注解表明该方法是个异步方法
     *        如果注解在类级别，则表明该类的所有方法都是异步方法
     * 注意开启 @EnableAsync
     * @Async 调用中的事务处理机制
     * 在 @Async 标注的方法，同时也适用了@Transactional进行了标注；在其调用数据库操作之时，将无法产生事务管理的控制，原因就在于其是基于异步处理的操作。
     * 那该如何给这些操作添加事务管理呢？可以将需要事务管理操作的方法放置到异步方法内部，在内部被调用的方法上添加@Transactional.
     * 例如：  方法A，使用了@Async/@Transactional来标注，但是无法产生事务控制的目的。
     *        方法B，使用了@Async来标注，B中调用了C、D，C/D分别使用@Transactional做了标注，则可实现事务控制的目的。
     */
    @Async
    public void one() throws InterruptedException {
        Thread.sleep(1000);
        log.info("-> one");
    }
    @Async
    public void two() throws InterruptedException {
        Thread.sleep(3000);
        log.info("-> two");
    }
    @Async
    public void three() throws InterruptedException {
        Thread.sleep(2000);
        log.info("-> three");
    }
}
