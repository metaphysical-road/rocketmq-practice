package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class ConsumerConfig {
    private String nameAddress;
    private String topic;
    private String consumerGroup;
    private String clientIp;
    private String onlineConsumerInstanceName;
    private String offlineConsumerInstanceName;
    private String offline;

    public String getNameAddress() {
        return nameAddress;
    }

    public void setNameAddress(String nameAddress) {
        this.nameAddress = nameAddress;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getOnlineConsumerInstanceName() {
        return onlineConsumerInstanceName;
    }

    public void setOnlineConsumerInstanceName(String onlineConsumerInstanceName) {
        this.onlineConsumerInstanceName = onlineConsumerInstanceName;
    }

    public String getOfflineConsumerInstanceName() {
        return offlineConsumerInstanceName;
    }

    public void setOfflineConsumerInstanceName(String offlineConsumerInstanceName) {
        this.offlineConsumerInstanceName = offlineConsumerInstanceName;
    }

    public String getOffline() {
        return offline;
    }

    public void setOffline(String offline) {
        this.offline = offline;
    }
}
