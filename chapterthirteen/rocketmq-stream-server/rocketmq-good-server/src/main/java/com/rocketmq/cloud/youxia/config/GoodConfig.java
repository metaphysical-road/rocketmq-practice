package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class GoodConfig {
    private String producerGroup;
    private String clientIp;
    private String namesrvAddr;
    private String streamJobId;
    private String sourceTopicName;
    private String sinkTopicName;
    private String instanceName;
    private String isOpenStream;
    private String isOpenPackageData;

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

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getStreamJobId() {
        return streamJobId;
    }

    public void setStreamJobId(String streamJobId) {
        this.streamJobId = streamJobId;
    }

    public String getSourceTopicName() {
        return sourceTopicName;
    }

    public void setSourceTopicName(String sourceTopicName) {
        this.sourceTopicName = sourceTopicName;
    }

    public String getSinkTopicName() {
        return sinkTopicName;
    }

    public void setSinkTopicName(String sinkTopicName) {
        this.sinkTopicName = sinkTopicName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getIsOpenStream() {
        return isOpenStream;
    }

    public void setIsOpenStream(String isOpenStream) {
        this.isOpenStream = isOpenStream;
    }

    public String getIsOpenPackageData() {
        return isOpenPackageData;
    }

    public void setIsOpenPackageData(String isOpenPackageData) {
        this.isOpenPackageData = isOpenPackageData;
    }
}
