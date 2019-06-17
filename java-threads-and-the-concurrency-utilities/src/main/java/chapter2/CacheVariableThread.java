package chapter2;

/**
 * 缓存变量
 *
 * @author neal.ma
 * @date 2019/6/17
 * @blog nealma.com
 */
public class CacheVariableThread {

    public static Long result = null;

    public static void compute(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) { e.printStackTrace(); }
        result = 10000L;
    }
    public static void main(String[] args) {
        Runnable r1 = () -> {
            compute();
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
