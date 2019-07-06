package chapter2.async;

import org.springframework.stereotype.Component;

@Component
public class SyncTask {

    public void one() throws InterruptedException {
        Thread.sleep(1000);
    }

    public void two() throws InterruptedException {
        Thread.sleep(3000);
    }

    public void three() throws InterruptedException {
        Thread.sleep(2000);
    }
}
