package com.rocketmq.cloud.youxia;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.rocketmq.cloud.youxia.mapper")
@EnableDiscoveryClient
public class SupplyServer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(SupplyServer.class);
        springApplication.run();
    }
}
