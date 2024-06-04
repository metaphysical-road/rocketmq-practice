package com.rocketmq.cloud.youxia.rocketmq;

import com.alibaba.cloud.dubbo.util.JSONUtils;
import com.rocketmq.cloud.youxia.bo.SevenGoodBo;
import com.rocketmq.cloud.youxia.config.TransactionProducerConfig;
import com.rocketmq.cloud.youxia.entity.GoodLockStorageEntity;
import com.rocketmq.cloud.youxia.listener.CreateOrderTransactionListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.util.Date;

//@Component
//public class ProduceCreateOrderMessage {
//    @Autowired
//    private TransactionProducerConfig transactionProducerConfig;
//
//    @Autowired
//    private CreateOrderTransactionListener createOrderTransactionListener;
//
//    private TransactionMQProducer transactionMQProducer;
//
//    //生产创建前置订单的消息
//    public void prodBeforeOrderMessage(SevenGoodBo sevenGoodBo) {
//        try {
//            final String producerGroup = transactionProducerConfig.getProducerGroup();
//            if (null == transactionMQProducer) {
//                transactionMQProducer = new TransactionMQProducer(producerGroup);
//                transactionMQProducer.setTransactionListener(createOrderTransactionListener);
//                String instanceName = transactionProducerConfig.getInstanceName();
//                String clientIp = transactionProducerConfig.getClientIp();
//                transactionMQProducer.setInstanceName(instanceName);
//                transactionMQProducer.setClientIP(clientIp);
//                transactionMQProducer.setNamesrvAddr(transactionProducerConfig.getNamesrvAddr());
//                transactionMQProducer.start();
//            }
//            String topic = transactionProducerConfig.getTopic();
//            JSONUtils jsonUtils = new JSONUtils();
//            String message = jsonUtils.toJSON(sevenGoodBo);
//            GoodLockStorageEntity goodLockStorageEntity = new GoodLockStorageEntity();
//            goodLockStorageEntity.setUk(Long.valueOf(sevenGoodBo.getCurrentUuid()));
//            goodLockStorageEntity.setGoodId(sevenGoodBo.getGoodId());
//            goodLockStorageEntity.setSkuId(sevenGoodBo.getSkuId());
//            goodLockStorageEntity.setUserId(sevenGoodBo.getUserId());
//            goodLockStorageEntity.setLockPrice(sevenGoodBo.getPrice());
//            goodLockStorageEntity.setLockNum(sevenGoodBo.getNum());
//            goodLockStorageEntity.setGmtCreate(new Date());
//            goodLockStorageEntity.setGmtModified(new Date());
//            goodLockStorageEntity.setIsDeleted(0);
//            Message transactionMessage = new Message(topic, (message).getBytes(RemotingHelper.DEFAULT_CHARSET));
//            //生产分布式事务消息
//            transactionMQProducer.sendMessageInTransaction(transactionMessage, goodLockStorageEntity);
//        } catch (MQClientException e1) {
//            System.out.println(e1.getCause().getMessage());
//        } catch (UnsupportedEncodingException e2) {
//            System.out.println(e2.getCause().getMessage());
//        }
//    }
//}