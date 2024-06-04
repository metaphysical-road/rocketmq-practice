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
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class ProducerApplicationServer {

    public static void main(String[] args) throws MQClientException, InterruptedException{
        SpringApplication springApplication=new SpringApplication(ProducerApplicationServer.class);
        springApplication.run();
        DefaultMQProducer mqProducer=new DefaultMQProducer("defaultProducerGroup");
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        mqProducer.start();
        ExecutorService executorService= Executors.newFixedThreadPool(1);
        executorService.execute(new SendMessageThread(mqProducer));
    }

    static class SendMessageThread implements Runnable {
        private DefaultMQProducer mqProducer;
        public SendMessageThread(DefaultMQProducer mqProducer) {
            this.mqProducer = mqProducer;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                try {
                    Message msg = new Message("managementInformationSystem",
                            "order123456789",
                            "123456789",
                            ("This is a test message" + RandomUtils.nextLong(1, 20000000)).getBytes(RemotingHelper.DEFAULT_CHARSET));
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
