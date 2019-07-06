package chapter2.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

/**
 * request scope
 * 线程级缓存
 *
 * @author neal.ma
 * @date 2019/7/5
 * @blog nealma.com
 */
@Service
@Scope(value=WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.INTERFACES)
public class RequestService implements IRequestBean{

    private String id = UUID.randomUUID().toString();
    @Override
    public String getId(){
        return id;
    }
}
