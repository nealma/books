package chapter2.profile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * main
 *
 * @author neal.ma
 * @date 2019/7/5
 * @blog nealma.com
 */
@Slf4j
public class Run {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 激活 test Profile
        context.getEnvironment().setActiveProfiles("test");
        // 注册 Bean 配置类
        context.register(ProfileConfig.class);
        // 刷新容器
        context.refresh();

        ProfileService profileService = context.getBean(ProfileService.class);

        log.info("name: {}", profileService.getName());

        context.close();
        // 输出
        // name: test
    }
}
