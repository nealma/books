package chapter4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan("chapter4")
public class Run {
    public static void main(String[] args) {
        SpringApplication.run(Run.class, args);
    }

}
