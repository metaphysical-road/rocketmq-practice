package com.rocketmq.cloud.youxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CustomHookProducerServer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(CustomHookProducerServer.class);
        springApplication.run();
    }
}
