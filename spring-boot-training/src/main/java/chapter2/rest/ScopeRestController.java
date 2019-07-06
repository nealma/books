package chapter2.rest;

import chapter2.scope.IRequestBean;
import chapter2.scope.ISessionBean;
import chapter2.scope.InstanceService;
import chapter2.scope.SingletonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
public class ScopeRestController {

    @Autowired
    private InstanceService instanceService1;

    @Autowired
    private InstanceService instanceService2;

    @GetMapping("scope/checkScope")
    public List<Object> checkScope() {

        List<Object> list = new ArrayList<>();

        // prototype
        log.info("instanceService1 == instanceService2 -> {}", instanceService1 == instanceService2);

        // singleton
        SingletonService singletonService1 = instanceService1.getSingletonService();
        SingletonService singletonService2 = instanceService1.getSingletonService();
        log.info("singletonService1 == singletonService2 -> {}", singletonService1 == singletonService2);

        // request
        IRequestBean requestBean1 = instanceService1.getIRequestBean();
        IRequestBean requestBean2 = instanceService1.getIRequestBean();
        log.info("requestBean1 == requestBean2 -> {}", requestBean1 == requestBean2);

        // session
        ISessionBean sessionBean1 = instanceService1.getISessionBean();
        ISessionBean sessionBean2 = instanceService1.getISessionBean();
        log.info("sessionBean1 == sessionBean2 -> {}", sessionBean1 == sessionBean2);

        // different request
        log.info("request id: {}", instanceService1.getIRequestBean().getId());
        log.info("request id: {}", instanceService2.getIRequestBean().getId());

        // different session
        log.info("session id: {}", instanceService1.getISessionBean().getId());
        log.info("session id: {}", instanceService2.getISessionBean().getId());

        return list;
    }

}
