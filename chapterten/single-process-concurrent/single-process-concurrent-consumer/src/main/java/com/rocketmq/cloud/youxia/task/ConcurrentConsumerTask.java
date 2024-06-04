package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.SingleProcessConcurrentConfig;
import com.rocketmq.cloud.youxia.listener.IMessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ConcurrentConsumerTask {

    private DefaultMQPushConsumer defaultMQPushConsumer;

    //①从Nacos配置中心读取配置信息
    @Autowired
    private SingleProcessConcurrentConfig singleProcessConcurrentConfig;

    @Scheduled(fixedRate = 1000)
    public void consumeMessage() {
        try {
            if(singleProcessConcurrentConfig.getIsNeedModifyThreadNum().equals("true")){
                if(null!=defaultMQPushConsumer){
                    defaultMQPushConsumer.shutdown();
                    start();
                }
            }else if (null == defaultMQPushConsumer) {
                //②定义一个消费者客户端
                defaultMQPushConsumer = new DefaultMQPushConsumer(singleProcessConcurrentConfig.getConsumerGroup());
                start();
            }

        } catch (MQClientException e) {
            System.out.println(e.getCause().getMessage());
        }
    }

    private void start() throws MQClientException {
        //③订阅消息主题
        defaultMQPushConsumer.subscribe(singleProcessConcurrentConfig.getTopic(), "");
        //④设置Name Server地址
        defaultMQPushConsumer.setNamesrvAddr(singleProcessConcurrentConfig.getNamesrvAddr());
        //⑤设置消费消息的线程池的最小核心线程数
        defaultMQPushConsumer.setConsumeThreadMin(singleProcessConcurrentConfig.getConsumeThreadMin());
        //⑥设置消费消息的线程池的最大核心线程数
        defaultMQPushConsumer.setConsumeThreadMax(singleProcessConcurrentConfig.getConsumeThreadMax());
        //⑦注册自定义的监听器
        defaultMQPushConsumer.registerMessageListener(new IMessageListenerConcurrently());
        defaultMQPushConsumer.start();
    }
}
