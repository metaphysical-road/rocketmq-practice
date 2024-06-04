package com.rocketmq.cloud.youxia.util;

import com.rocketmq.cloud.youxia.config.GoodConfig;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

public class ProducerUtil {

    private DefaultMQProducer defaultMQProducer;

    private void ProducerUtil(){
    }

    private static class InstanceHodler{
        private static final ProducerUtil instance=new ProducerUtil();
    }

    public static final ProducerUtil getInstance(){
        return InstanceHodler.instance;
    }

    public DefaultMQProducer getDefaultMQProducer(GoodConfig goodConfig) {
        final String producerGroup = goodConfig.getProducerGroup();
        if (null == defaultMQProducer) {
            defaultMQProducer = new DefaultMQProducer(producerGroup);
            String instanceName = goodConfig.getInstanceName();
            String clientIp = goodConfig.getClientIp();
            defaultMQProducer.setInstanceName(instanceName);
            defaultMQProducer.setClientIP(clientIp);
            defaultMQProducer.setNamesrvAddr(goodConfig.getNamesrvAddr());
            try {
                defaultMQProducer.start();
            } catch (MQClientException e) {
                System.out.println(e.getMessage());
            }
        }
        return defaultMQProducer;
    }
}
