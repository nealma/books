package chapter8;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Completion Service
 *
 * @author neal.ma
 * @date 2019/6/30
 * @blog nealma.com
 */
public class CompletionThread {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 创建一个 executorService , 以供后续"执行"任务
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        // 创建一个 completionService , 以供后续"完成"任务
        CompletionService completionService = new ExecutorCompletionService(executorService);

        // 使用 completionService.submit() 提交两个计算任务，每个任务都会异步执行
        completionService.submit(new CallableTask(100));
        completionService.submit(new CallableTask(200));

        // result 1, 使用 take() 方法返回任务的 feature 对象，阻塞等待
        Future<BigDecimal> future = completionService.take();
        // 使用 future.get() 获取任务的结果
        System.out.println("result1: " + future.get());

        // result 2, 使用 take() 方法返回任务的 feature 对象，阻塞等待
        future = completionService.take();
        // 使用 future.get() 获取任务的结果
        System.out.println("result2: " + future.get());

        // 关闭
        executorService.shutdown();
        // 输出
        // result1: 200
        // result2: 100
    }
}

/**
 * 模拟耗时任务
 */
class CallableTask implements Callable<BigDecimal> {
    private int max;

    public CallableTask(int max) {
        this.max = max;
    }

    @Override
    public BigDecimal call() throws Exception {
        // long-running
        Thread.sleep(new Random().nextInt(1000));
        return BigDecimal.valueOf(max);
    }
}