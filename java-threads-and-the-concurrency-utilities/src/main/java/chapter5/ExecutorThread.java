package chapter5;

import java.util.concurrent.Executor;

/**
 * Executor 框架
 *
 * @author neal.ma
 * @date 2019/6/24
 * @blog nealma.com
 */
public class ExecutorThread {

    public static void main(String[] args) {
        Executor executor = command -> System.out.println("command");
        executor.execute(new RunnableTask());
    }
}

class RunnableTask implements Runnable{

    @Override
    public void run() {
        System.out.println("task run");
    }
}