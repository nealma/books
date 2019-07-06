package chapter2.rest;

import chapter2.async.AsyncTask;
import chapter2.async.SyncTask;
import chapter2.scope.InstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class TaskRestController {

    @Autowired
    private SyncTask syncTask;

    @Autowired
    private AsyncTask asyncTask;

    @GetMapping("syncTask")
    public String syncTask() throws InterruptedException {

        long startTime = System.currentTimeMillis();

        syncTask.one();
        syncTask.two();
        syncTask.three();

        long endTime = System.currentTimeMillis();

        log.info("cost time(s) : {}", endTime - startTime);

        return "ok";
    }

    @GetMapping("asyncTask")
    public String asyncTask() throws InterruptedException {

        long startTime = System.currentTimeMillis();

        asyncTask.one();
        asyncTask.two();
        asyncTask.three();

        long endTime = System.currentTimeMillis();

        log.info("cost time(s) : {}", endTime - startTime);

        return "ok";
    }
}
