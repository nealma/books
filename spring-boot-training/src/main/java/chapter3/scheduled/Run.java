package chapter3.scheduled;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 运行类
 *
 * @author neal.ma
 * @date 2019/7/7
 * @blog nealma.com
 */
public class Run {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(ScheduledTaskConfig.class);
        // delay[1000]: 2019-07-07 23:32:57.878
        // delay[1000]: 2019-07-07 23:32:58.884
        // rate[500]: 2019-07-07 23:32:59.325
        // rate[500]: 2019-07-07 23:32:59.827
        // cron[0 33 23 ？* *]: 2019-07-07 23:33:00.003
        // ......
    }
}
