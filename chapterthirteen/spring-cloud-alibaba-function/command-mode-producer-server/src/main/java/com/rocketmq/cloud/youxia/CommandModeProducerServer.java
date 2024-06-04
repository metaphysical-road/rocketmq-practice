package com.rocketmq.cloud.youxia;

import com.rocketmq.cloud.youxia.source.ProduceMessageSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableBinding({ProduceMessageSource.class})
@EnableScheduling
public class CommandModeProducerServer {
    public static void main(String[] args){
        SpringApplication springApplication = new SpringApplication(CommandModeProducerServer.class);
        springApplication.run();
        System.setProperty("rocketmq.namesrv.domain","127.0.0.1:8080");
    }
}
