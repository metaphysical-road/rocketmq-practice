package com.rocketmq.cloud.youxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
public class StrictOrderMessageConsumer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(StrictOrderMessageConsumer.class);
        springApplication.run();
    }
}
