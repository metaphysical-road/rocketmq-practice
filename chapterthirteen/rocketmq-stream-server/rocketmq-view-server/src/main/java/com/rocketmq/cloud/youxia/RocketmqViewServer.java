package com.rocketmq.cloud.youxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class RocketmqViewServer {
    public static void main(String[] args){
        SpringApplication springApplication = new SpringApplication(RocketmqViewServer.class);
        springApplication.run();
    }
}
