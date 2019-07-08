package chapter3.annotation;

import java.lang.annotation.*;

/**
 * 角色注解，可重复使用
 *
 * @author neal.ma
 * @date 2019/7/8
 * @blog nealma.com
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Roles.class)
public @interface Role {
    String  value();
}
