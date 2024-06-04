package com.rocketmq.cloud.youxia.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StartCreateServiceThread {

    @Autowired
    private CreateOrderService createOrderService;

    @PostConstruct
    public void start(){
        createOrderService.start();
    }
}
