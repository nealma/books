package chapter5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * ExecutorService
 *
 * @author neal.ma
 * @date 2019/6/24
 * @blog nealma.com
 */
public class ExecutorServiceThread {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // execute(Runnable)
        executorService.execute(new RunnableTask());

        // submit(Runnable)
        Future future = executorService.submit(new RunnableTask());
        try {
            System.out.println("result: " + future.get()); //result: null
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // submit(Callable)
        Future<String> futureResult = executorService.submit(new CallableTask());
        try {
            while (!futureResult.isDone()){
                System.out.println("waiting");
            }
            System.out.println("result: " + futureResult.get()); //result: async callable
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // invokeAny
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(new CallableTask1());
        tasks.add(new CallableTask2());
        tasks.add(new CallableTask3());
        try {
            String result = executorService.invokeAny(tasks);
            System.out.println("result: " + result); // result: async callable 2

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // invokeAll
        try {
            List<Future<String>> futures = executorService.invokeAll(tasks);
            futures.forEach(item -> {
                try {
                    System.out.println("result: " + item.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 输出
        // java.lang.ArithmeticException: / by zero
        // result: async callable 2
        // result: async callable 3


        // shutdown
        executorService.shutdown();

//        executorService.shutdownNow();

    }
}

class RunnableTask1 implements Runnable{

    @Override
    public void run() {
        System.out.println("task run");
    }
}
class CallableTask implements Callable {

    @Override
    public Object call() throws Exception {
        Thread.sleep(3000);
        return "async callable";
    }
}

class CallableTask1 implements Callable {

    @Override
    public Object call() throws Exception {
        int i = 1 / 0;
        return "async callable 1";
    }
}
class CallableTask2 implements Callable {

    @Override
    public Object call() throws Exception {
        return "async callable 2";
    }
}
class CallableTask3 implements Callable {

    @Override
    public Object call() throws Exception {
        return "async callable 3";
    }
}