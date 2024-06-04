package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.DynamicStorageEngineConfig;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;

@Component
@EnableScheduling
public class ProducerTask {
    private DefaultMQProducer mqProducer;

    @Autowired
    private DynamicStorageEngineConfig dynamicStorageEngineConfig;

    @Scheduled(fixedRate = 200)
    public void producerMessage() throws MQClientException, MQBrokerException, InterruptedException
            , RemotingException, UnsupportedEncodingException {
        if (dynamicStorageEngineConfig.getOpenProducer().equals("true")) {
            String instanceName = dynamicStorageEngineConfig.getInstanceName() + RandomUtils.nextLong(100, 1000000000);
            if (null == mqProducer) {
                mqProducer = new DefaultMQProducer(dynamicStorageEngineConfig.getProducerGroup());
                //设置Name Server的IP地址
                mqProducer.setNamesrvAddr(dynamicStorageEngineConfig.getNamesrvAddr());
                mqProducer.setInstanceName(instanceName);
                mqProducer.setClientIP(dynamicStorageEngineConfig.getClientIP());
                //启动生产者
                mqProducer.start();
            }
            String topic = dynamicStorageEngineConfig.getTopic();
            String message = "this is a test message" + RandomUtils.nextLong(100, 1000000000);
            Message msg = new Message(topic, (message).getBytes(RemotingHelper.DEFAULT_CHARSET));
            mqProducer.send(msg);
        }
    }
}
