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
    // 注解属性，也叫成员变量，没有形参；方法名即为属性名；方法类型即为属性类型；可以使用 default 设置默认值
    String  value() default "宙斯";
}
