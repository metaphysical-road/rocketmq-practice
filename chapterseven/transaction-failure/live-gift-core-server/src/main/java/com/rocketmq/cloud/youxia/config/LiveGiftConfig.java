package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class LiveGiftConfig {
    private Integer giftNum;
    private Integer producerNum;
    private String openInsertData;
    private String openFaultInsertion;
    private Integer simulateNum;
    private Integer eliminateNum;
    private String openEliminate;
    private String eliminateProducerClientId;
    private String instanceName;
    private String clientIp;

    public Integer getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(Integer giftNum) {
        this.giftNum = giftNum;
    }

    public Integer getProducerNum() {
        return producerNum;
    }

    public void setProducerNum(Integer producerNum) {
        this.producerNum = producerNum;
    }

    public String getOpenInsertData() {
        return openInsertData;
    }

    public void setOpenInsertData(String openInsertData) {
        this.openInsertData = openInsertData;
    }

    public String getOpenFaultInsertion() {
        return openFaultInsertion;
    }

    public void setOpenFaultInsertion(String openFaultInsertion) {
        this.openFaultInsertion = openFaultInsertion;
    }

    public Integer getSimulateNum() {
        return simulateNum;
    }

    public void setSimulateNum(Integer simulateNum) {
        this.simulateNum = simulateNum;
    }

    public Integer getEliminateNum() {
        return eliminateNum;
    }

    public void setEliminateNum(Integer eliminateNum) {
        this.eliminateNum = eliminateNum;
    }

    public String getOpenEliminate() {
        return openEliminate;
    }

    public void setOpenEliminate(String openEliminate) {
        this.openEliminate = openEliminate;
    }

    public String getEliminateProducerClientId() {
        return eliminateProducerClientId;
    }

    public void setEliminateProducerClientId(String eliminateProducerClientId) {
        this.eliminateProducerClientId = eliminateProducerClientId;
    }

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
}
