package chapter6;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableConfigurationProperties(HttpEncodingProperties.class)
@ConditionalOnClass(CharacterEncodingFilter.class) // CharacterEncodingFilter 类 在 类路径下
@ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)
// application.properties 中的配置  spring.http.encoding.enabled=false 这种条件下，不会注入 Bean
@Slf4j
public class HttpEncodingAutoConfiguration {

    @Autowired
    private HttpEncodingProperties httpEncodingProperties; // 属性注入, 通过 @EnableConfigurationProperties 声明

    @Bean // java config 方式 注入
    public CharacterEncodingFilter characterEncodingFilter(){
        log.info("characterEncodingFilter -- ");
        CharacterEncodingFilter characterEncodingFilter = new OrderedCharacterEncodingFilter();
        characterEncodingFilter.setEncoding(httpEncodingProperties.getCharset().name());
        characterEncodingFilter.setForceEncoding(httpEncodingProperties.isForce());
        return characterEncodingFilter;
    }
}
