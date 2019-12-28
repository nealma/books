package chapter2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan("chapter2.rest,chapter2.scope,chapter2.async")
@EnableAsync
public class Run2 {
    public static void main(String[] args) {
        SpringApplication.run(Run2.class, args);
    }

}
