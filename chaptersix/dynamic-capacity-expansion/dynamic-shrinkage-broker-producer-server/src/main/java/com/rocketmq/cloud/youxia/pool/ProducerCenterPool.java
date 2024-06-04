package com.rocketmq.cloud.youxia.pool;

import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ProducerCenterPool {
    private final Map<String, Map<String, DefaultMQProducer>> cacheDefaultMQProducer = new ConcurrentHashMap<>();
    private final Map<String, AtomicBoolean> mqProducerStatus = new ConcurrentHashMap<>();
    private final Lock producerInfoLock = new ReentrantLock();

    public Map<String, AtomicBoolean> getMqProducerStatus() {
        return mqProducerStatus;
    }

    public List<DefaultMQProducer> getDefaultMQProducer(String topic) {
        Map<String, DefaultMQProducer> producerMap = cacheDefaultMQProducer.get(topic);
        List<DefaultMQProducer> result = new CopyOnWriteArrayList<>();
        if (null != producerMap && producerMap.size() > 0) {
            Iterator<DefaultMQProducer> iterator = producerMap.values().iterator();
            while (iterator.hasNext()) {
                DefaultMQProducer defaultMQProducer = iterator.next();
                result.add(defaultMQProducer);
            }
            return result;
        }
        return new ArrayList<>();
    }

    public void updateDefaultMQProducerInfo(String mappingRelation,
                                            String nameServerAddress, String type,String topicName) {
        try {
            if (this.producerInfoLock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                try {
                    if (type.equals("0")) {
                        addCacheDefaultMQProducer(mappingRelation, nameServerAddress);
                    } else if (type.equals("1")) {
                        deleteCacheDefaultMQProducer(mappingRelation,topicName);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    producerInfoLock.unlock();
                }
            } else {
                System.out.println("加锁失败！");
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    //删除生产者实例
    public void deleteCacheDefaultMQProducer(String data,String topicName) {
        Map<String, DefaultMQProducer> oldResult = cacheDefaultMQProducer.get(topicName);
        if (StringUtils.isNotEmpty(data)) {
            String[] item = data.split(";");
            for (String s : item) {
                String[] realValue = s.split("_");
                if (!cacheDefaultMQProducer.containsKey(topicName)) {
                    //不执行任何操作
                } else {
                    //如果已经存在，则遍历
                    Iterator<String> iteratorKey = oldResult.keySet().iterator();
                    while (iteratorKey.hasNext()) {
                        String key = iteratorKey.next();
                        if (key.equals(s)) {
                            DefaultMQProducer defaultMQProducer = oldResult.get(key);
                            defaultMQProducer.shutdown();
                            oldResult.remove(key);
                            mqProducerStatus.remove(key);
                        } else {
                            //
                        }
                    }
                }
            }
            cacheDefaultMQProducer.put(topicName, oldResult);
        }
    }

    //添加生产者实例
    public void addCacheDefaultMQProducer(String data, String nameServerAddress) {
        //配置格式如下：
        if (StringUtils.isNotEmpty(data)) {
            String[] item = data.split(";");
            for (String s : item) {
                String[] realValue = s.split("_");
                String topic = realValue[0];
                String clientIP = realValue[1];
                String instanceName = realValue[2];
                String producerGroup = realValue[3];
                //在缓存中不存在指定主题的缓存信息
                if (!cacheDefaultMQProducer.containsKey(topic)) {
                    //新建一个
                    Map<String ,DefaultMQProducer> addNewResult=new ConcurrentHashMap<>();
                    cacheDefaultMQProducer.putIfAbsent(topic,addNewResult);
                    setProducerItem(nameServerAddress, s, clientIP, instanceName, producerGroup,
                            cacheDefaultMQProducer.get(topic));
                } else {
                    //如果已经存在，则遍历
                    Map<String, DefaultMQProducer> oldResult = cacheDefaultMQProducer.get(topic);
                    //重新设置生产者实例，key为s
                    if (!oldResult.containsKey(s)) {
                        setProducerItem(nameServerAddress, s, clientIP, instanceName, producerGroup, oldResult);
                    }
                }
            }
        }
    }

    private void setProducerItem(String nameServerAddress, String s, String clientIP, String instanceName,
                                 String producerGroup, Map<String, DefaultMQProducer> addNewResult) {
        DefaultMQProducer mqProducer = new DefaultMQProducer(producerGroup);
        mqProducer.setNamesrvAddr(nameServerAddress);
        mqProducer.setInstanceName(instanceName);
        mqProducer.setClientIP(clientIP);
        try {
            mqProducer.start();
            //方便缩容
            mqProducerStatus.put(s, new AtomicBoolean(true));
            addNewResult.put(s, mqProducer);
        } catch (MQClientException mqClientException) {
            System.out.println(mqClientException.getMessage());
        }
    }
}
