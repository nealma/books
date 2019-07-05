package chapter2.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * singleton
 *
 * @author neal.ma
 * @date 2019/7/5
 * @blog nealma.com
 */
@Service
@Scope("singleton") // 可以不显示使用，Spring 默认配置
public class SingletonService {
}
