package com.rocketmq.cloud.youxia.service;

import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.rocketmq.cloud.youxia.config.DingdingAlarmConfig;
import com.rocketmq.cloud.youxia.util.DingDingUtils;
import com.taobao.api.ApiException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class AlarmService {

    @Autowired
    private DingdingAlarmConfig dingdingAlarmConfig;

    public void send(Map<String,String> jsonAlarmMap){
        OapiRobotSendRequest markDownRequest = new OapiRobotSendRequest();
        String secret =dingdingAlarmConfig.getSecret();
        String webHook = dingdingAlarmConfig.getWebHookUrl();
        String filterField=dingdingAlarmConfig.getFilterField();
        Long sleepTime=dingdingAlarmConfig.getSleepTime();
        String[] filterFieldArray=null;
        if(StringUtils.isNotEmpty(filterField)){
            if(filterField.contains(",")){
                filterFieldArray=filterField.split(",");
            }else{
                filterFieldArray=new String[]{filterField};
            }
        }
        List<String> filterFieldList=new ArrayList<>();
        if(null!=filterFieldArray){
            filterFieldList=Arrays.asList(filterFieldArray);
        }
        DingTalkClient client = DingDingUtils.getClient(secret, webHook);
        markDownRequest.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("Alert Manager RocketMQ Exporter监控告警");
        StringBuffer markDownText = new StringBuffer();
        markDownText.append("Alert Manager RocketMQ Exporter监控告警 \n\n");
        if (null!=jsonAlarmMap&&jsonAlarmMap.size()>0) {
            Iterator<String> iterator=jsonAlarmMap.keySet().iterator();
            while (iterator.hasNext()){
                String key=iterator.next();
                String value=jsonAlarmMap.get(key);
                if(CollectionUtils.isNotEmpty(filterFieldList)){
                    if(!filterFieldList.contains(key)){
                        markDownText.append("【"+key+"】"+"-->"+value+"\n\n");
                    }
                }else{
                    markDownText.append("【"+key+"】"+"-->"+value+"\n\n");
                }
            }
        }
        String mt = String.valueOf(markDownText);
        try {
            sendAlarmMessage(markDownRequest, markdown, mt, client);
        } catch (ApiException e) {
            System.out.println(e.getCause().getMessage());
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent("请相关业务负责人尽快处理告警！！");
        request.setText(text);
        List<String> atList=new ArrayList<>();
        atList.add("15857196417");
        if (CollectionUtils.isNotEmpty(atList)) {
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setAtMobiles(atList);
            request.setAt(at);
        }
        try {
            OapiRobotSendResponse testResponse = client.execute(request);
            System.out.println("通知负责人:" + testResponse.getErrmsg());
            Thread.sleep(sleepTime);
        }catch (ApiException e){
        }catch (InterruptedException e1){
        }
    }

    private void sendAlarmMessage(OapiRobotSendRequest markDownRequest, OapiRobotSendRequest.Markdown markdown,
                                  String markDownText, DingTalkClient client) throws ApiException {
        markdown.setText(markDownText);
        markDownRequest.setMarkdown(markdown);
        OapiRobotSendResponse response = client.execute(markDownRequest);
        if (response != null && StringUtils.isNotBlank(response.getErrmsg())) {
            System.out.println("执行结果:" + response.getErrmsg());
        }
    }
}
