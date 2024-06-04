package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.ProducerConfig;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.LongAdder;

@Component
@EnableScheduling
public class ProducerMessageTask {

    private DefaultMQProducer defaultMQProducer;
    @Autowired
    private ProducerConfig producerConfig;
    private volatile LongAdder longAdder=new LongAdder();

    @Scheduled(cron = "*/5 * * * * ?")
    public void producerMessage() {
        if (producerConfig.getIsUseNewProducer().equals("true")&&longAdder.intValue()<=20) {
            longAdder.increment();
            DefaultMQProducer useNewProducer = new DefaultMQProducer("statelessNameServer");
            useNewProducer.setInstanceName("statelessNameServer" + longAdder.intValue());
            useNewProducer.setNamesrvAddr("127.0.0.1:9876;127.0.0.1:9877;127.0.0.1:9878");
            useNewProducer.setClientIP("127.0.0.1:222" + longAdder.intValue());
            try {
                useNewProducer.start();
            } catch (MQClientException e1) {
                System.out.println(e1.getMessage());
            }
            sendMessage(useNewProducer);
        } else {
            if (null == defaultMQProducer) {
                defaultMQProducer = new DefaultMQProducer("statelessNameServer");
                defaultMQProducer.setInstanceName("statelessNameServer");
                defaultMQProducer.setNamesrvAddr("127.0.0.1:9876;127.0.0.1:9877;127.0.0.1:9878");
                defaultMQProducer.setClientIP("127.0.0.1:2221");
                try {
                    defaultMQProducer.start();
                } catch (MQClientException e1) {
                    System.out.println(e1.getMessage());
                }
            }
            sendMessage(defaultMQProducer);
        }
    }

    private void sendMessage(DefaultMQProducer defaultMQProducer) {
        try {
            Message msg = new Message("statelessNameServer",
                    "order454545",
                    "454545",
                    ("This is a testB message" + RandomUtils.nextLong(1, 20000000)).getBytes(RemotingHelper.DEFAULT_CHARSET));
            defaultMQProducer.send(msg, 3000);
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
