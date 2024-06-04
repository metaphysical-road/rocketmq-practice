package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class TransactionProducerConfig {
    private Integer producerNum;

    public Integer getProducerNum() {
        return producerNum;
    }

    public void setProducerNum(Integer producerNum) {
        this.producerNum = producerNum;
    }
}
