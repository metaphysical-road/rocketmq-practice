package com.rocketmq.cloud.youxia.service;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class RocketmqTask {
    @Autowired
    private SenderService senderService;

    private volatile boolean isStart=false;

    public void startRunSendMessage(){
        ExecutorService executorService= Executors.newFixedThreadPool(1 );
        executorService.execute(new SendMessage());
        isStart=true;
    }
    class SendMessage implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000);
                    String msgContent = "msg-" + RandomUtils.nextLong(0, 100000000);
                    senderService.send(msgContent);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    public void start() {
        if(!isStart){
            startRunSendMessage();
        }
    }
}
