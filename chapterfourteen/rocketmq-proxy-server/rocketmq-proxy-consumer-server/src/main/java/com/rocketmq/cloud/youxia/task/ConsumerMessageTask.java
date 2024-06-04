package com.rocketmq.cloud.youxia.task;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ConsumerMessageTask {

    @Scheduled(fixedRate = 1000)
    public void consumerMessage(){

    }
}
