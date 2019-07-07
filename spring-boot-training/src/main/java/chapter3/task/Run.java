package chapter3.task;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * 运行类
 *
 * @author neal.ma
 * @date 2019/7/7
 * @blog nealma.com
 */
public class Run {
    public static void main(String[] args) throws IOException, InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TaskConfig.class);
        TaskService taskService = context.getBean(TaskService.class);
        for (int i=0; i<3; i++){
            taskService.one();
            taskService.two();
            taskService.three();
        }
        context.close();
        // 结果是不是按照顺序执行，也就证明任务是异步执行。
        // -> one
        // -> one
        // -> three
        // -> one
        // -> two
        // -> three
        // -> two
        // -> three
        // -> two
    }
}
