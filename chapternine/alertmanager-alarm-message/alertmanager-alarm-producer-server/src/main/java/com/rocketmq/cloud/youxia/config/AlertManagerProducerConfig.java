package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class AlertManagerProducerConfig {
    private String nameAddress;
    private String topic;
    private String producerGroup;
    private String clientIp;
    private String instanceName;
    private Integer batchSize;
    private Long sleepTime;
    private String onlineProducerInstanceName;
    private String offlineProducerInstanceName;
    private String offlineProducer;

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

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getOnlineProducerInstanceName() {
        return onlineProducerInstanceName;
    }

    public void setOnlineProducerInstanceName(String onlineProducerInstanceName) {
        this.onlineProducerInstanceName = onlineProducerInstanceName;
    }

    public String getOfflineProducerInstanceName() {
        return offlineProducerInstanceName;
    }

    public void setOfflineProducerInstanceName(String offlineProducerInstanceName) {
        this.offlineProducerInstanceName = offlineProducerInstanceName;
    }

    public String getOfflineProducer() {
        return offlineProducer;
    }

    public void setOfflineProducer(String offlineProducer) {
        this.offlineProducer = offlineProducer;
    }
}
