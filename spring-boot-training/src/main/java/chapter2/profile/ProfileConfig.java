package chapter2.profile;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * config
 *
 * @author neal.ma
 * @date 2019/7/6
 * @blog nealma.com
 */
@Configuration
public class ProfileConfig {

    @Bean
    @Profile("test")
    public ProfileService test(){
        return new ProfileService("test");
    }

    @Bean
    @Profile("prod")
    public ProfileService prod(){
        return new ProfileService("prod");
    }
}
