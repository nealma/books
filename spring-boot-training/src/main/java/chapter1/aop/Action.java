package chapter1.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 拦截规则的注解
 *
 * @author neal.ma
 * @date 2019/7/4
 * @blog nealma.com
 */
@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface Action {
    String name();
}
