package com.rocketmq.cloud.youxia.pool;

import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ConsumerCenterPool {
    private final Map<String, Map<String, DefaultMQPushConsumer>> cacheDefaultMQConsumer = new ConcurrentHashMap<>();
    private final Map<String, AtomicBoolean> mqConsumerStatus = new ConcurrentHashMap<>();
    private final Lock consumerInfoLock = new ReentrantLock();

    public void updateConsumerStatus(String key,AtomicBoolean status){
        mqConsumerStatus.putIfAbsent(key,status);
    }

    public Map<String, AtomicBoolean> getMqConsumerStatus() {
        return mqConsumerStatus;
    }

    public void deleteConsumerStatus(String key){
        mqConsumerStatus.remove(key);
    }

    public Map<String,DefaultMQPushConsumer> getDefaultMQConsumer(String topic) {
        Map<String, DefaultMQPushConsumer> consumerMap = cacheDefaultMQConsumer.get(topic);
        if (null != consumerMap && consumerMap.size() > 0) {
            return  consumerMap;
        }
        return new ConcurrentHashMap<>();
    }

    public void updateDefaultMQConsumerInfo(String mappingRelation,
                                            String nameServerAddress, String type,String topicName) {
        try {
            if (this.consumerInfoLock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                try {
                    if (type.equals("0")) {
                        addCacheDefaultMQConsumer(mappingRelation, nameServerAddress);
                    } else if (type.equals("1")) {
                        deleteCacheDefaultMQConsumer(mappingRelation,topicName);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    consumerInfoLock.unlock();
                }
            } else {
                System.out.println("加锁失败！");
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    //删除消费者实例
    public void deleteCacheDefaultMQConsumer(String data,String topicName) {
        Map<String, DefaultMQPushConsumer> oldResult = cacheDefaultMQConsumer.get(topicName);
        if (StringUtils.isNotEmpty(data)) {
            String[] item = data.split(";");
            for (String s : item) {
                String[] realValue = s.split("_");
                if (!cacheDefaultMQConsumer.containsKey(topicName)) {
                    //不执行任何操作
                } else {
                    //如果已经存在，则遍历
                    Iterator<String> iteratorKey = oldResult.keySet().iterator();
                    while (iteratorKey.hasNext()) {
                        String key = iteratorKey.next();
                        if (key.equals(s)) {
                            DefaultMQPushConsumer defaultMQPushConsumer = oldResult.get(key);
                            defaultMQPushConsumer.shutdown();
                            oldResult.remove(key);
                            //删除消费者的运行状态
                            mqConsumerStatus.put(key, new AtomicBoolean(false));
                        } else {
                        }
                    }
                }
            }
            cacheDefaultMQConsumer.put(topicName, oldResult);
        }
    }

    //添加消费者实例
    public void addCacheDefaultMQConsumer(String data, String nameServerAddress) {
        //配置格式如下：
        if (StringUtils.isNotEmpty(data)) {
            String[] item = data.split(";");
            for (String s : item) {
                String[] realValue = s.split("_");
                String topic = realValue[0];
                String clientIP = realValue[1];
                String instanceName = realValue[2];
                String consumerGroup = realValue[3];
                //在缓存中不存在指定主题的缓存信息
                if (!cacheDefaultMQConsumer.containsKey(topic)) {
                    //新建一个
                    Map<String ,DefaultMQPushConsumer> addNewResult=new ConcurrentHashMap<>();
                    cacheDefaultMQConsumer.putIfAbsent(topic,addNewResult);
                    setConsumerItem(nameServerAddress, s, clientIP, instanceName, consumerGroup,
                            cacheDefaultMQConsumer.get(topic),topic);
                } else {
                    //如果已经存在，则遍历
                    Map<String, DefaultMQPushConsumer> oldResult = cacheDefaultMQConsumer.get(topic);
                    //重新设置生产者实例，key为s
                    if (!oldResult.containsKey(s)) {
                        setConsumerItem(nameServerAddress, s, clientIP, instanceName, consumerGroup, oldResult,topic);
                    }
                }
            }
        }
    }

    private void setConsumerItem(String nameServerAddress, String s, String clientIP, String instanceName,
                                 String consumerGroup, Map<String, DefaultMQPushConsumer> addNewResult,String topicName) {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
        defaultMQPushConsumer.setNamesrvAddr(nameServerAddress);
        defaultMQPushConsumer.setInstanceName(instanceName);
        defaultMQPushConsumer.setClientIP(clientIP);
        try {
            defaultMQPushConsumer.subscribe(topicName,"");
            defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            //为了方便缩容，只是初始化完成消费者对象，但是不开启实例
            addNewResult.put(s, defaultMQPushConsumer);
        } catch (MQClientException mqClientException) {
            System.out.println(mqClientException.getMessage());
        }
    }
}
