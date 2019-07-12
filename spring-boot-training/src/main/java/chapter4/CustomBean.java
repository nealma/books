package chapter4;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 自定义 Bean
 */
@Data
@AllArgsConstructor
public class CustomBean {
    private String name;
    private String id;
    private String gender;
}
