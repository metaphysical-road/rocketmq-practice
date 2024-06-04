package com.rocketmq.cloud.youxia.task;

import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.config.CustomHookConfig;
import com.rocketmq.cloud.youxia.dispatch.ProducerAsyncTraceDispatcher;
import com.rocketmq.cloud.youxia.listener.LocalTransactionListener;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.hook.EndTransactionHook;
import org.apache.rocketmq.client.hook.SendMessageHook;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

@EnableScheduling
@Component
public class ProducerTask {
    @Autowired
    private CustomHookConfig customHookConfig;

    @Autowired
    private EndTransactionHook endTransactionHook;

    @Autowired
    private SendMessageHook sendMessageHook;

    @Autowired
    private ProducerAsyncTraceDispatcher producerAsyncTraceDispatcher;

    @Autowired
    private LocalTransactionListener localTransactionListener;

    private volatile AtomicBoolean flag = new AtomicBoolean(false);

    @Scheduled(fixedRate = 2000)
    public void producerMessage() throws MQClientException,UnsupportedEncodingException {
        if (flag.compareAndSet(false, true)) {
            //定义一个生产者对象mqProducer
            TransactionMQProducer mqProducer = new TransactionMQProducer(customHookConfig.getProducerGroup());
            mqProducer.getDefaultMQProducerImpl().registerSendMessageHook(sendMessageHook);
            mqProducer.getDefaultMQProducerImpl().registerEndTransactionHook(endTransactionHook);
            //设置Name Server的IP地址
            mqProducer.setNamesrvAddr(customHookConfig.getNameAddress());
            //启动生产者
            mqProducer.start();
            producerAsyncTraceDispatcher.setHostProducer(mqProducer.getDefaultMQProducerImpl());
            producerAsyncTraceDispatcher.start(customHookConfig.getNameAddress(),mqProducer.getAccessChannel());
            //定义消息主题名称
            String topic = customHookConfig.getTopic();
            mqProducer.setTransactionListener(localTransactionListener);
            for (int i = 0; i < 1; i++) {
                //构造消息体
                Message msg = new Message(topic,
                        ("this is a trace message" + RandomUtils.nextLong(1, 20000000)).
                                getBytes(RemotingHelper.DEFAULT_CHARSET));
                String content = new String(msg.getBody(), Charsets.UTF_8);
                //生产事务消息
                TransactionSendResult result = mqProducer.sendMessageInTransaction(msg, content);
                String msgId = result.getMsgId();
                String offsetMsgId = result.getOffsetMsgId();
                System.out.println("msgId:" + msgId + " offsetMsgId:" + offsetMsgId);
            }
            flag.set(true);
        }
    }
}
