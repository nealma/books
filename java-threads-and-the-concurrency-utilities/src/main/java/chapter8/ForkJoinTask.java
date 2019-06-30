package chapter8;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Fork/Join
 * 从 1 数到 10，分两个任务
 *
 * @author neal.ma
 * @date 2019/6/30
 * @blog nealma.com
 */
public class ForkJoinTask {

    public static void main(String[] args) throws InterruptedException {

        // 创建 ForkJoinPool 实例，默认包含 Runtime.getRuntime().availableProcessors() 个工作线程
        final ForkJoinPool pool = new ForkJoinPool();
        // 有结果
        pool.invoke(new SumTask(1,10));

        // 等待结果，超时结束
        pool.awaitTermination(10, TimeUnit.SECONDS);

        //关闭线程池
        pool.shutdown();

        // 输出
        // ForkJoinPool-1-worker-3 sum: 40
        // ForkJoinPool-1-worker-2 sum: 15
        // ForkJoinPool-1-worker-1 sum: 55
    }
}

/**
 * 从 1 加到 10 ， 分成两个任务。
 * 有返回结果
 */
class SumTask extends RecursiveTask<Integer> {

    /**
     * 开始
     */
    private int start;
    /**
     * 结束
     */
    private int end;

    public SumTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        if (end - start < 5) {
            for (int i=start; i<=end; i++) {
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sum += i;
            }
        } else {
            SumTask t1 = new SumTask(1,5);
            SumTask t2 = new SumTask(6,10);
            // 并行两个小任务
            t1.fork();
            t2.fork();
            // 把两个小任务的返回结果合并
            sum = t1.join() + t2.join();
        }
        System.out.println(Thread.currentThread().getName() + " sum: " + sum);
        return sum;
    }
}
