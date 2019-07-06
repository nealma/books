package chapter2.el;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

/**
 * 配置类
 *
 * @author neal.ma
 * @date 2019/7/6
 * @blog nealma.com
 */
@Slf4j
@Configuration
@ComponentScan("chapter2.el")
@PropertySource("application.properties")
public class ElConfig {
    // 注入普通字符串
    @Value("normal")
    private String normal;

    // 注入操作系统属性
    @Value("#{systemProperties['os.name']}")
    private String osName;

    // 注入 表达式结果
    @Value("#{T(java.lang.Math).random()}")
    private double random;

    // 注入其他 Bean 属性
    @Value("#{elService.name}")
    private String elServiceName;

    // 注入文件资源
    @Value("test.properties")
    private Resource resourceFile;

    // 注入网址资源
    @Value("http://www.aliyun.com")
    private Resource resourceUrl;

    // 注入配置文件
    @Value("${neal.ma}")
    private String nealmaName;

    @Autowired
    private Environment environment;

    public void print() throws IOException {
        log.info("normal: {}", normal);
        log.info("osName: {}", osName);
        log.info("random: {}", random);
        log.info("elServiceName: {}", elServiceName);
        log.info("resourceFile: {}", FileCopyUtils.copyToByteArray(resourceFile.getInputStream()));
        log.info("resourceUrl: {}", FileCopyUtils.copyToByteArray(resourceUrl.getInputStream()));
        log.info("nealmaName: {}", nealmaName);
        log.info("neal.ma: {}", environment.getProperty("neal.ma"));
    }

}
