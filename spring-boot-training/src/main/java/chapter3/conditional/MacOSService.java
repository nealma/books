package chapter3.conditional;

import lombok.extern.slf4j.Slf4j;

/**
 * service
 *
 * @author neal.ma
 * @date 2019/7/8
 * @blog nealma.com
 */
@Slf4j
public class MacOSService implements ListService{
    @Override
    public void os(){
       log.info("Mac OS X");
    }
}
