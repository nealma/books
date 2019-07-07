package chapter3.conditional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author neal.ma
 * @date 2019/7/7
 * @blog nealma.com
 */
@Configuration
public class ConditionConfig {

    /**
     * 通过 @Conditional 注解，符合 windows 条件，则实例化 WindowListService。
     */
    @Bean
    @Conditional(MacCondition.class)
    public ListService mac(){
        return new MacOSService();
    }
    /**
     * 通过 @Conditional 注解，符合 linux 条件，则实例化 LinuxListService。
     */
    @Bean
    @Conditional(LinuxCondition.class)
    public ListService linux(){
        return new LinuxOSService();
    }
}
