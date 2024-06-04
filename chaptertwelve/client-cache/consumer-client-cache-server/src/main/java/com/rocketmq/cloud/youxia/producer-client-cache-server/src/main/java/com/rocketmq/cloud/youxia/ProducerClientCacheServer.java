package com.rocketmq.cloud.youxia;

import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class ProducerClientCacheServer {

    public static void main(String[] args){
        SpringApplication springApplication=new SpringApplication(ProducerClientCacheServer.class);
        springApplication.run();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        ProducerThread producerThread = new ProducerThread();
        producerThread.setName("ProducerCacheClientService:" + RandomUtils.nextLong(1000, 1000000000));
        executorService.execute(producerThread);
    }

    static class ProducerThread extends Thread {
        private DefaultMQProducer mqProducer;
        private Object lock = new Object();

        @Override
        public void run() {
            synchronized (lock) {
                try {
                    //定义一个生产者对象mqProducer
                    if (null == mqProducer) {
                        mqProducer = new DefaultMQProducer("testCacheProducerClientGroup");
                        //设置Name Server的IP地址
                        mqProducer.setNamesrvAddr("127.0.0.1:9876");
                        mqProducer.setInstanceName(Thread.currentThread().getName() + ":" + RandomUtils.nextLong(1000, 100000000));
                        int port = Integer.parseInt(System.getProperty("server.port","8089"));
                        mqProducer.setClientIP("127.0.0.1:" + port);
                        //启动生产者
                        mqProducer.start();
                        //定义消息主题名称
                        String topic = "testCacheProducerClientMessage";
                        //构造消息体
                        Message msg = new Message(topic,
                                ("This is test cache client message" + RandomUtils.nextLong(1, 20000000)).
                                        getBytes(RemotingHelper.DEFAULT_CHARSET));
                        //生产消息
                        for (int i = 0; i < 1000000; i++) {
                            mqProducer.send(msg);
                        }
                    }
                } catch (MQClientException e) {
                    System.out.println(e.getMessage());
                } catch (MQBrokerException e1) {
                    System.out.println(e1.getMessage());
                } catch (RemotingException e2) {
                    System.out.println(e2.getMessage());
                } catch (InterruptedException e3) {
                    System.out.println(e3.getMessage());
                } catch (UnsupportedEncodingException e4) {
                    System.out.println(e4.getMessage());
                }
            }
        }
    }
}
