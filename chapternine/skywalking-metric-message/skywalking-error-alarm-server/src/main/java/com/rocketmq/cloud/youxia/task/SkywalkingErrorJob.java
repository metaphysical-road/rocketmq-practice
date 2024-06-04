package com.rocketmq.cloud.youxia.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.rocketmq.cloud.youxia.config.SkywalkingErrorTraceConfig;
import com.rocketmq.cloud.youxia.dto.SkywalkingAlarmMessage;
import com.rocketmq.cloud.youxia.service.SkywalkingErrorAlarmService;
import com.rocketmq.cloud.youxia.util.DingDingUtils;
import com.rocketmq.cloud.youxia.util.HttpClientUtils;
import com.rocketmq.cloud.youxia.util.RestResult;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
//@Slf4j
@Component
public class SkywalkingErrorJob {

    @Autowired
    private SkywalkingErrorAlarmService skywalkingErrorAlarmService;

    @Autowired
    private SkywalkingErrorTraceConfig skywalkingAlarmConfig;

    @Scheduled(fixedRate = 1000)
    public void execute() {
        Date date = new Date();
        String nowTime = DateFormatUtils.format(date.getTime(), "yyyy-MM-dd HHmm");
        String oldTime = DateFormatUtils
                .format(date.getTime() - Long.parseLong(skywalkingAlarmConfig.getTime()), "yyyy-MM-dd HHmm");
        String paramJson = getParamJson(nowTime, oldTime);
        List<String> errorKeyList;
        try {
            RestResult post = HttpClientUtils.post(skywalkingAlarmConfig.getReqApi(), paramJson);
            HttpClientUtils.checkHttpResult(post);
            errorKeyList = getErrorKeyList(post.getMessage());
            List<SkywalkingAlarmMessage> skywalkingAlarmMessageList = new ArrayList<>();
            for (String errorKey : errorKeyList) {
                String message = reqErrorData(errorKey);
                skywalkingErrorAlarmService.handleMessage(skywalkingAlarmMessageList, message);
            }
            whiteList(skywalkingAlarmMessageList);
            removeDuplicatedAlarm(skywalkingAlarmMessageList);
            sendAlarm(skywalkingAlarmMessageList);
        } catch (Exception e) {
            System.out.println("skywalking 告警失败:"+e.getMessage());
        }
    }

    private String reqErrorData(String errorKey) throws Exception {
        JSONObject rootJsonObject = new JSONObject();
        rootJsonObject.put("query",
                "query queryTrace($traceId: ID!) {\n  trace: queryTrace(traceId: $traceId) {\n    spans {\n      traceId\n      segmentId\n      spanId\n      parentSpanId\n      refs {\n        traceId\n        parentSegmentId\n        parentSpanId\n        type\n      }\n      serviceCode\n      startTime\n      endTime\n      endpointName\n      type\n      peer\n      component\n      isError\n      layer\n      tags {\n        key\n        value\n      }\n      logs {\n        time\n        data {\n          key\n          value\n        }\n      }\n    }\n  }\n  }");
        JSONObject variablesJsonObject = new JSONObject();
        rootJsonObject.put("variables", variablesJsonObject);
        variablesJsonObject.put("traceId", errorKey);
        String alarmJson = rootJsonObject.toJSONString();
        RestResult alarmResult = HttpClientUtils.post(skywalkingAlarmConfig.getReqApi(), alarmJson);
        HttpClientUtils.checkHttpResult(alarmResult);
        return alarmResult.getMessage();
    }

    private List<String> getErrorKeyList(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        JSONObject data = null;
        if (jsonObject != null) {
            data = (JSONObject) jsonObject.get("data");
        }
        JSONObject traces = null;
        if (data != null) {
            traces = (JSONObject) data.get("traces");
        }
        JSONArray dataList = new JSONArray();
        if (traces != null) {
            dataList = traces.getJSONArray("data");
        }
        List<String> errorKeyList = new ArrayList<>();
        for (Object errorKey : dataList) {
            JSONObject errorKeyJson = (JSONObject) errorKey;
            JSONArray traceIds = errorKeyJson.getJSONArray("traceIds");
            if (traceIds != null && !traceIds.isEmpty()) {
                //目前只取了第一个，后续
                errorKeyList.add((String) traceIds.get(0));
            }
        }
        return errorKeyList;
    }

    private String getParamJson(String nowTime, String oldTime) {
        JSONObject rootJsonObject = new JSONObject();
        rootJsonObject.put("query",
                "query queryTraces($condition: TraceQueryCondition) {\n  traces: queryBasicTraces(condition: $condition) {\n    data: traces {\n      key: segmentId\n      endpointNames\n      duration\n      start\n      isError\n      traceIds\n    }\n    total\n  }}");
        JSONObject variablesJsonObject = new JSONObject();
        rootJsonObject.put("variables", variablesJsonObject);
        JSONObject conditionJsonObject = new JSONObject();
        variablesJsonObject.put("condition", conditionJsonObject);
        JSONObject queryDurationJsonObject = new JSONObject();
        conditionJsonObject.put("queryDuration", queryDurationJsonObject);
        queryDurationJsonObject.put("start", oldTime);
        queryDurationJsonObject.put("end", nowTime);
        queryDurationJsonObject.put("step", "MINUTE");
        conditionJsonObject.put("traceState", "ERROR");
        JSONObject pagingJsonObject = new JSONObject();
        conditionJsonObject.put("paging", pagingJsonObject);
        pagingJsonObject.put("pageNum", 1);
        pagingJsonObject.put("pageSize", 10000);
        pagingJsonObject.put("needTotal", true);
        conditionJsonObject.put("queryOrder", "BY_DURATION");
        return rootJsonObject.toJSONString();
    }
    Map<String, Long> cacheAlarmMessageList = new ConcurrentHashMap<>();

