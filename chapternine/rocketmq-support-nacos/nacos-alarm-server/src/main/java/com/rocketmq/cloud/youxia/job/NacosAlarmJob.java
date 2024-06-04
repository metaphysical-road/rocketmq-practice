package com.rocketmq.cloud.youxia.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.rocketmq.cloud.youxia.config.NacosAlarmConfig;
import com.rocketmq.cloud.youxia.util.DingDingUtils;
import com.rocketmq.cloud.youxia.util.HttpClientUtils;
import com.rocketmq.cloud.youxia.util.NacosAlarmMessage;
import com.rocketmq.cloud.youxia.util.RestResult;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@EnableScheduling
@Component
//@Slf4j
public class NacosAlarmJob {
    private static final String INSTANCE_NULL = "该服务下无可用实例";
    private static final String INSTANCE_BAD = "该实例不可用";
    private static final String SERVICE_BAD = "该服务不可用";
    private List<String> newServices = new ArrayList<>();
    private Map<String, List<String>> newInstanceMap = new HashMap<>();

    @Autowired
    private NacosAlarmService nacosAlarmService;

    @Autowired
    private NacosAlarmConfig nacosAlarmConfig;

    @Scheduled(fixedRate = 10000)
    public void execute() {
        if (!NacosAlarmService.ISINIT) {
            return;
        }
        List<NacosAlarmMessage> alarmMessageList = new ArrayList<>();
        compareService();
        List<String> missService = new ArrayList<>();
        try {
            if (!newServices.containsAll(NacosAlarmService.serverNameList)) {
                for (String serviceName : NacosAlarmService.serverNameList) {
                    if (!newServices.contains(serviceName)) {
                        StringBuilder instanceNameBuilder = new StringBuilder();
                        for (String instanceName :NacosAlarmService.instanceMap.get(serviceName)){
                            instanceNameBuilder.append(instanceName).append(",");
                        }
                        NacosAlarmMessage nacosAlarmMessage = new NacosAlarmMessage(
                                "becf336b-3ccf-4c6b-be93-e51c1542bece", serviceName, instanceNameBuilder.length() > 0 ?
                                instanceNameBuilder.substring(0,instanceNameBuilder.length()-1) : "", SERVICE_BAD, new Date());
                        alarmMessageList.add(nacosAlarmMessage);
                        missService.add(serviceName);
                        System.out.println("service miss,{}:"+nacosAlarmMessage.toString());
                    }
                }
            }
            for (String serviceName : NacosAlarmService.serverNameList) {
                if (missService.contains(serviceName)) {
                    continue;
                }
                JSONArray ips = getInstanceAlarm(serviceName);
                if (ips.isEmpty()) {
                    StringBuilder  instanceNameBuilder= new StringBuilder();
                    for (String instanceName :NacosAlarmService.instanceMap.get(serviceName)){
                        instanceNameBuilder.append(instanceName).append(",");
                    }
                    NacosAlarmMessage nacosAlarmMessage = new NacosAlarmMessage("becf336b-3ccf-4c6b-be93-e51c1542bece",
                            serviceName, instanceNameBuilder.length() > 0 ? instanceNameBuilder.substring(0,instanceNameBuilder.length()-1) : "",
                            INSTANCE_NULL, new Date());
                    alarmMessageList.add(nacosAlarmMessage);
                    System.out.println("Instance isEmpty,{}:"+nacosAlarmMessage.toString());
                } else {
                    for (Object ip : ips) {
                        if (((String) ip).contains("false")) {
                            NacosAlarmMessage nacosAlarmMessage = new NacosAlarmMessage(
                                    "becf336b-3ccf-4c6b-be93-e51c1542bece", serviceName, (String) ip, INSTANCE_BAD,
                                    new Date());
                            alarmMessageList.add(nacosAlarmMessage);
                            System.out.println("Instance health false,{}:"+nacosAlarmMessage.toString());
                        }
                    }
                    List<String> instances = NacosAlarmService.instanceMap.get(serviceName);
                    String[] ipList = ips.toArray(new String[0]);
                    List<String> ipSimpleList = new ArrayList<>();
                    for (String ip : ipList) {
                        if(ip.contains("true")||ip.contains("false")){
                            String ipSimple = ip.split("_")[0];
                            ipSimpleList.add(ipSimple);
                        }else{
                            ipSimpleList.add(ip);
                        }
                    }
                    for (String instance : instances) {
                        if (!ipSimpleList.contains(instance)) {
                            NacosAlarmMessage nacosAlarmMessage = new NacosAlarmMessage(
                                    "becf336b-3ccf-4c6b-be93-e51c1542bece", serviceName, instance, INSTANCE_BAD,
                                    new Date());
                            alarmMessageList.add(nacosAlarmMessage);
                            System.out.println("Instance miss,{}:"+nacosAlarmMessage.toString());
                        }
                    }
                }
            }
            removeDuplicatedAlarm(alarmMessageList);
            sendAlarm(alarmMessageList);
        } catch (Exception e) {
            System.out.println("Nacos 告警失败"+e.getMessage());
        }
    }

