package chapter8;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * Fork/Join
 * 从 1 数到 10，分两个任务
 *
 * @author neal.ma
 * @date 2019/6/30
 * @blog nealma.com
 */
public class ForkJoinAction {

    public static void main(String[] args) throws InterruptedException {

        // 创建 ForkJoinPool 实例，默认包含 Runtime.getRuntime().availableProcessors() 个工作线程
        final ForkJoinPool pool = new ForkJoinPool();
        // 无结果
        pool.invoke(new CounterAction(1,10));

        // 等待结果，超时结束
        pool.awaitTermination(10, TimeUnit.SECONDS);

        //关闭线程池
        pool.shutdown();
        // 输出
        // ForkJoinPool-1-worker-1 start: 1, end: 10
        // ForkJoinPool-1-worker-2 i: 1
        // ForkJoinPool-1-worker-3 i: 6
        // ForkJoinPool-1-worker-2 i: 2
        // ForkJoinPool-1-worker-2 i: 3
        // ForkJoinPool-1-worker-3 i: 7
        // ForkJoinPool-1-worker-2 i: 4
        // ForkJoinPool-1-worker-3 i: 8
        // ForkJoinPool-1-worker-2 i: 5
        // ForkJoinPool-1-worker-2 start: 1, end: 5
        // ForkJoinPool-1-worker-3 i: 9
        // ForkJoinPool-1-worker-3 i: 10
        // ForkJoinPool-1-worker-3 start: 6, end: 10
    }
}

/**
 * 从 1 数到 10 ， 分成连个任务。
 * 无返回结果
 */
class CounterAction extends RecursiveAction {

    /**
     * 开始
     */
    private int start;
    /**
     * 结束
     */
    private int end;

    public CounterAction(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {

        if (end - start < 5) {
            for (int i=start; i<=end; i++) {
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " i: " + i);
            }
        } else {
            CounterAction t1 = new CounterAction(1,5);
            CounterAction t2 = new CounterAction(6,10);
            // 并行执行两个小任务
            t1.fork();
            t2.fork();
        }
        System.out.println(Thread.currentThread().getName() + " start: " + start + ", end: " + end);
    }
}