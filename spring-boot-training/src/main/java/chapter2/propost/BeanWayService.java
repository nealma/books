package chapter2.propost;

import lombok.extern.slf4j.Slf4j;

/**
 * bean annotation
 *
 * @author neal.ma
 * @date 2019/7/6
 * @blog nealma.com
 */
@Slf4j
public class BeanWayService {
    public BeanWayService() {
        super();
        log.info("BeanWayService construct");
    }
    public void init(){
        log.info("BeanWayService init");
    }
    public void destroy(){
        log.info("BeanWayService destroy");
    }
}
