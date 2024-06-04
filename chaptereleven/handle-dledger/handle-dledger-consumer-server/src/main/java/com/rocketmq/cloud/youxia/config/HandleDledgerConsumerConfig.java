package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class HandleDledgerConsumerConfig {
    private String instanceName;
    private String clientIp;
    private String namesrvAddr;
    private String topic;
    private Integer consumerNum;
    private String consumerGroup;
    private Integer consumerThreadMin;
    private Integer consumerThreadMax;
    private String isNeedModifyThreadNum;
    private String singleInstanceName;
    private String isSingleInstance;
    private String multiConsumerGroup;
    private String retryMessageIds;
    private Integer delayLevelWhenNextConsume;
    private Integer maxReconsumeTimes;

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
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

    public Integer getConsumerNum() {
        return consumerNum;
    }

    public void setConsumerNum(Integer consumerNum) {
        this.consumerNum = consumerNum;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public Integer getConsumerThreadMin() {
        return consumerThreadMin;
    }

    public void setConsumerThreadMin(Integer consumerThreadMin) {
        this.consumerThreadMin = consumerThreadMin;
    }

    public Integer getConsumerThreadMax() {
        return consumerThreadMax;
    }

    public void setConsumerThreadMax(Integer consumerThreadMax) {
        this.consumerThreadMax = consumerThreadMax;
    }

    public String getIsNeedModifyThreadNum() {
        return isNeedModifyThreadNum;
    }

    public void setIsNeedModifyThreadNum(String isNeedModifyThreadNum) {
        this.isNeedModifyThreadNum = isNeedModifyThreadNum;
    }

    public String getSingleInstanceName() {
        return singleInstanceName;
    }

    public void setSingleInstanceName(String singleInstanceName) {
        this.singleInstanceName = singleInstanceName;
    }

    public String getIsSingleInstance() {
        return isSingleInstance;
    }

    public void setIsSingleInstance(String isSingleInstance) {
        this.isSingleInstance = isSingleInstance;
    }

    public String getMultiConsumerGroup() {
        return multiConsumerGroup;
    }

    public void setMultiConsumerGroup(String multiConsumerGroup) {
        this.multiConsumerGroup = multiConsumerGroup;
    }

    public String getRetryMessageIds() {
        return retryMessageIds;
    }

    public void setRetryMessageIds(String retryMessageIds) {
        this.retryMessageIds = retryMessageIds;
    }

    public Integer getDelayLevelWhenNextConsume() {
        return delayLevelWhenNextConsume;
    }

    public void setDelayLevelWhenNextConsume(Integer delayLevelWhenNextConsume) {
        this.delayLevelWhenNextConsume = delayLevelWhenNextConsume;
    }

    public Integer getMaxReconsumeTimes() {
        return maxReconsumeTimes;
    }

    public void setMaxReconsumeTimes(Integer maxReconsumeTimes) {
        this.maxReconsumeTimes = maxReconsumeTimes;
    }
}
