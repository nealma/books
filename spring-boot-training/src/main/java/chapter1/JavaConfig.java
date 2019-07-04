package chapter1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java 配置
 *
 * @author neal.ma
 * @date 2019/7/4
 * @blog nealma.com
 */
@Configuration // 标注当前是一个配置类
public class JavaConfig {
    /**
     * 普通 Bean 注入
     */
    @Bean
    public UserService userService(){
        return new UserService();
    }

    /**
     * 直接将 userService 作为参数传递给 payService() 方法
     * 在 Spring 容器中，只要容器中存在某个 Bean，就可以在另一个 Bean 声明的方法的参数中注入
     */
    @Bean
    public PayService payService(UserService userService){
        return new PayService(userService);
    }
}
