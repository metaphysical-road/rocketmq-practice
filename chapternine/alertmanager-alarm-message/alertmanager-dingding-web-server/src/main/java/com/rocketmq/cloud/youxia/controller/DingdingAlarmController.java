package com.rocketmq.cloud.youxia.controller;

import com.alibaba.fastjson.JSON;
import com.rocketmq.cloud.youxia.service.AlarmService;
import com.rocketmq.cloud.youxia.util.JsonObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping(value = "/dingtalk")
public class DingdingAlarmController {

    @Autowired
    private AlarmService alarmService;

    @PostMapping(value = "/sendAlarmMessage")
    public void sendAlarmMessage(@RequestBody Map<String,Object> alertInfo) {
        String jsonData=JSON.toJSONString(alertInfo);
        Map<String,Object> mapData=(Map<String, Object>) JSON.parse(jsonData);
        List<Map<String, String>> alarmList = new CopyOnWriteArrayList<>();
        if (null != alertInfo && alertInfo.size() > 0) {
            alarmList = JsonObjectUtil.parse(mapData);
        }
        if (CollectionUtils.isNotEmpty(alarmList)) {
            for (Map<String, String> item : alarmList) {
                //已经解决的告警就不发送了
                String alarmStatus=item.get("status");
                if(alarmStatus.equals("firing")){
                    alarmService.send(item);
                }
            }
        }
    }
}
