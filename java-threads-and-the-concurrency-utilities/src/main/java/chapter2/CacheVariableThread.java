package chapter2;

import java.math.BigDecimal;

/**
 * 缓存变量
 *
 * @author neal.ma
 * @date 2019/6/17
 * @blog nealma.com
 */
public class CacheVariableThread {

    public static BigDecimal result = null;

    public static BigDecimal compute(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) { e.printStackTrace(); }
        return new BigDecimal(10000);
    }
    public static void main(String[] args) {
        Runnable r1 = () -> {
            result = compute();
        };

        Thread t1 = new Thread(r1, "t1");
        t1.start();

        try {
            t1.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("result [" + result +"]");
    }
}
