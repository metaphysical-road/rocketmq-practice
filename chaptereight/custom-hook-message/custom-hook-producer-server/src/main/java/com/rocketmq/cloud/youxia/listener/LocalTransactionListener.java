package com.rocketmq.cloud.youxia.listener;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

@Component
public class LocalTransactionListener implements TransactionListener{
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        try {
            Thread.sleep(200);
        }catch (InterruptedException e){
        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
