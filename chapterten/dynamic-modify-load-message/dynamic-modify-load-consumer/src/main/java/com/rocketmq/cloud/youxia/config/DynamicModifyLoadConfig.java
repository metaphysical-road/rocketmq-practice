package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config.multi")
public class DynamicModifyLoadConfig {
    private String instanceName;
    private String clientIp;
    private String namesrvAddr;
    private String topic;
    private Integer consumerNum;
    private String consumerGroup;
    private Integer consumerThreadMin;
    private Integer consumerThreadMax;
    private String isNeedModifyThreadNum;
    private String isModifyLoadBalanceStrategy;
    private Integer loadBalanceStrategy;
    private Integer machineRoomNearbyType;
    private String messageQueueAndBrokerName;
    private Integer virtualNodeCnt;
    private String isOffline;
    private String offlineInstance;
    private String machineName;
    private String unitName;

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

    public String getIsModifyLoadBalanceStrategy() {
        return isModifyLoadBalanceStrategy;
    }

    public void setIsModifyLoadBalanceStrategy(String isModifyLoadBalanceStrategy) {
        this.isModifyLoadBalanceStrategy = isModifyLoadBalanceStrategy;
    }

    public Integer getLoadBalanceStrategy() {
        return loadBalanceStrategy;
    }

    public void setLoadBalanceStrategy(Integer loadBalanceStrategy) {
        this.loadBalanceStrategy = loadBalanceStrategy;
    }

    public Integer getMachineRoomNearbyType() {
        return machineRoomNearbyType;
    }

    public void setMachineRoomNearbyType(Integer machineRoomNearbyType) {
        this.machineRoomNearbyType = machineRoomNearbyType;
    }

    public String getMessageQueueAndBrokerName() {
        return messageQueueAndBrokerName;
    }

    public void setMessageQueueAndBrokerName(String messageQueueAndBrokerName) {
        this.messageQueueAndBrokerName = messageQueueAndBrokerName;
    }

    public Integer getVirtualNodeCnt() {
        return virtualNodeCnt;
    }

    public void setVirtualNodeCnt(Integer virtualNodeCnt) {
        this.virtualNodeCnt = virtualNodeCnt;
    }

    public String getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(String isOffline) {
        this.isOffline = isOffline;
    }

    public String getOfflineInstance() {
        return offlineInstance;
    }

    public void setOfflineInstance(String offlineInstance) {
        this.offlineInstance = offlineInstance;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
