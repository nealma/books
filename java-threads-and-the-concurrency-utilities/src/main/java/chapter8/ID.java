package chapter8;

/**
 * 原子性
 *
 * @author neal.ma
 * @date 2019/6/29
 * @blog nealma.com
 */
public class ID {
    private volatile static long nextID = 1;

    public static void main(String[] args) {
        System.out.println(getNextID());
        System.out.println(nextID);
    }

    synchronized static long getNextID(){
        return nextID++;
    }

    // 输出
    // 1
    // 2
}
