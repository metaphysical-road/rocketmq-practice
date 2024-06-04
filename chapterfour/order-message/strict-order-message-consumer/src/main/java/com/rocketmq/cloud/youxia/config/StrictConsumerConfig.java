package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class StrictConsumerConfig {
    private Integer consumerClientNum;

    public Integer getConsumerClientNum() {
        return consumerClientNum;
    }

    public void setConsumerClientNum(Integer consumerClientNum) {
        this.consumerClientNum = consumerClientNum;
    }
}
