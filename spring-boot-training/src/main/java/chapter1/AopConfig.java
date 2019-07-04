package chapter1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP 配置类
 *
 * @author neal.ma
 * @date 2019/7/4
 * @blog nealma.com
 */
@Configuration // 标注当前是一个配置类
@EnableAspectJAutoProxy // 开启 Spring 对 AspectJ 的支持
public class AopConfig {
    @Bean
    public MethodRuleService methodRuleService(){
        return new MethodRuleService();
    }

    @Bean
    public AnnocationService annocationService(){
        return new AnnocationService();
    }
}
