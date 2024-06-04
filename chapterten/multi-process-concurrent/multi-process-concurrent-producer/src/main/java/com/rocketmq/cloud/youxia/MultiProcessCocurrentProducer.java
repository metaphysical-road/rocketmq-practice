package com.rocketmq.cloud.youxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultiProcessCocurrentProducer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MultiProcessCocurrentProducer.class);
        springApplication.run();
    }
}
