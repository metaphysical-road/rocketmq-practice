package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.FaultToleranceConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
public class ProducerTask {
    private List<DefaultMQProducer> defaultMQProducerList = new ArrayList<>();

    private Map<String, DefaultMQProducer> defaultMQProducerMap = new ConcurrentHashMap<>();

    @Autowired
    private FaultToleranceConfig faultToleranceConfig;

    @Scheduled(fixedRate = 1000)
    public void producerMessage() {
        String isSingleInstance = faultToleranceConfig.getIsSingleInstance();
        if (isSingleInstance.equals("false")) {
            String instanceName = faultToleranceConfig.getInstanceName();
            String[] instanceNameList = instanceName.split(",");
            if (instanceNameList.length > 0) {
                for (String s : instanceNameList) {
                    if (!defaultMQProducerMap.containsKey(s)) {
                        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(faultToleranceConfig.getProducerGroup());
                        defaultMQProducer.setNamesrvAddr(faultToleranceConfig.getNamesrvAddr());
                        defaultMQProducer.setInstanceName(s);
                        defaultMQProducer.setClientIP(faultToleranceConfig.getClientIp());
                        try {
                            defaultMQProducer.start();
                            defaultMQProducerList.add(defaultMQProducer);
                            defaultMQProducerMap.put(s, defaultMQProducer);
                        } catch (MQClientException e) {
                            System.out.println(e.getCause().getMessage());
                        }
                    }
                }
            }
        }else {
            String singleInstanceName = faultToleranceConfig.getSingleInstanceName();
            String multiProducerGroup = faultToleranceConfig.getMultiProducerGroup();
            String[] multiProducerGroupArray = multiProducerGroup.split(",");
            for (String groupName : multiProducerGroupArray) {
                if (!defaultMQProducerMap.containsKey(groupName)) {
                    DefaultMQProducer defaultMQProducer = new DefaultMQProducer(groupName);
                    defaultMQProducer.setNamesrvAddr(faultToleranceConfig.getNamesrvAddr());
                    defaultMQProducer.setInstanceName(singleInstanceName);
                    defaultMQProducer.setClientIP(faultToleranceConfig.getClientIp());
                    try {
                        defaultMQProducer.start();
                        defaultMQProducerList.add(defaultMQProducer);
                        defaultMQProducerMap.put(groupName, defaultMQProducer);
                    } catch (MQClientException e) {
                        System.out.println(e.getCause().getMessage());
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(defaultMQProducerList)) {
            DefaultMQProducer random = defaultMQProducerList.get(RandomUtils.nextInt(0, defaultMQProducerList.size()));
            if(faultToleranceConfig.getSendLatencyFaultEnable().equals("true")){
                random.setSendLatencyFaultEnable(true);
            }
            try {
                //生产消息
                String topic = faultToleranceConfig.getTopic();
                //构造消息体
                Message msg = new Message(topic,
                        ("this is a test message" + RandomUtils.nextLong(1, 20000000)).
                                getBytes(RemotingHelper.DEFAULT_CHARSET));
                //开启延迟
                msg.putUserProperty("delay",faultToleranceConfig.getDelay());
                //开启延迟时间
                msg.putUserProperty("delayTime",faultToleranceConfig.getDelayTime());
                SendResult result = random.send(msg);
                if (result.getSendStatus().equals(SendStatus.SEND_OK)) {
                    System.out.println("生产消息成功，消息ID为:" + result.getMsgId());
                }
            } catch (UnsupportedEncodingException e1) {
                System.out.println(e1.getMessage());
            } catch (MQClientException e2) {
                System.out.println(e2.getMessage());
            } catch (RemotingException e3) {
                System.out.println(e3.getMessage());
            } catch (MQBrokerException e4) {
                System.out.println(e4.getMessage());
            } catch (InterruptedException e5) {
                System.out.println(e5.getMessage());
            }
        }
    }
}
