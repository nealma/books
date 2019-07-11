package chapter4;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试 controller
 */
@RestController
public class CustomRestController {
    @InitBinder
    public void initBinder(WebDataBinder binder){
        //让name属性无法被接收
        binder.setDisallowedFields("name");
    }
    @GetMapping(value = "/error")
    public String error(){
        return "failed";
    }
    @GetMapping(value = "/get", produces = "application/xml;charset=UTF-8")// 需引入依赖 jackson-dataformat-xml
    public CustomBean get(){
        return new CustomBean("web", "id");
    }
    @GetMapping(value = "/advice")
    public CustomBean advice(CustomBean customBean){
        int i = 0;
        int j = i/i;
        return customBean;
    }
    @GetMapping(value = "/disallowId", produces = "application/json;charset=UTF-8")
    public CustomBean disallowId(CustomBean customBean){
        return customBean;
    }

}
