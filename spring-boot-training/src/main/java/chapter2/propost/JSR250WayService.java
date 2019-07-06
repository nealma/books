package chapter2.propost;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * jsr-250
 *
 * @author neal.ma
 * @date 2019/7/6
 * @blog nealma.com
 */
@Slf4j
public class JSR250WayService {
    public JSR250WayService() {
        super();
        log.info("JSR250WayService construct");
    }
    // 构造函数执行完之后执行
    @PostConstruct
    public void init(){
        log.info("JSR250WayService init");
    }

    // 在 Bean 销毁之前执行
    @PreDestroy
    public void destroy(){
        log.info("JSR250WayService destroy");
    }
}
