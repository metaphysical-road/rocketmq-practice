package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.AgencyProducerConfig;
import com.rocketmq.cloud.youxia.listener.AgencyTransactionListener;
import com.rocketmq.cloud.youxia.message.MessageEntity;
import com.rocketmq.cloud.youxia.service.DistributedService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
public class AgencyTransactionProducerTask {

    @Resource
    private AgencyProducerConfig producerConfig;

    @Autowired
    private AgencyTransactionListener agencyTransactionListener;

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private DistributedService distributedService;

    private List<TransactionMQProducer> defaultMQProducerList =new ArrayList<>();

    @Scheduled(fixedRate = 1000)
    public void producerMessage() {
        if (defaultMQProducerList.size() == 0) {
            TransactionMQProducer defaultMQProducer = new TransactionMQProducer(producerConfig.getProducerGroupName());
            defaultMQProducer.setNamesrvAddr(producerConfig.getNamesrvAddr());
            defaultMQProducer.setInstanceName(producerConfig.getTransactionInstanceName());
            defaultMQProducer.setClientIP(producerConfig.getClientIp());
            defaultMQProducer.setDefaultTopicQueueNums(producerConfig.getTopicQueueNums());
            defaultMQProducer.setTransactionListener(agencyTransactionListener);
            try {
                defaultMQProducer.start();
            } catch (MQClientException e) {
                System.out.println(e.getMessage());
            }
            defaultMQProducerList.add(defaultMQProducer);
        }
        try {
            //生产消息
            String topic = producerConfig.getTransactionTopic();
            //构造消息体
            Message msg = new Message(topic,
                    ("this is a test message" + RandomUtils.nextLong(1, 20000000)).
                            getBytes(RemotingHelper.DEFAULT_CHARSET));
            MessageEntity messageEntity = new MessageEntity();
            Long uk=distributedService.nextId();
            messageEntity.setUk(uk);
            msg.getProperties().put("uk",uk+"");
            if (defaultMQProducerList.size() > 0) {
                //生产事务消息
                SendResult result = defaultMQProducerList.get(0).sendMessageInTransaction(msg, messageEntity);
                if (result.getSendStatus().equals(SendStatus.SEND_OK)) {
                    System.out.println("生产延迟消息成功，消息ID为:" + result.getMsgId());
                }
            }
        } catch (UnsupportedEncodingException e1) {
            System.out.println(e1.getMessage());
        } catch (MQClientException e2) {
            System.out.println(e2.getMessage());
        }
    }
}
