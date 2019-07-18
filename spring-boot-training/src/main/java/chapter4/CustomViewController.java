package chapter4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 测试 controller
 */
@Controller
@Slf4j
public class CustomViewController {

    @GetMapping(value = "/index")
    public String index(){
        return "index";
    }

}
