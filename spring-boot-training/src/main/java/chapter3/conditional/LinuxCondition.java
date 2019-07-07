package chapter3.conditional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 判定 Linux 条件
 *
 * @author neal.ma
 * @date 2019/7/7
 * @blog nealma.com
 */
@Slf4j
public class LinuxCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        log.info("matches Linux");
        return context.getEnvironment().getProperty("os.name").contains("Linux");
    }
}
