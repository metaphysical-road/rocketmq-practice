package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class AgencyProducerConfig {
    private String producerGroupName;
    private String instanceName;
    private String transactionInstanceName;
    private String clientIp;
    private String namesrvAddr;
    private String topic;
    private String transactionTopic;
    private String unitName;
    private Integer topicQueueNums;
    private Integer delayTimeLevel;
    private String isOpenException;

    public String getProducerGroupName() {
        return producerGroupName;
    }

    public void setProducerGroupName(String producerGroupName) {
        this.producerGroupName = producerGroupName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getTransactionInstanceName() {
        return transactionInstanceName;
    }

    public void setTransactionInstanceName(String transactionInstanceName) {
        this.transactionInstanceName = transactionInstanceName;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTransactionTopic() {
        return transactionTopic;
    }

    public void setTransactionTopic(String transactionTopic) {
        this.transactionTopic = transactionTopic;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Integer getTopicQueueNums() {
        return topicQueueNums;
    }

    public void setTopicQueueNums(Integer topicQueueNums) {
        this.topicQueueNums = topicQueueNums;
    }

    public Integer getDelayTimeLevel() {
        return delayTimeLevel;
    }

    public void setDelayTimeLevel(Integer delayTimeLevel) {
        this.delayTimeLevel = delayTimeLevel;
    }

    public String getIsOpenException() {
        return isOpenException;
    }

    public void setIsOpenException(String isOpenException) {
        this.isOpenException = isOpenException;
    }
}
