package com.rocketmq.cloud.youxia.thread;

import com.rocketmq.cloud.youxia.config.ConsumerConfig;
import com.rocketmq.cloud.youxia.listener.IMessageListenerConcurrently;
import com.rocketmq.cloud.youxia.task.ConsumerTask;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.ServiceThread;

public class ConsumerThread extends ServiceThread {

    private ConsumerConfig multiProcessConcurrentConfig;

    private ConsumerTask consumerTask;

    private String item;

    private boolean isGroup;

    public ConsumerThread(ConsumerConfig multiProcessConcurrentConfig
    ,ConsumerTask consumerTask,String item,boolean isGroup) {
        this.multiProcessConcurrentConfig = multiProcessConcurrentConfig;
        this.consumerTask = consumerTask;
        this.item=item;
        this.isGroup=isGroup;
    }

    @Override
    public void run() {
        while (!this.isStopped()) {
            try {
                start(item, isGroup);
            } catch (Exception e) {
            }
        }
    }

    private void start(String item,boolean isGroup) throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = null;
        if (isGroup) {
            defaultMQPushConsumer = new DefaultMQPushConsumer(item);
            defaultMQPushConsumer.setInstanceName(multiProcessConcurrentConfig.getSingleInstanceName());
        } else {
            defaultMQPushConsumer = new DefaultMQPushConsumer(multiProcessConcurrentConfig.getConsumerGroup());
            defaultMQPushConsumer.setInstanceName(item);
        }
        defaultMQPushConsumer.setClientIP(multiProcessConcurrentConfig.getClientIp());
        defaultMQPushConsumer.subscribe(multiProcessConcurrentConfig.getTopic(), "");
        defaultMQPushConsumer.setNamesrvAddr(multiProcessConcurrentConfig.getNamesrvAddr());
        //设置消费消息的线程池的最小核心线程数
        defaultMQPushConsumer.setConsumeThreadMin(multiProcessConcurrentConfig.getConsumerThreadMin());
        //设置消费消息的线程池的最大核心线程数
        defaultMQPushConsumer.setConsumeThreadMax(multiProcessConcurrentConfig.getConsumerThreadMax());
        defaultMQPushConsumer.registerMessageListener(new IMessageListenerConcurrently());
        defaultMQPushConsumer.start();
        consumerTask.getDefaultMQPushConsumerList().add(defaultMQPushConsumer);
        consumerTask.getDefaultMQPushConsumerMap().put(defaultMQPushConsumer.getInstanceName(), defaultMQPushConsumer);
    }

    @Override
    public String getServiceName() {
        return "消费消息的线程";
    }
}
