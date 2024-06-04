package com.rocketmq.cloud.youxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductSourceServer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ProductSourceServer.class);
        springApplication.run();
    }
}
