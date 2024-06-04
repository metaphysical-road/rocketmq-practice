package com.rocketmq.cloud.youxia;

import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class ProducerApplicationServer {
    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException,
            RemotingException, MQBrokerException, InterruptedException {
        SpringApplication springApplication = new SpringApplication(ProducerApplicationServer.class);
        springApplication.run();

        testSyncDefaultProducerMessage();
//            testSyncKernelProducerMessage();
//            testSyncSelectProducerMessage();
//            testOnewayDefaultProducerMessage();
//            Thread.sleep(8000);
//            testOnewayKernelProducerMessage();
//            testOnewaySelectProducerMessage();
//            testAsyncDefaultProducerMessage();
//            testAsyncKernelProducerMessage();
//            testAsyncSelectProducerMessage();
    }
    //"默认方式"的"同步模式"
    static void testSyncDefaultProducerMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testSyncDefaultProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //启动生产者
        mqProducer.start();
        //定义消息主题名称
        String topic="testSyncDefaultProducerMessage";
        //构造消息体
        Message msg = new Message(topic,
                ("This is sync default pattern test message" + RandomUtils.nextLong(1, 20000000)).
                        getBytes(RemotingHelper.DEFAULT_CHARSET));
        //生产消息
        for(int i=0;i<1000000000;i++) {
            mqProducer.send(msg);
        }
    }
    //"内核方式"的"同步模式"
    static void testSyncKernelProducerMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testSyncKernelProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //启动生产者
        mqProducer.start();
        //定义消息主题名称
        String topic="testSyncKernelProducerMessage";
        //构造消息体
        Message msg = new Message(topic,("This is sync kernel pattern test message" + RandomUtils.nextLong(1, 20000000)).
                        getBytes(RemotingHelper.DEFAULT_CHARSET));
        String brokerName="broker-a";
        Integer queueId=1;
        //构造一个消息队列，消息队列ID为1
        MessageQueue messageQueue = new MessageQueue(topic, brokerName, queueId);
        //生产消息
        mqProducer.send(msg,messageQueue);
    }
    //"选择器方式"的"同步模式"
    static void testSyncSelectProducerMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testSyncSelectProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //定义消息主题名称
        String topic = "testSyncSelectProducerMessageTest";
        //启动生产者
        mqProducer.start();
        for (int i = 0; i < 100; i++) {
            //RocketMQ消息队列默认队列数为8个，模拟业务ID的取模运算，并选择消息队列
            int businessId = i % 8;
            //构造消息体
            Message msg =
                    new Message(topic,
                            ("This is sync select pattern test message " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            //生产消息
            SendResult sendResult = mqProducer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;
                    //用选择器MessageQueueSelector，从下标为[0 7]之间选择一个值，并获取指定下标的消息队列
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, businessId);
            System.out.printf("%s%n", sendResult);
        }
    }

    //"默认方式"的"最多发送一次模式"
    static void testOnewayDefaultProducerMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testOnewayDefaultProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //启动生产者
        mqProducer.start();
        //定义消息主题名称
        String topic="testOnewayDefaultProducerMessage";
        //构造消息体
        Message msg = new Message(topic,
                ("This is oneway default pattern test message" + RandomUtils.nextLong(1, 20000000)).
                        getBytes(RemotingHelper.DEFAULT_CHARSET));
        //生产消息
        mqProducer.sendOneway(msg);
    }
    //"内核方式"的"最多发送一次模式"
    static void testOnewayKernelProducerMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testOnewayKernelProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //启动生产者
        mqProducer.start();
        //定义消息主题名称
        String topic="testOnewayKernelProducerMessage";
        //构造消息体
        Message msg = new Message(topic,("This is oneway kernel pattern test message" + RandomUtils.nextLong(1, 20000000)).
                getBytes(RemotingHelper.DEFAULT_CHARSET));
        String brokerName="broker-a";
        Integer queueId=1;
        //构造一个消息队列，消息队列ID为1
        MessageQueue messageQueue = new MessageQueue(topic, brokerName, queueId);
        //生产消息
        mqProducer.sendOneway(msg,messageQueue);
    }
    //"选择器方式"的"最多发送一次模式"
    static void testOnewaySelectProducerMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testOnewaySelectProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //定义消息主题名称
        String topic = "testOnewaySelectProducerMessage";
        //启动生产者
        mqProducer.start();
        for (int i = 0; i < 100; i++) {
            //RocketMQ消息队列默认队列数为8个，模拟业务ID的取模运算，并选择消息队列
            int businessId = i % 8;
            //构造消息体
            Message msg =
                    new Message(topic,
                            ("This is oneway select pattern test message " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            //生产消息
            mqProducer.sendOneway(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;
                    //用选择器MessageQueueSelector，从下标为[0 7]之间选择一个值，并获取指定下标的消息队列
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, businessId);
        }
    }

    //"默认方式"的"异步模式"
    static void testAsyncDefaultProducerMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
        final AtomicInteger autoIncrement = new AtomicInteger(0);
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        SendCallback sendCallback = new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                if(sendResult.getSendStatus().equals(SendStatus.SEND_OK)){
                    System.out.println("send async default pattern message success"+",the message id is "+sendResult.getMsgId());
                    countDownLatch.countDown();
                }
            }

            @Override
            public void onException(Throwable e) {
                System.out.println(e.getMessage());
                autoIncrement.incrementAndGet();
                countDownLatch.countDown();
            }
        };
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testAsyncDefaultProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //启动生产者
        mqProducer.start();
        //定义消息主题名称
        String topic="testAsyncDefaultProducerMessage";
        //构造消息体
        Message msg = new Message(topic,
                ("This is async default pattern test message" + RandomUtils.nextLong(1, 20000000)).
                        getBytes(RemotingHelper.DEFAULT_CHARSET));
        //生产消息
        mqProducer.send(msg,sendCallback,3000);
    }

    //"内核方式"的"异步模式"
    static void testAsyncKernelProducerMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
        final AtomicInteger autoIncrement = new AtomicInteger(0);
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        //自定义一个处理生产结果的回调函数
        SendCallback sendCallback = new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                if(sendResult.getSendStatus().equals(SendStatus.SEND_OK)){
                    System.out.println("send async kernel pattern message success"+",the message id is "+sendResult.getMsgId());
                    countDownLatch.countDown();
                }
            }

            @Override
            public void onException(Throwable e) {
                System.out.println(e.getMessage());
                autoIncrement.incrementAndGet();
                countDownLatch.countDown();
            }
        };
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testAsyncKernelProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //启动生产者
        mqProducer.start();
        //定义消息主题名称
        String topic="testAsyncKernelProducerMessage";
        //构造消息体
        Message msg = new Message(topic,
                ("This is async kernel pattern test message" + RandomUtils.nextLong(1, 20000000)).
                        getBytes(RemotingHelper.DEFAULT_CHARSET));
        String brokerName="broker-a";
        Integer queueId=1;
        //构造一个消息队列，消息队列ID为1
        MessageQueue messageQueue = new MessageQueue(topic, brokerName, queueId);
        //生产消息
        mqProducer.send(msg,messageQueue,sendCallback);
    }

    //"选择器方式"的"最多发送一次模式"
    static void testAsyncSelectProducerMessage() throws MQClientException,
            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("testAsyncSelectProducerGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //定义消息主题名称
        String topic = "testAsyncSelectProducerMessage";
        final AtomicInteger autoIncrement = new AtomicInteger(0);
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        //自定义一个处理生产结果的回调函数
        SendCallback sendCallback = new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                if(sendResult.getSendStatus().equals(SendStatus.SEND_OK)){
                    System.out.println("send async select pattern message success"+",the message id is "+sendResult.getMsgId());
                    countDownLatch.countDown();
                }
            }

            @Override
            public void onException(Throwable e) {
                System.out.println(e.getMessage());
                autoIncrement.incrementAndGet();
                countDownLatch.countDown();
            }
        };
        //启动生产者
        mqProducer.start();
        for (int i = 0; i < 100; i++) {
            //RocketMQ消息队列默认队列数为8个，模拟业务ID的取模运算，并选择消息队列
            int businessId = i % 8;
            //构造消息体
            Message msg =
                    new Message(topic,
                            ("This is async select pattern test message " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            //生产消息
            mqProducer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;
                    //用选择器MessageQueueSelector，从下标为[0 7]之间选择一个值，并获取指定下标的消息队列
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, businessId,sendCallback,3000);
        }
    }
}
