package com.rocketmq.cloud.youxia.task;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ExecutorServiceCache {
    private ExecutorService executorService=Executors.newFixedThreadPool(2);

    public ExecutorService getExecutorService() {
        return executorService;
    }
}

