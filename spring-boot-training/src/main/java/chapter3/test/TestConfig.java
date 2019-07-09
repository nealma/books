package chapter3.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

public class TestConfig {

    @Bean
    @Profile("dev")
    public TestBean dev(){
        return new TestBean("dev");
    }
    @Bean
    @Profile("test")
    public TestBean test(){
        return new TestBean("test");
    }
    @Bean
    @Profile("prod")
    public TestBean prod(){
        return new TestBean("prod");
    }
}
