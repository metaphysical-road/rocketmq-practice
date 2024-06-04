package com.rocketmq.cloud.youxia.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmq.cloud.youxia.config.PressureMeasurementBatchConfig;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

@Component
@EnableScheduling
public class ProducerTask {

    @Autowired
    private MessageCachePool messageCachePool;

    @Autowired
    private PressureMeasurementBatchConfig pressureMeasurementBatchConfig;

    private Map<String, DefaultMQProducer> producerMap = new ConcurrentHashMap<>();

    @Autowired
    private GlobalCyclicBarrierLock globalCyclicBarrierLock;

    @Autowired
    private StatisticsPool statisticsPool;

    private ObjectMapper objectMapper = new ObjectMapper();

    private volatile long golbalBeginTime = System.currentTimeMillis();

    private volatile AtomicBoolean isFirstExecute=new AtomicBoolean(true);

    private volatile LongAdder longAdder=new LongAdder();

    @Autowired
    private ExecutorServiceCache executorServiceCache;

    @Scheduled(fixedRate = 6000)
    public void producerMessage() {
        executorServiceCache.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    globalCyclicBarrierLock.getGlobalLock().lock();
                    Integer adder=0;
                    try {
                        if(statisticsPool.getIsIncrement().compareAndSet(false,true)){
                            statisticsPool.increment();
                            statisticsPool.getIsIncrement().set(true);
                        }
                        adder = statisticsPool.getLongAdder().intValue();
                    } finally {
                        globalCyclicBarrierLock.getGlobalLock().unlock();
                    }
                    Long totalMessageSize = 0L;
                    if (pressureMeasurementBatchConfig.getOpenProducer().equals("true")) {
                        long beginTime = System.currentTimeMillis();
                        if(isFirstExecute.compareAndSet(true,false)){
                            golbalBeginTime = System.currentTimeMillis();
                            isFirstExecute.set(false);
                        }
                        String topicName = pressureMeasurementBatchConfig.getTopicName();
                        String[] topicNameArrays = topicName.split(",");
                        for (String s : topicNameArrays) {
                            if (!producerMap.containsKey(s)) {
                                String instanceName = pressureMeasurementBatchConfig.getInstanceName() + RandomUtils.nextLong(100, 1000000000);
                                DefaultMQProducer mqProducer = new DefaultMQProducer(pressureMeasurementBatchConfig.getProducerGroup());
                                //设置Name Server的IP地址
                                mqProducer.setNamesrvAddr(pressureMeasurementBatchConfig.getNamesrvAddr());
                                mqProducer.setInstanceName(instanceName);
                                mqProducer.setClientIP(pressureMeasurementBatchConfig.getClientIP());
                                //启动生产者
                                mqProducer.start();
                                producerMap.put(s, mqProducer);
                            }
                        }
                        Map<String, List<Message>> messagePool = messageCachePool.getMessagePool();
                        for (String s : topicNameArrays) {
                            if (messagePool.containsKey(s)) {
                                DefaultMQProducer sendMQProducer = producerMap.get(s);
                                List<Message> producerMessage = messagePool.get(s);
                                Integer size = producerMessage.size();
                                totalMessageSize += size;
                                for (int i = 0; i < producerMessage.size(); i++) {
                                    SendResult sendResult = sendMQProducer.send(producerMessage.get(i));
                                    if (!sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                                        System.out.println("生产消息不成功：" + sendResult.getSendStatus());
                                    }
                                }
                            }
                        }
                        String key =StatisticsPool.constantFlag + "_" + adder;
                        Map<String, String> keyValue = new HashMap<>();
                        long endTime = System.currentTimeMillis();
                        longAdder.increment();
                        keyValue.put("执行次数",longAdder.intValue()+"");
                        keyValue.put("本次生产单条消息条数", totalMessageSize + "");
                        keyValue.put("本次生产单条消息耗时T1", (endTime - beginTime) + "ms");
                        keyValue.put("生产单条消息持续时间", (endTime - golbalBeginTime) + "ms");
//                        System.out.println("单条消息："+key);
                        try {
                            String json = objectMapper.writeValueAsString(keyValue);
                            statisticsPool.addTimeConsuming(key, json, StatisticsEnum.SINGLE_PRODUCER);
                        } catch (IOException ioException) {
                        }
                        globalCyclicBarrierLock.getGlobalCyclic().await();
                    }
                } catch (MQClientException mqClientException) {
                    System.out.println(mqClientException.getCause().getMessage());
                } catch (InterruptedException interruptedException) {
                    System.out.println(interruptedException.getCause().getMessage());
                } catch (RemotingException remotingException) {
                    System.out.println(remotingException.getCause().getMessage());
                } catch (MQBrokerException mqBrokerException) {
                    System.out.println(mqBrokerException.getCause().getMessage());
                } catch (BrokenBarrierException brokenBarrierException) {
                    System.out.println(brokenBarrierException.getCause().getMessage());
                }
            }
        });
    }
}
