package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.TransactionProducerConfig;
import com.rocketmq.cloud.youxia.listener.LocalTransactionListener;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.LongAdder;

@EnableScheduling
@Component
public class TransactionProducerTask {

    Map<String,TransactionMQProducer> producerMap=new ConcurrentHashMap<>();
    List<TransactionMQProducer> producerList=new CopyOnWriteArrayList<>();
    @Autowired
    private TransactionProducerConfig transactionProducerConfig;
    @Autowired
    private LocalTransactionListener localTransactionListener;
    private LongAdder longAdder=new LongAdder();

    @Scheduled(fixedRate = 200)
    public void producerTransactionMessage()
            throws MQClientException,UnsupportedEncodingException {
        while (longAdder.intValue() < transactionProducerConfig.getProducerNum()) {
            final String producerGroup = "testTransactionMessage";
            TransactionMQProducer transactionMQProducer = new TransactionMQProducer(producerGroup);
            transactionMQProducer.setTransactionListener(localTransactionListener);
            String instanceName = "testTransactionMessage" + RandomUtils.nextInt(1000, 10000000);
            while (producerMap.containsKey(instanceName)) {
                instanceName = "testTransactionMessage" + RandomUtils.nextInt(1000, 10000000);
            }
            transactionMQProducer.setInstanceName("testTransactionMessage" + RandomUtils.nextInt(1000, 10000000));
            transactionMQProducer.setClientIP("127.0.0.1:787" + RandomUtils.nextInt(0, 10));
            transactionMQProducer.setNamesrvAddr("127.0.0.1:9876");
            transactionMQProducer.start();
            producerMap.put(transactionMQProducer.getInstanceName(), transactionMQProducer);
            producerList.add(transactionMQProducer);
            longAdder.increment();
        }
        String topic = "testTransactionMessage";
        String message = "testTransactionMessage" + RandomUtils.nextLong(100, 1000000000);
        Message transactionMessage = new Message(topic, (message).getBytes(RemotingHelper.DEFAULT_CHARSET));
        Integer index = RandomUtils.nextInt(0, producerList.size());
        TransactionMQProducer transactionMQProducer = producerList.get(index);
        transactionMQProducer.sendMessageInTransaction(transactionMessage, "事务消息");
    }
}
