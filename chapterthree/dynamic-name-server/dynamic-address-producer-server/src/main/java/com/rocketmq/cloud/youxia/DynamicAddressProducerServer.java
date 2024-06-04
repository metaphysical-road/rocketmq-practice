package com.rocketmq.cloud.youxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DynamicAddressProducerServer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DynamicAddressProducerServer.class);
        springApplication.run();
        System.setProperty("rocketmq.namesrv.domain","127.0.0.1:8080");
    }
}
