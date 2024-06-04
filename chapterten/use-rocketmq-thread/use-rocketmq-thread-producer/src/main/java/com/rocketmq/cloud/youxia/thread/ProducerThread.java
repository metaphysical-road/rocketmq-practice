package com.rocketmq.cloud.youxia.thread;

import com.rocketmq.cloud.youxia.config.UseRocketmqThreadConfig;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.consumer.PullRequest;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.ServiceThread;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import java.io.UnsupportedEncodingException;

public class ProducerThread extends ServiceThread {

    private DefaultMQProducer random;
    private UseRocketmqThreadConfig useRocketmqThreadConfig;

    public ProducerThread(DefaultMQProducer random,
                               UseRocketmqThreadConfig useRocketmqThreadConfig){
        this.random=random;
        this.useRocketmqThreadConfig=useRocketmqThreadConfig;
    }

    @Override
    public String getServiceName() {
        return "ProducerThread";
    }

    @Override
    public void run() {
        while (!this.isStopped()) {
            try {
                sendMessgae(random, useRocketmqThreadConfig);
            } catch (Exception e) {
            }
        }
    }

    private void sendMessgae(DefaultMQProducer random,
                             UseRocketmqThreadConfig useRocketmqThreadConfig) {
        try {
            //生产消息
            String topic = useRocketmqThreadConfig.getTopic();
            //构造消息体
            Message msg = new Message(topic,
                    ("this is a test message" + RandomUtils.nextLong(1, 20000000)).
                            getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = random.send(msg);
            if (result.getSendStatus().equals(SendStatus.SEND_OK)) {
                System.out.println("生产消息成功，消息ID为:" + result.getMsgId());
            }
        } catch (UnsupportedEncodingException e1) {
            System.out.println(e1.getCause().getMessage());
        } catch (MQClientException e2) {
            System.out.println(e2.getCause().getMessage());
        } catch (RemotingException e3) {
            System.out.println(e3.getCause().getMessage());
        } catch (MQBrokerException e4) {
            System.out.println(e4.getMessage());
        } catch (InterruptedException e5) {
            System.out.println(e5.getCause().getMessage());
        }
    }
}
