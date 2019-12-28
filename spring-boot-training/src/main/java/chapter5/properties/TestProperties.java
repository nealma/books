package chapter5.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类型安全的配置
 *
 * prefix 的值 匹配 application.properties文件中前缀
 *
 * test.id=1
 * test.name=tina
 *
 * @author neal.ma
 * @date 2019/7/19
 * @blog nealma.com
 */
@Component
@ConfigurationProperties(prefix = "test")
public class TestProperties {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
