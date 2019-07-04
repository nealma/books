package chapter1;

/**
 * pay
 *
 * @author neal.ma
 * @date 2019/7/4
 * @blog nealma.com
 */
public class PayService {
    private UserService userService;
    public PayService(UserService userService) {
        this.userService = userService;
    }
    public String pay(){
        return userService.sayHello() + " pay";
    }
}
