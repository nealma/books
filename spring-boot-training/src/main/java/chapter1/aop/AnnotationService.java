package chapter1.aop;

import org.springframework.stereotype.Service;

/**
 * 使用注解的被拦截类
 *
 * @author neal.ma
 * @date 2019/7/4
 * @blog nealma.com
 */
@Service
public class AnnotationService {
    @Action(name = "注解式拦截的 play 操作")
    public void play(){}
}
