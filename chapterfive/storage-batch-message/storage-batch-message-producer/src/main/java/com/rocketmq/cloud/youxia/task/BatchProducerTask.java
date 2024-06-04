package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.StorageBatchMessageConfig;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageBatch;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
public class BatchProducerTask {

    @Autowired
    private StorageBatchMessageConfig storageBatchMessageConfig;

    private DefaultMQProducer mqProducer;

    @Scheduled(fixedRate = 200)
    public void producerBatchMessage() throws MQClientException, MQBrokerException, InterruptedException
            , RemotingException, UnsupportedEncodingException {
        if (storageBatchMessageConfig.getOpenProducer().equals("true")) {
            String instanceName = storageBatchMessageConfig.getInstanceName() + RandomUtils.nextLong(100, 1000000000);
            if (null == mqProducer) {
                mqProducer = new DefaultMQProducer(storageBatchMessageConfig.getProducerGroup());
                //设置Name Server的IP地址
                mqProducer.setNamesrvAddr(storageBatchMessageConfig.getNamesrvAddr());
                mqProducer.setInstanceName(instanceName);
                mqProducer.setClientIP(storageBatchMessageConfig.getClientIP());
                //启动生产者
                mqProducer.start();
            }
            String topic = storageBatchMessageConfig.getTopic();
            Integer size=storageBatchMessageConfig.getBatchSize();
            List<Message> messageList=new ArrayList<>();
            for(int i=0;i<size;i++){
                String message = "this is a test message" + RandomUtils.nextLong(100, 1000000000);
                Message msg = new Message(topic, (message).getBytes(RemotingHelper.DEFAULT_CHARSET));
                messageList.add(msg);
            }
            //批量生产消息
            SendResult sendResult=mqProducer.send(messageList,3000);
            System.out.println("sendResult:"+sendResult.getSendStatus());
        }
    }
}
