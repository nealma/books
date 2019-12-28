package chapter6;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.Charset;

@ConfigurationProperties(prefix = "spring.http.encoding")//类型安全的配置 在 application.properties 中读取前缀为 spring.http.encoding 的所有属性
@Data // 使用 lombok
public class HttpEncodingProperties {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Charset charset = DEFAULT_CHARSET;
    private boolean force = true;
}
