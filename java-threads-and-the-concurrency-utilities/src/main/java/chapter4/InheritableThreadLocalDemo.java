package chapter4;

/**
 * 父子线程之间传递数据
 *
 * @author neal.ma
 * @date 2019/6/23
 * @blog nealma.com
 */
public class InheritableThreadLocalDemo {

    private static final InheritableThreadLocal<String> passValue = new InheritableThreadLocal();
    public static void main(String[] args) {

        Runnable runnableParent = () -> {
            passValue.set("Parent");

            Runnable runnableChild = () -> {
                Thread current = Thread.currentThread();
                System.out.println(current.getName() + "," +  passValue.get());
            };

            Thread child = new Thread(runnableChild);
            child.setName("Child");
            child.start();
            System.out.println("Parent -> " + passValue.get());

        };

        Thread parent = new Thread(runnableParent);
        parent.setName("Parent");
        parent.start();
        System.out.println("Main -> " + passValue.get());

        // 输出
        // Main -> null
        // Parent -> Parent
        // Child,Parent
    }
}
