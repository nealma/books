package chapter2.scope;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * prototype scope
 * 多实例
 *
 * @author neal.ma
 * @date 2019/7/5
 * @blog nealma.com
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Data
public class InstanceService {

    @Autowired
    private IRequestBean iRequestBean;

    @Autowired
    private ISessionBean iSessionBean;

    @Autowired
    private SingletonService singletonService;
}
