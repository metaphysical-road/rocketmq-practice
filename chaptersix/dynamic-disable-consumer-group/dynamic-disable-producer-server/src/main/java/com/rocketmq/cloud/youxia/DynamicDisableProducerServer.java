package com.rocketmq.cloud.youxia;

import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class DynamicDisableProducerServer {
    public static void main(String[] args){
        SpringApplication springApplication=new SpringApplication(DynamicDisableProducerServer.class);
        springApplication.run();
        ExecutorService executorService= Executors.newFixedThreadPool(3);
        executorService.execute(new ProducerThreadB());
        executorService.execute(new ProducerThreadA());
        executorService.execute(new ProducerThreadC());
    }

    static class ProducerThreadB implements Runnable {
        private DefaultMQProducer mqProducer;

        @Override
        public void run() {
            mqProducer = new DefaultMQProducer("disableConsumerTest");
            mqProducer.setNamesrvAddr("127.0.0.1:9876");
            mqProducer.setInstanceName("dynamicQueueTestB");
            mqProducer.setClientIP("127.0.0.1:2220");
            try {
                mqProducer.start();
            } catch (MQClientException e1) {
                System.out.println(e1.getMessage());
            }
            while (true) {
                try {
                    Message msg = new Message("disableConsumerTest",
                            "order454545",
                            "454545",
                            ("This is a testB message" + RandomUtils.nextLong(1, 20000000)).getBytes(RemotingHelper.DEFAULT_CHARSET));
                    mqProducer.send(msg, 3000);
                } catch (MQClientException e1) {
                    System.out.println(e1.getMessage());
                } catch (InterruptedException e2) {
                    System.out.println(e2.getMessage());
                } catch (RemotingException e3) {
                    System.out.println(e3.getMessage());
                } catch (MQBrokerException e4) {
                    System.out.println(e4.getMessage());
                } catch (UnsupportedEncodingException e5) {
                    System.out.println(e5.getMessage());
                }
            }
        }
    }

    static class ProducerThreadA implements Runnable {
        private DefaultMQProducer mqProducer;

        @Override
        public void run() {
            mqProducer = new DefaultMQProducer("disableConsumerTest");
            mqProducer.setNamesrvAddr("127.0.0.1:9876");
            mqProducer.setInstanceName("dynamicQueueTestA");
            mqProducer.setClientIP("127.0.0.1:2001");
            try {
                mqProducer.start();
            } catch (MQClientException e1) {
                System.out.println(e1.getMessage());
            }
            while (true) {
                try {
                    Message msg = new Message("disableConsumerTest",
                            "order454545",
                            "454545",
                            ("This is a testA message" + RandomUtils.nextLong(1, 20000000)).getBytes(RemotingHelper.DEFAULT_CHARSET));
                    mqProducer.send(msg, 3000);
                } catch (MQClientException e1) {
                    System.out.println(e1.getMessage());
                } catch (InterruptedException e2) {
                    System.out.println(e2.getMessage());
                } catch (RemotingException e3) {
                    System.out.println(e3.getMessage());
                } catch (MQBrokerException e4) {
                    System.out.println(e4.getMessage());
                } catch (UnsupportedEncodingException e5) {
                    System.out.println(e5.getMessage());
                }
            }
        }
    }

    static class ProducerThreadC implements Runnable {
        private DefaultMQProducer mqProducer;

        @Override
        public void run() {
            mqProducer = new DefaultMQProducer("disableConsumerTest");
            mqProducer.setNamesrvAddr("127.0.0.1:9876");
            mqProducer.setInstanceName("dynamicQueueTestC");
            mqProducer.setClientIP("127.0.0.1:2002");
            try {
                mqProducer.start();
            } catch (MQClientException e1) {
                System.out.println(e1.getMessage());
            }
            while (true) {
                try {
                    Message msg = new Message("disableConsumerTest",
                            "order454545",
                            "454545",
                            ("This is a testC message" + RandomUtils.nextLong(1, 20000000)).getBytes(RemotingHelper.DEFAULT_CHARSET));
                    mqProducer.send(msg, 3000);
                } catch (MQClientException e1) {
                    System.out.println(e1.getMessage());
                } catch (InterruptedException e2) {
                    System.out.println(e2.getMessage());
                } catch (RemotingException e3) {
                    System.out.println(e3.getMessage());
                } catch (MQBrokerException e4) {
                    System.out.println(e4.getMessage());
                } catch (UnsupportedEncodingException e5) {
                    System.out.println(e5.getMessage());
                }
            }
        }
    }
}
