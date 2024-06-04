package com.rocketmq.cloud.youxia;

import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SingleProcessCocurrentConsumer {
    public static void main(String[] args) throws MQClientException {
        SpringApplication springApplication = new SpringApplication(SingleProcessCocurrentConsumer.class);
        springApplication.run();
    }
}
