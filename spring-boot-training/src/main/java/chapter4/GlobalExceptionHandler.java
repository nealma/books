package chapter4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.ui.Model;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice // 控制器建言，实际是一个 @Component 组合注解
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView exception(Exception exception, WebRequest request){
        ModelAndView modelAndView = new ModelAndView("error");// error 页面
        modelAndView.addObject("errorMsg", exception.getMessage());
        return modelAndView;
    }

    @ModelAttribute // 将键值对添加到全局，所有 @RequestMapping 的方法可以获取到该键值对, 每次请求都会经过该方法
    public void addAttributes(Model model){
        model.addAttribute("msg", "other message");
        log.info("global model: {}", model);
        // 输出
        // global model: {msg=other message}
    }

    @InitBinder // 定制 WebDataBinder
    public void initBinder(DataBinder webDataBinder){
        log.info("initBinder: {}", webDataBinder.getAllowedFields());
        webDataBinder.setDisallowedFields("name"); // 忽略 request 请求中 id 参数
        webDataBinder.setDisallowedFields("id"); // 忽略 request 请求中 id 参数

        // 时间格式转换
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CustomDateEditor dateEditor = new CustomDateEditor(df, true);
        webDataBinder.registerCustomEditor(Date.class,dateEditor);
    }
}
