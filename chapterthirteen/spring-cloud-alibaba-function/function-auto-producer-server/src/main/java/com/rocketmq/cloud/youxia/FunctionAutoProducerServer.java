package com.rocketmq.cloud.youxia;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.function.Supplier;

@SpringBootApplication
public class FunctionAutoProducerServer {
    public static void main(String[] args){
        SpringApplication springApplication = new SpringApplication(FunctionAutoProducerServer.class);
        springApplication.run();
    }

    @Bean
    public Supplier<String> source1() {
        return () -> "test"+ RandomUtils.nextLong(0,100000000);
    }
}
