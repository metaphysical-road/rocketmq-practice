package com.rocketmq.cloud.youxia.task;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class SendMessageWorker implements Runnable{
    private DefaultMQProducer defaultMQProducer;
    private String topic;
    private Long orderId;
    private String messageType;

    public SendMessageWorker(DefaultMQProducer defaultMQProducer, String topic,Long orderId,String messageType) {
        this.defaultMQProducer = defaultMQProducer;
        this.topic = topic;
        this.orderId = orderId;
        this.messageType = messageType;
    }

    @Override
    public void run() {
        try {
            String message = orderId + ":" + messageType;
            doWork(message);
        } catch (MQClientException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex1) {
            System.out.println(ex1.getMessage());
        } catch (MQBrokerException ex2) {
            System.out.println(ex2.getMessage());
        } catch (UnsupportedEncodingException ex3) {
            System.out.println(ex3.getMessage());
        } catch (RemotingException ex4) {
            System.out.println(ex4.getMessage());
        }
    }

    void doWork(final String message) throws MQClientException,InterruptedException, MQBrokerException, UnsupportedEncodingException
        , RemotingException {
        Message msg = new Message(topic, (message).getBytes(RemotingHelper.DEFAULT_CHARSET));
        //生产消息
        SendResult sendResult = defaultMQProducer.send(msg, new MessageQueueSelector() {
            @Override
            public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                Long id = (Long) arg;
                Long size = Long.valueOf(mqs.size());
                //用选择器MessageQueueSelector，从下标为[0 7]之间选择一个值，并获取指定下标的消息队列
                Long index = id % size;
                return mqs.get(index.intValue());
            }
        }, orderId);
        SendStatus sendStatus = sendResult.getSendStatus();
        if (sendStatus.equals(SendStatus.SEND_OK)) {
            System.out.println("使用"+defaultMQProducer.getDefaultMQProducerImpl().getmQClientFactory().getClientId() + "_" + Thread.currentThread().getId());
            System.out.println("发送消息：" + message + " 成功！" + " 消息队列为：" + sendResult.getMessageQueue());
        }
    }
}
