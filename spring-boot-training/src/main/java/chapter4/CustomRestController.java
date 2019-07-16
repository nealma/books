package chapter4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试 controller
 */
@RestController
@Slf4j
public class CustomRestController {
    // 只针对当前 Controller
    @InitBinder("a")
    public void initBinder(WebDataBinder webDataBinder){
        // 让name属性无法被接收
        log.info("CustomRestController initBinder: {}", webDataBinder.getAllowedFields());
        webDataBinder.setDisallowedFields("name");
    }
    @GetMapping(value = "/error")
    public String error(){
        return "failed";
    }
    @GetMapping(value = "/get", produces = "application/xml;charset=UTF-8")// 需引入依赖 jackson-dataformat-xml
    public CustomBean get(){
        return new CustomBean("web", "id", "gender");
    }
    @GetMapping(value = "/advice")
    public CustomBean advice(CustomBean customBean){
        int i = 0;
        int j = i/i;
        return customBean;
    }
    @GetMapping(value = "/disallowId", produces = "application/json;charset=UTF-8")
    public CustomBean disallowId(@ModelAttribute("a") CustomBean customBean){
        return customBean;
    }

    @GetMapping(value = "/a.txt")
    public String suffix(){
        return "ok";
    }

}
