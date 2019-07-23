package chapter3;

import chapter3.test.TestBean;
import chapter3.test.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class) // 在 Junit 环境下，提供 Spring TestContext Framework 的功能。
@ContextConfiguration(classes = TestConfig.class) // 用来加载配置 ApplicationContext，classes 用来加载配置类
@ActiveProfiles("dev") // 用来声明活动的 Profile
@Slf4j
public class TestBeanTest {
    @Autowired
    private TestBean bean;

    @Test
    public void devInject() {
        // 通过 JUnit 的 Assert 来校验结果是否和预期一样
        Assert.assertEquals("dev", bean.getContent());
    }

}
