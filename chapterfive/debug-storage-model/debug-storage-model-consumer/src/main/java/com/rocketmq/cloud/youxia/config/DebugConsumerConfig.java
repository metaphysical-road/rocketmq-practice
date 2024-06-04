package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class DebugConsumerConfig {
    private String openConsumer;

    public String getOpenConsumer() {
        return openConsumer;
    }

    public void setOpenConsumer(String openConsumer) {
        this.openConsumer = openConsumer;
    }
}
