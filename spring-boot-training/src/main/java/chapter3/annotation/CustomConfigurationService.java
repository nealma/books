package chapter3.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 自定义注解验证类
 *
 * @author neal.ma
 * @date 2019/7/8
 * @blog nealma.com
 */
@Service
@Slf4j
public class CustomConfigurationService {
    public void print(){
      log.info("Get Bean from custom annotation.");
    }
}
