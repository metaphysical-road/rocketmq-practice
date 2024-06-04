package com.rocketmq.cloud.youxia;

import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DynamicAddressConsumerServer {
    public static void main(String[] args){
        SpringApplication springApplication = new SpringApplication(DynamicAddressConsumerServer.class);
        springApplication.run();
        System.setProperty("rocketmq.namesrv.domain","127.0.0.1:8080");
    }
}
