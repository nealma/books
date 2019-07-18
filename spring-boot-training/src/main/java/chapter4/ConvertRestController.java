package chapter4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * 测试 controller
 */
@RestController
@Slf4j
public class ConvertRestController {

    @PostMapping(value = "/convert", produces = "application/x-nealma;charset=UTF-8")
    public @ResponseBody CustomBean convert(@RequestBody CustomBean customBean){
        return customBean;
    }

}
