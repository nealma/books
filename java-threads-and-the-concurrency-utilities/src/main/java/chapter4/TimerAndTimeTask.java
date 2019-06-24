package chapter4;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时器
 *
 * @author neal.ma
 * @date 2019/6/24
 * @blog nealma.com
 */
public class TimerAndTimeTask {

    public static void main(String[] args) {

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " run at " + System.currentTimeMillis());
                // 不设置，timer会一值执行
//                System.exit(0);
            }
        };
        // execute one-shot timer task after 2-second delay
        timer.schedule(task, 2000);

        TimerTask taskDelay = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " run at " + System.currentTimeMillis());
                // 不设置，timer会一值执行
            }
        };
        // delay 2-second, repeat every 1-second
        timer.schedule(taskDelay, 2000, 1000);

        TimerTask taskFixed = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " run at " + System.currentTimeMillis());
                // 此定时任务最近被实际调度执行的时间
                System.out.println(Thread.currentThread().getName() + " execute time: " + this.scheduledExecutionTime());
            }
        };
        // delay 2-second, repeat every 1-second
        timer.scheduleAtFixedRate(taskFixed, 2000, 1000);


        TimerTask taskCancel = new TimerTask() {
            @Override
            public void run() {

                System.out.println(Thread.currentThread().getName() + " cancel at " + System.currentTimeMillis());
                // cancel
                timer.cancel();
            }
        };
        // 5 秒之后 取消定时任务
        timer.schedule(taskCancel, new Date(System.currentTimeMillis() + 5000));
        // 输出
        // Timer-0 run at 1561338802370
        // Timer-0 run at 1561338802371
        // Timer-0 run at 1561338802371
        // Timer-0 execute time: 1561338802365
        // Timer-0 run at 1561338803370
        // Timer-0 execute time: 1561338803365
        // Timer-0 run at 1561338803371
        // Timer-0 run at 1561338804370
        // Timer-0 execute time: 1561338804365
        // Timer-0 run at 1561338804372
        // Timer-0 run at 1561338805369
        // Timer-0 execute time: 1561338805365
        // Timer-0 cancel at 1561338805369
    }
}
