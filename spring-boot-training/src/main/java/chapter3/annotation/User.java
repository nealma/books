package chapter3.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * repeat demo
 *
 * @author neal.ma
 * @date 2019/7/8
 * @blog nealma.com
 */
@Slf4j
@Component
public class User {
    @Role("画家")
    @Role("艺术家")
    @Role("哲学家")
    public void listRoles(){
        Method[] methods = User.class.getMethods();
        Set<Role> roles = new HashSet<>(3);
        for(Method method : methods){
            log.info("listRoles method: {}", method);
            roles = AnnotatedElementUtils.getMergedRepeatableAnnotations(method, Role.class, Roles.class);
            if(!CollectionUtils.isEmpty(roles)){
                break;
            }
        }
        log.info("listRoles roles: {}", roles);

        roles.stream().forEach(item -> {
            log.info(item.value());
        });
    }
}
