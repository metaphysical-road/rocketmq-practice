package com.rocketmq.cloud.youxia;

import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.exception.RequestTimeoutException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.RequestCallback;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageAccessor;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.UnsupportedEncodingException;
import java.util.List;

@SpringBootApplication
public class ProducerRequestReplyApplicationServer {
    public static void main(String[] args) throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException,RequestTimeoutException{
        SpringApplication springApplication=new SpringApplication(ProducerRequestReplyApplicationServer.class);
        springApplication.run();
        testAsyncDefaultRequestReplyMessage();
        testAsyncKernelRequestReplyMessage();
        testAsyncSelectRequestReplyMessage();
    }

    //"默认模式"生产请求/应答消息
    static public void testAsyncDefaultRequestReplyMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException , RequestTimeoutException {
        //定义一个回调函数RequestCallback
        RequestCallback requestCallback = new RequestCallback() {
            @Override public void onSuccess(Message message) {
                //用回调函数等待应答消息
                System.out.println("send async default pattern request reply message success"+",the message is "+message.toString());
            }
            @Override public void onException(Throwable e) {
                System.out.println(e.getMessage());
            }
        };
        //定义一个生产者对象mqProducer
        DefaultMQProducer requestReplyProducer = new DefaultMQProducer("testAsyncDefaultRequestReplyProducerGroup");
        //设置Name Server的IP地址
        requestReplyProducer.setNamesrvAddr("127.0.0.1:9876");
        //启动生产者
        requestReplyProducer.start();
        //定义消息主题名称
        String topic = "testAsyncDefaultRequestReplyProducerMessage";
        //构造消息体
        Message msg = new Message(topic,
                ("This is async default pattern request reply test message" + RandomUtils.nextLong(1, 20000000)).
                        getBytes(RemotingHelper.DEFAULT_CHARSET));
//        MessageAccessor.putProperty(msg, MessageConst.PROPERTY_MESSAGE_TYPE, MixAll.REPLY_MESSAGE_FLAG);
        MessageAccessor.putProperty(msg,MessageConst.PROPERTY_CLUSTER,"DefaultCluster");
        //生产请求/应答消息
        requestReplyProducer.request(msg,requestCallback,3000);
    }

    //"内核模式"生产请求/应答消息
    static public void testAsyncKernelRequestReplyMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException , RequestTimeoutException {
        //定义一个回调函数RequestCallback
        RequestCallback requestCallback = new RequestCallback() {
            @Override public void onSuccess(Message message) {
                System.out.println("send async default pattern request reply message success"+",the message is "+message.toString());
            }
            @Override public void onException(Throwable e) {
                System.out.println(e.getMessage());
            }
        };
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testAsyncKernelRequestReplyProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //启动生产者
        mqProducer.start();
        //定义消息主题名称
        String topic="testAsyncKernelRequestReplyProducerMessage";
        //构造消息体
        Message msg = new Message(topic,
                ("This is async kernel pattern request reply test message" + RandomUtils.nextLong(1, 20000000)).
                        getBytes(RemotingHelper.DEFAULT_CHARSET));
//        MessageAccessor.putProperty(msg, MessageConst.PROPERTY_MESSAGE_TYPE, MixAll.REPLY_MESSAGE_FLAG);
        MessageAccessor.putProperty(msg,MessageConst.PROPERTY_CLUSTER,"DefaultCluster");
        String brokerName="broker-a";
        Integer queueId=1;
        //构造一个消息队列，消息队列ID为1
        MessageQueue messageQueue = new MessageQueue(topic, brokerName, queueId);
        //生产请求/应答消息
        mqProducer.request(msg,messageQueue,requestCallback,3000);
    }

    //"选择器模式"生产请求/应答消息
    static public void testAsyncSelectRequestReplyMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
        //定义一个回调函数RequestCallback
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onSuccess(Message message) {
                System.out.println("send async select pattern request reply message success" + ",the message is " + message.toString());
            }

            @Override
            public void onException(Throwable e) {
                System.out.println(e.getMessage());
            }
        };
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testAsyncSelectRequestReplyProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //定义消息主题名称
        String topic = "testAsyncSelectRequestReplyProducerMessage";
        //启动生产者
        mqProducer.start();
        for (int i = 0; i < 100; i++) {
            //RocketMQ消息队列默认队列数为8个，模拟业务ID的取模运算，并选择消息队列
            int businessId = i % 8;
            //构造消息体
            Message msg =
                    new Message(topic,
                            ("This is async select pattern request reply test message " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
//            MessageAccessor.putProperty(msg, MessageConst.PROPERTY_MESSAGE_TYPE, MixAll.REPLY_MESSAGE_FLAG);
            MessageAccessor.putProperty(msg,MessageConst.PROPERTY_CLUSTER,"DefaultCluster");
            //生产消息
            mqProducer.request(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;
                    //用选择器MessageQueueSelector，从下标为[0 7]之间选择一个值，并获取指定下标的消息队列
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, businessId, requestCallback, 3000);
        }
    }
}
