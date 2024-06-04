package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class ProducerConfig {
    private Integer orderPoolSize;
    private Integer threaPooldNum;
    private Integer clientNum;

    public Integer getOrderPoolSize() {
        return orderPoolSize;
    }

    public void setOrderPoolSize(Integer orderPoolSize) {
        this.orderPoolSize = orderPoolSize;
    }

    public Integer getThreaPooldNum() {
        return threaPooldNum;
    }

    public void setThreaPooldNum(Integer threaPooldNum) {
        this.threaPooldNum = threaPooldNum;
    }

    public Integer getClientNum() {
        return clientNum;
    }

    public void setClientNum(Integer clientNum) {
        this.clientNum = clientNum;
    }
}
