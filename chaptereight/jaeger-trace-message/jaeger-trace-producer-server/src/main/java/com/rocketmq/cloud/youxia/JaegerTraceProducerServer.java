package com.rocketmq.cloud.youxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JaegerTraceProducerServer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(JaegerTraceProducerServer.class);
        springApplication.run();
    }
}
