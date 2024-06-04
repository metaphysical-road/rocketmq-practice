package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.UseRocketmqThreadConfig;
import com.rocketmq.cloud.youxia.thread.ProducerThread;
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
    private UseRocketmqThreadConfig useRocketmqThreadConfig;

    @Scheduled(fixedRate = 1000)
    public void producerMessage() {

        String instanceName = useRocketmqThreadConfig.getInstanceName();
        String[] instanceNameList = instanceName.split(",");
        if (instanceNameList.length > 0) {
            for (String s : instanceNameList) {
                if (!defaultMQProducerMap.containsKey(s)) {
                    DefaultMQProducer defaultMQProducer = new DefaultMQProducer(useRocketmqThreadConfig.getProducerGroup());
                    defaultMQProducer.setNamesrvAddr(useRocketmqThreadConfig.getNamesrvAddr());
                    defaultMQProducer.setInstanceName(s);
                    defaultMQProducer.setClientIP(useRocketmqThreadConfig.getClientIp());
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

        if(useRocketmqThreadConfig.getIsUseThread().equals("true")){
            for (DefaultMQProducer item : defaultMQProducerList) {
                ProducerThread producerThread = new ProducerThread(item, useRocketmqThreadConfig);
                producerThread.start();
            }
        }else{
            for (DefaultMQProducer item : defaultMQProducerList) {
                sendMessgae(item);
            }
        }
    }

    private void sendMessgae(DefaultMQProducer random) {
        try {
            //生产消息
            String topic = useRocketmqThreadConfig.getTopic();
            //构造消息体
            Message msg = new Message(topic,
                    ("this is a test message" + RandomUtils.nextLong(1, 20000000)).
                            getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = random.send(msg);
            if (result.getSendStatus().equals(SendStatus.SEND_OK)) {
                System.out.println("生产消息成功，消息ID为:" + result.getMsgId());
            }
        } catch (UnsupportedEncodingException e1) {
            System.out.println(e1.getCause().getMessage());
        } catch (MQClientException e2) {
            System.out.println(e2.getCause().getMessage());
        } catch (RemotingException e3) {
            System.out.println(e3.getCause().getMessage());
        } catch (MQBrokerException e4) {
            System.out.println(e4.getMessage());
        } catch (InterruptedException e5) {
            System.out.println(e5.getCause().getMessage());
        }
    }
}
