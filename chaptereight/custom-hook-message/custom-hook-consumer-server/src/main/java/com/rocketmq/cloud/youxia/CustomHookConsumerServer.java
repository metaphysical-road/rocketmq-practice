package com.rocketmq.cloud.youxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CustomHookConsumerServer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(CustomHookConsumerServer.class);
        springApplication.run();
    }
}
