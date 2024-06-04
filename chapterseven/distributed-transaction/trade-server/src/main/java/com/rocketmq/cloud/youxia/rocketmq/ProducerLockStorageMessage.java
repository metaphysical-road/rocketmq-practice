package com.rocketmq.cloud.youxia.rocketmq;

import com.alibaba.cloud.dubbo.util.JSONUtils;
import com.rocketmq.cloud.youxia.config.TransactionProducerConfig;
import com.rocketmq.cloud.youxia.dto.LockStorageMessageDTO;
import com.rocketmq.cloud.youxia.listener.LockStorageTransactionListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@Component
public class ProducerLockStorageMessage {

    @Autowired
    private TransactionProducerConfig transactionProducerConfig;

    @Autowired
    private LockStorageTransactionListener localTransactionListener;

    private TransactionMQProducer transactionMQProducer;

    //生产锁前台库存的预处理消息
    public void produceLockStorageMessage(Map<String, String> messageMap) {
        try {
            final String producerGroup = transactionProducerConfig.getProducerGroup();
            if (null == transactionMQProducer) {
                transactionMQProducer = new TransactionMQProducer(producerGroup);
                //设置事务监听器
                transactionMQProducer.setTransactionListener(localTransactionListener);
                String instanceName = transactionProducerConfig.getInstanceName();
                String clientIp = transactionProducerConfig.getClientIp();
                transactionMQProducer.setInstanceName(instanceName);
                transactionMQProducer.setClientIP(clientIp);
                transactionMQProducer.setNamesrvAddr(transactionProducerConfig.getNamesrvAddr());
                transactionMQProducer.start();
            }
            String topic = transactionProducerConfig.getTopic();
            JSONUtils jsonUtils = new JSONUtils();
            String message = jsonUtils.toJSON(messageMap);
            LockStorageMessageDTO lockStorageMessageDTO = new LockStorageMessageDTO();
            lockStorageMessageDTO.setGobalUuid(messageMap.get("gobalUuid"));
            lockStorageMessageDTO.setProcessOnUuid(messageMap.get("processOnUuid"));
            lockStorageMessageDTO.setGoodId(messageMap.get("goodId"));
            lockStorageMessageDTO.setSkuId(messageMap.get("skuId"));
            lockStorageMessageDTO.setNum(messageMap.get("num"));
            Message transactionMessage = new Message(topic, (message).getBytes(RemotingHelper.DEFAULT_CHARSET));
            //LockStorageMessageDTO主要是用来传递非消息体的业务参数，比如UUID。
            transactionMQProducer.sendMessageInTransaction(transactionMessage, lockStorageMessageDTO);
        } catch (MQClientException e1) {
            System.out.println(e1.getMessage());
        } catch (UnsupportedEncodingException e2) {
            System.out.println(e2.getMessage());
        }
    }
}
