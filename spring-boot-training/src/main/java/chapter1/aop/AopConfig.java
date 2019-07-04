package chapter1.aop;

import org.springframework.context.annotation.ComponentScan;
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
@ComponentScan("chapter1.aop")
public class AopConfig {

}
