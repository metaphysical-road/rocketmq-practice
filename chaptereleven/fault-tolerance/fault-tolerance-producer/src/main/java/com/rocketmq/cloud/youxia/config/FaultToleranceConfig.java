package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class FaultToleranceConfig {
    private String instanceName;
    private String clientIp;
    private String namesrvAddr;
    private String topic;
    private Integer producerNum;
    private String producerGroup;
    private Integer producerThreadMin;
    private Integer producerThreadMax;
    private String isNeedModifyThreadNum;
    private String singleInstanceName;
    private String isSingleInstance;
    private String multiProducerGroup;
    private String delay;
    private String delayTime;
    private String sendLatencyFaultEnable;

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

    public Integer getProducerNum() {
        return producerNum;
    }

    public void setProducerNum(Integer producerNum) {
        this.producerNum = producerNum;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public Integer getProducerThreadMin() {
        return producerThreadMin;
    }

    public void setProducerThreadMin(Integer producerThreadMin) {
        this.producerThreadMin = producerThreadMin;
    }

    public Integer getProducerThreadMax() {
        return producerThreadMax;
    }

    public void setProducerThreadMax(Integer producerThreadMax) {
        this.producerThreadMax = producerThreadMax;
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

    public String getMultiProducerGroup() {
        return multiProducerGroup;
    }

    public void setMultiProducerGroup(String multiProducerGroup) {
        this.multiProducerGroup = multiProducerGroup;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(String delayTime) {
        this.delayTime = delayTime;
    }

    public String getSendLatencyFaultEnable() {
        return sendLatencyFaultEnable;
    }

    public void setSendLatencyFaultEnable(String sendLatencyFaultEnable) {
        this.sendLatencyFaultEnable = sendLatencyFaultEnable;
    }
}
