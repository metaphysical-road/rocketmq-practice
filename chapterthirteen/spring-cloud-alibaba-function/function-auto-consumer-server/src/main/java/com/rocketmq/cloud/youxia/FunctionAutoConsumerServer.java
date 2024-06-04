package com.rocketmq.cloud.youxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.function.Consumer;

@SpringBootApplication
@EnableScheduling
public class FunctionAutoConsumerServer {
    public static void main(String[] args){
        SpringApplication springApplication = new SpringApplication(FunctionAutoConsumerServer.class);
        springApplication.run();
    }

    @Bean
    public Consumer<String> sink1(){
        return String->System.out.println(String);
    }
}
