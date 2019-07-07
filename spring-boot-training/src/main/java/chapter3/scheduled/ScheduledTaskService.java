package chapter3.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定时任务
 *
 * @author neal.ma
 * @date 2019/7/7
 * @blog nealma.com
 */
@Service
@Slf4j
public class ScheduledTaskService {
    /**
     * @Scheduled 声明该方法是计划任务，使用 fixedRate 属性每隔固定时间执行。
     */
    @Scheduled(fixedRate = 500)
    public void printRateDateTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        log.info("rate[500]: {}", LocalDateTime.now().format(formatter));
    }
    /**
     * @Scheduled 声明该方法是计划任务，使用 fixedDelay 属性延迟固定时间执行。
     */
    @Scheduled(fixedDelay = 1000)
    public void printDelayDateTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        log.info("delay[1000]: {}", LocalDateTime.now().format(formatter));
    }
    /**
     * @Scheduled 声明该方法是计划任务，使用 cron 属性可按照指定时间执行。
     * cron 是UNIX 或 类 UNIX（Linux）系统下的定时任务
     * 例子：每天 23：25 执行, 如果时间是过去时，不再执行。
     */
    @Scheduled(cron = "0 33 23 * * ?")
    public void printCronDateTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        log.info("cron[0 33 23 ？* *]: {}", LocalDateTime.now().format(formatter));
    }
}
