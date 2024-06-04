package com.rocketmq.cloud.youxia.listener;

import com.rocketmq.cloud.youxia.config.AgencyProducerConfig;
import com.rocketmq.cloud.youxia.message.MessageEntity;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 事务监听器
 */
@Component
public class AgencyTransactionListener implements TransactionListener {

    @Autowired
    private AgencyProducerConfig agencyProducerConfig;

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        MessageEntity updateObject = (MessageEntity) arg;
        Long uk=updateObject.getUk();
        System.out.println("执行本地事务，唯一ID为:"+uk);
        if(agencyProducerConfig.getIsOpenException().equals("true")){
            return LocalTransactionState.UNKNOW;
        }else {
            return LocalTransactionState.COMMIT_MESSAGE;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
