package chapter2.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注意开启 @EnableAsync
 * @Async 调用中的事务处理机制
 *
 * 在 @Async 标注的方法，同时也适用了@Transactional进行了标注；在其调用数据库操作之时，将无法产生事务管理的控制，原因就在于其是基于异步处理的操作。
 *
 * 那该如何给这些操作添加事务管理呢？可以将需要事务管理操作的方法放置到异步方法内部，在内部被调用的方法上添加@Transactional.
 *
 * 例如：  方法A，使用了@Async/@Transactional来标注，但是无法产生事务控制的目的。
 *
 *        方法B，使用了@Async来标注，B中调用了C、D，C/D分别使用@Transactional做了标注，则可实现事务控制的目的。
 *
 */
@Component
public class AsyncTask {

    @Async
    public void one() throws InterruptedException {
        Thread.sleep(1000);
    }
    @Async
    public void two() throws InterruptedException {
        Thread.sleep(3000);
    }
    @Async
    public void three() throws InterruptedException {
        Thread.sleep(2000);
    }


    @Transactional
    public void A() throws InterruptedException {
        Thread.sleep(1000);
    }
    @Transactional
    public void B() throws InterruptedException {
        Thread.sleep(3000);
    }
    @Transactional
    public void C() throws InterruptedException {
        Thread.sleep(2000);
    }

    @Async
    public void transational() throws InterruptedException {
        A();
        B();
        C();
    }


}
