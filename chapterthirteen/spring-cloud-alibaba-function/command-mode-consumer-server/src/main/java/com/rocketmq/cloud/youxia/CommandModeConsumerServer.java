package com.rocketmq.cloud.youxia;

import com.rocketmq.cloud.youxia.source.ConsumerMessageSink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableBinding({ConsumerMessageSink.class})
@EnableScheduling
public class CommandModeConsumerServer {
    public static void main(String[] args){
        SpringApplication springApplication = new SpringApplication(CommandModeConsumerServer.class);
        springApplication.run();
    }
}
