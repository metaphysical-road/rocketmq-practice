package com.rocketmq.cloud.youxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertmanagerDingdingWebServer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AlertmanagerDingdingWebServer.class);
        springApplication.run();
    }
}
