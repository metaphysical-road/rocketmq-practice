package com.rocketmq.cloud.youxia.config;

import com.alibaba.fastjson.JSONObject;
import com.rocketmq.cloud.youxia.dto.SkywalkingAlarmMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author huxian
 * 告警服务查询Skywalking的链路错误信息的配置信息
 */
@Component
//@Data
@RefreshScope
//@Slf4j
@ConfigurationProperties(prefix = "query.skywalking.error")
public class SkywalkingErrorTraceConfig {
    private List<Map<String, String>> skywalkingWhiteLists;
    private String time;
    private String reqApi;
    private String sksecret;
    private String skwebhook;
    private Map<String, String> phoneNumber;
    public boolean isWhiteList(SkywalkingAlarmMessage alarmMessage){
        //alarmMessage  - map
        Map<String,Object> alarmMessageMap = JSONObject.parseObject(JSONObject.toJSONString(alarmMessage), Map.class);
        JSONObject logJson = (JSONObject) alarmMessageMap.get("logsMap");
        Map<String,Object> logMap = JSONObject.parseObject(logJson.toJSONString(), Map.class);
        alarmMessageMap.remove("logsMap");
        alarmMessageMap.putAll(logMap);

        JSONObject tagsJson = (JSONObject) alarmMessageMap.get("tagsMap");
        Map<String,Object> tagMap = JSONObject.parseObject(tagsJson.toJSONString(), Map.class);
        alarmMessageMap.remove("tagsMap");
        alarmMessageMap.putAll(tagMap);
        for (Map<String, String> skywalkingWhiteMap : skywalkingWhiteLists){
            if (equals(alarmMessageMap,skywalkingWhiteMap)){
                System.out.println("skywalking whiteList filter{}:"+alarmMessage.toString());
                return true;
            }
        }
        return false;
    }

    public boolean equals(Map<String,Object> alarmMessage,Map<String,String> whiteList){
        Set<Map.Entry<String, String>> entries = whiteList.entrySet();
        for(Map.Entry<String, String> entry: entries){
            if(!alarmMessage.containsKey(entry.getKey()) || !alarmMessage.get(entry.getKey()).equals(entry.getValue())){
                return false;
            }
        }
        return true;
    }

    public List<Map<String, String>> getSkywalkingWhiteLists() {
        return skywalkingWhiteLists;
    }

    public void setSkywalkingWhiteLists(List<Map<String, String>> skywalkingWhiteLists) {
        this.skywalkingWhiteLists = skywalkingWhiteLists;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReqApi() {
        return reqApi;
    }

    public void setReqApi(String reqApi) {
        this.reqApi = reqApi;
    }

    public String getSksecret() {
        return sksecret;
    }

    public void setSksecret(String sksecret) {
        this.sksecret = sksecret;
    }

    public String getSkwebhook() {
        return skwebhook;
    }

    public void setSkwebhook(String skwebhook) {
        this.skwebhook = skwebhook;
    }

    public Map<String, String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Map<String, String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
