package chapter2.event;

import org.springframework.context.ApplicationEvent;

/**
 * event
 *
 * @author neal.ma
 * @date 2019/7/6
 * @blog nealma.com
 */
public class MyEvent extends ApplicationEvent {
    private String msg;
    public MyEvent(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