    private void removeDuplicatedAlarm(List<SkywalkingAlarmMessage> alarmMessageList) {
        Iterator<SkywalkingAlarmMessage> alarmMessageIterator = alarmMessageList.iterator();
        while (alarmMessageIterator.hasNext()) {
            SkywalkingAlarmMessage alarmMessge = alarmMessageIterator.next();
            Long aLong = cacheAlarmMessageList.get(alarmMessge.getTraceId());
            if (aLong != null && System.currentTimeMillis() - aLong < 1000 * 30) {
                alarmMessageIterator.remove();
                System.out.println("skywalking 告警发送太频繁： {}:"+alarmMessge.toString());
            } else {
                cacheAlarmMessageList.put(alarmMessge.getTraceId(), System.currentTimeMillis());
            }
        }
    }

    private void whiteList(List<SkywalkingAlarmMessage> alarmMessageList) {
        alarmMessageList.removeIf(alarmMessge -> skywalkingAlarmConfig.isWhiteList(alarmMessge));
    }

    public void sendAlarm(List<SkywalkingAlarmMessage> alarmMessageList) throws ApiException {
        if (alarmMessageList.isEmpty()) {
            return;
        }
        OapiRobotSendRequest markDownRequest = new OapiRobotSendRequest();
        String secret = skywalkingAlarmConfig.getSksecret();
        String webHook = skywalkingAlarmConfig.getSkwebhook();
        DingTalkClient client = DingDingUtils.getClient(secret,webHook);
        markDownRequest.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("SKYWALKING ERROR告警");
        StringBuffer markDownText = new StringBuffer();
        alarmMessageList.forEach(info -> {
            String addText =
                    "### Skywalking Error报警 \n\n" +
                            "### 所属平台 :" + "middleware" + "\n\n" +
                            "#### 服务 :" + info.getServiceCode() + "\n\n" +
                            "#### TraceId :" + info.getTraceId() + "\n\n" +
                            "#### 端点 :" + info.getEndpointName() + "\n\n" +
                            "#### 组件 :" + info.getComponent() + "\n\n" +
                            "#### Peer :" + info.getPeer() + "\n\n" +
                            "#### 时间 :" + DateFormatUtils.format(Long.parseLong(info.getEndTime()), "yyyy-MM-dd HH:mm:ss") + "\n\n";

            StringBuilder sb = new StringBuilder(addText);
            Map<String, String> tagsMap = info.getTagsMap();
            Set<Map.Entry<String, String>> tagsEntries = tagsMap.entrySet();
            for (Map.Entry<String, String> entry : tagsEntries) {
                sb.append("#### " + entry.getKey() + " :" + entry.getValue() + "\n\n");
            }
            Map<String, String> logsMap = info.getLogsMap();
            Set<Map.Entry<String, String>> logsEntries = logsMap.entrySet();
            for (Map.Entry<String, String> entry : logsEntries) {
                sb.append("#### " + entry.getKey() + " :" + entry.getValue() + "\n\n");
            }
            sb.append("---" + "\n\n");
            if (markDownText.toString().getBytes().length + sb.toString().getBytes().length > 20000 && StringUtils
                    .isNotEmpty(String.valueOf(markDownText))) {

                try {
                    sendErrorMessage(markDownRequest, markdown, markDownText, client);
                } catch (ApiException e) {
                    System.out.println("skywalking 告警推送钉钉失败:"+e.getMessage());
                }
                markDownText.delete(0, markDownText.length());
            }
            markDownText.append(sb);
        });
        sendErrorMessage(markDownRequest, markdown, markDownText, client);
        sendATProjectLeader(alarmMessageList, client);
    }

    private void sendErrorMessage(OapiRobotSendRequest markDownRequest, OapiRobotSendRequest.Markdown markdown,
                                  StringBuffer markDownText, DingTalkClient client) throws ApiException {
        System.out.println("skywalking 告警信息{}"+markDownText.toString());
        markdown.setText(String.valueOf(markDownText));
        markDownRequest.setMarkdown(markdown);
        OapiRobotSendResponse response = client.execute(markDownRequest);
        if (response != null && StringUtils.isNotBlank(response.getErrmsg())) {
            System.out.println("发送执行结果 execute:{}" + response.getErrmsg());
        }
    }

    private void sendATProjectLeader(List<SkywalkingAlarmMessage> alarmMessageList, DingTalkClient client)
            throws ApiException {
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

    private void addResponsible(List<SkywalkingAlarmMessage> alarmMessageList, OapiRobotSendRequest.At at) {
        List<String> atMobiles = new ArrayList<>();
        alarmMessageList.forEach(info -> {
            //根据服务名找mobile
            atMobiles.add(skywalkingAlarmConfig.getPhoneNumber().get(info.getServiceCode()));
        });
        if (atMobiles.contains(null)) {
            at.setIsAtAll("true");
        } else {
            at.setAtMobiles(atMobiles);
        }
    }
}
