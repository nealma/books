package chapter3.scheduled;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 配置类
 *
 * @author neal.ma
 * @date 2019/7/7
 * @blog nealma.com
 */
@Configuration
@ComponentScan("chapter3.Scheduled")
@EnableScheduling
public class ScheduledTaskConfig{

}
