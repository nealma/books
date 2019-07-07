package chapter3.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/**
 * 自定义组合注解
 *
 * 顺便学习下几个元注解
 * @Retention 保留期，解释这个注解的存活时间
 *            取值：
 *            1. RetentionPolicy.SOURCE 只在源码阶段保留，在编译器编译时会被丢弃忽视。
 *            2. RetentionPolicy.CLASS 直到编译字节码的阶段一直保留，不会被加载到 VM。
 *            3. RetentionPolicy.RUNTIME 一直保留包运行时阶段，被加载到 VM，程序中可以读取。
 * @Documented 跟文档相关，可以将注解的元素包含到 javadoc 中。
 * @Target 标注这个元素可以在那个地方使用
 *            1. ElementType.TYPE 可以注解在 类、接口、注解、枚举
 *            2. ElementType.FIELD 可以注解在 属性
 *            3. ElementType.METHOD 可以注解在 方法
 *            4. ElementType.PARAMETER 可以注解在 方法参数
 *            5. ElementType.CONSTRUCTOR 可以注解在 构造函数
 *            6. ElementType.ANNOTATION_TYPE 可以注解一个注解
 *            7. ElementType.PACKAGE 可以注解在 包
 *            8. ElementType.LOCAL_VARIABLE 可以注解在 局部变量
 *            9. ElementType.TYPE_PARAMETER 同 PARAMETER
 *            10. ElementType.TYPE_USE 同 TYPE
 * @Inherited 继承，父类使用 @Inherited 注解，则子类会继承父类的所有注解，即使子类什么注解都没有。
 * @Repeatable 可重复的
 *
 *      @interface Role {
 * 	        User[]  value();
 *      }
 *
 *      @Repeatable(Role.class)
 *      @interface User{
 * 	        String role default "";
 *      }
 *
 *      @User(role="ARTIST")
 *      @User(role="CODER")
 *      @User(role="PM")
 *      public class DaNiu{
 *
 *      }
 *  注解的属性
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@ComponentScan
public @interface CustomConfiguration {
    String[] value() default {};
}