    List<NacosAlarmMessage> cacheAlarmMessageList = new ArrayList<>();

    private void removeDuplicatedAlarm(List<NacosAlarmMessage> alarmMessageList) {
        Iterator<NacosAlarmMessage> alarmMessageIterator = alarmMessageList.iterator();
        while (alarmMessageIterator.hasNext()) {
            NacosAlarmMessage alarmMessge = alarmMessageIterator.next();
            int num = cacheAlarmMessageList.indexOf(alarmMessge);
            if (num != -1) {
                if (alarmMessge.getTime().getTime() - cacheAlarmMessageList.get(num).getTime().getTime() < 1000 * 30) {
                    alarmMessageIterator.remove();
                    System.out.println("nacos告警发送太频繁： {}:"+alarmMessge.toString());
                } else {
                    cacheAlarmMessageList.set(num, alarmMessge);
                }
            } else {
                cacheAlarmMessageList.add(alarmMessge);
            }
        }
    }

    private JSONArray getInstanceAlarm(String serviceName) throws Exception {
        Map<String, String> map = new HashMap<>(2);
        map.put("key", "becf336b-3ccf-4c6b-be93-e51c1542bece" + "##DEFAULT_GROUP@@" + serviceName);
        RestResult restResult = HttpClientUtils
                .get(nacosAlarmConfig.getNacosIp() + nacosAlarmConfig.getNacosInstanceAlarmApi(), map);

        if (restResult.getCode() != HttpStatus.SC_OK) {
            throw new Exception("HTTP请求失败" + restResult.getCode() + " message: " + restResult.getMessage());
        }
        String message = restResult.getMessage();
        JSONObject jsonObject = JSON.parseObject(message);
        return jsonObject.getJSONArray("ips");
    }

    public void sendAlarm(List<NacosAlarmMessage> alarmMessageList) throws ApiException {
        if (alarmMessageList.isEmpty()) {
            return;
        }
        OapiRobotSendRequest markDownRequest = new OapiRobotSendRequest();
        markDownRequest.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("NACOS告警");
        StringBuffer markDownText = new StringBuffer();
        AtomicInteger successCount = new AtomicInteger();

        alarmMessageList.forEach(info -> {
            String addText =
                    "### NACOS报警 \n\n" + "### 所属平台 :" + nacosAlarmConfig.getPlatform() + "\n\n" + "### 命名空间 :" + info
                            .getNameSpace() + "\n\n" + "### 所属服务 :" + info.getServiceName() + "\n\n" + "### 服务实例 :" + info
                            .getIp() + "\n\n" + "### 告警时间 :" + DateFormatUtils.format(info.getTime(), "yyyy-MM-dd HH:mm:ss")
                            + "\n\n" + "### 告警内容 :" + info.getMessage() + "\n\n" + "---" + "\n\n";

            markDownText.append(addText);
            successCount.getAndIncrement();
        });
        markdown.setText(String.valueOf(markDownText));
        markDownRequest.setMarkdown(markdown);
        String secret = nacosAlarmConfig.getSecret();
        String webHook = nacosAlarmConfig.getWebhook();
        DingTalkClient client = DingDingUtils.getClient(secret,webHook);
        OapiRobotSendResponse response = client.execute(markDownRequest);
        System.out.println("execute:{}" + response.toString());
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent("请相关人员尽快处理报警所示异常");
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        addResponsible(alarmMessageList, at);
        request.setAt(at);
        client.execute(request);
    }

    private void addResponsible(List<NacosAlarmMessage> alarmMessageList, OapiRobotSendRequest.At at) {
        List<String> atMobiles = new ArrayList<>();
        alarmMessageList.forEach(info -> {
            atMobiles.add(nacosAlarmConfig.getPhoneNumber().get(info.getServiceName()));
        });
        if (atMobiles.contains(null)) {
            at.setIsAtAll("true");
        } else {
            at.setAtMobiles(atMobiles);
        }
    }

    public void compareService() {
        newServices = nacosAlarmService.selectServices();
        List<String> oldServices = NacosAlarmService.serverNameList;

        Map<String, List<String>> oldInstanceMap = NacosAlarmService.instanceMap;
        for (String serviceName : newServices) {
            if (!oldServices.contains(serviceName)) {
                oldServices.add(serviceName);
                List<String> instances = nacosAlarmService.selectInstances(serviceName);
                oldInstanceMap.put(serviceName, instances);
                System.out.println("新增服务：{}"+serviceName);
                newInstanceMap.put(serviceName, instances);
            } else {
                List<String> oldInstances = oldInstanceMap.get(serviceName);
                List<String> newInstances = nacosAlarmService.selectInstances(serviceName);
                if (!oldInstances.containsAll(newInstances)) {
                    for (String instance : newInstances) {
                        if (!oldInstances.contains(instance)) {
                            oldInstances.add(instance);
                            System.out.println("新增实例 服务：{},实例：{}"+serviceName+instance);
                        }
                    }
                }
                newInstanceMap.put(serviceName, newInstances);
            }
        }
    }
}
