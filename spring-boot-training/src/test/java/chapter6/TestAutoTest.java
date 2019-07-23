package chapter6;

import com.nealma.hello.service.Hello1Service;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class) // 在 Junit 环境下，提供 Spring TestContext Framework 的功能。
@Slf4j
public class TestAutoTest {

    @Autowired
    private Hello1Service helloService;


    @Test
    public void devInject() {
        helloService = new Hello1Service();
        log.info("hello {}", helloService.getMsg());
    }

}
