package com.rocketmq.cloud.youxia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
@Configuration
@ConfigurationProperties(prefix = "query.nacos")
//@Data
@RefreshScope
public class NacosAlarmConfig {
    private String platform;
    private String nacosIp;
    private String nacosInstanceAlarmApi;
    private String nacosServiceListApi;
    private String nacosInstanceListApi;
    private String nacosNameSpaceId;
    private String secret;
    private String webhook;
    private Map<String, String> phoneNumber;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getNacosIp() {
        return nacosIp;
    }

    public void setNacosIp(String nacosIp) {
        this.nacosIp = nacosIp;
    }

    public String getNacosInstanceAlarmApi() {
        return nacosInstanceAlarmApi;
    }

    public void setNacosInstanceAlarmApi(String nacosInstanceAlarmApi) {
        this.nacosInstanceAlarmApi = nacosInstanceAlarmApi;
    }

    public String getNacosServiceListApi() {
        return nacosServiceListApi;
    }

    public void setNacosServiceListApi(String nacosServiceListApi) {
        this.nacosServiceListApi = nacosServiceListApi;
    }

    public String getNacosInstanceListApi() {
        return nacosInstanceListApi;
    }

    public void setNacosInstanceListApi(String nacosInstanceListApi) {
        this.nacosInstanceListApi = nacosInstanceListApi;
    }

    public String getNacosNameSpaceId() {
        return nacosNameSpaceId;
    }

    public void setNacosNameSpaceId(String nacosNameSpaceId) {
        this.nacosNameSpaceId = nacosNameSpaceId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public Map<String, String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Map<String, String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
