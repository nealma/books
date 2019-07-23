package chapter6;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Run6 {
    @Autowired
    private HelloService helloService;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Run6.class);
        application.setBannerMode(Banner.Mode.OFF); // 关闭 banner
        application.run(args);
    }
}