package com.rocketmq.cloud.youxia;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
//@Data
@ConfigurationProperties(prefix = "rocketmq.youxia.config")
public class DynamicConfig {
    //是否扩容Consumer
    private String dilatation;
    //是否缩容Consumer
    private String shrinkage;
    private String nameServerAddress;
    //基础Consumer能力，比如会初始化三个生产者实例，生产消息
    private String mappingRelation;
    //需要扩容的Consumer能力
    private String mappingRelationExt;
    //需要缩容的Consumer能力
    private String mappingRelationReduce;
    //需要进行弹性的Topic主题名称
    private String topicName;

    public String getDilatation() {
        return dilatation;
    }

    public void setDilatation(String dilatation) {
        this.dilatation = dilatation;
    }

    public String getShrinkage() {
        return shrinkage;
    }

    public void setShrinkage(String shrinkage) {
        this.shrinkage = shrinkage;
    }

    public String getNameServerAddress() {
        return nameServerAddress;
    }

    public void setNameServerAddress(String nameServerAddress) {
        this.nameServerAddress = nameServerAddress;
    }

    public String getMappingRelation() {
        return mappingRelation;
    }

    public void setMappingRelation(String mappingRelation) {
        this.mappingRelation = mappingRelation;
    }

    public String getMappingRelationExt() {
        return mappingRelationExt;
    }

    public void setMappingRelationExt(String mappingRelationExt) {
        this.mappingRelationExt = mappingRelationExt;
    }

    public String getMappingRelationReduce() {
        return mappingRelationReduce;
    }

    public void setMappingRelationReduce(String mappingRelationReduce) {
        this.mappingRelationReduce = mappingRelationReduce;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
