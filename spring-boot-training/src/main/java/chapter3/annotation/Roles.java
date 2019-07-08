package chapter3.annotation;

import java.lang.annotation.*;

/**
 * 角色容器
 *
 * @author neal.ma
 * @date 2019/7/8
 * @blog nealma.com
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Roles {
    Role[]  value();
}
