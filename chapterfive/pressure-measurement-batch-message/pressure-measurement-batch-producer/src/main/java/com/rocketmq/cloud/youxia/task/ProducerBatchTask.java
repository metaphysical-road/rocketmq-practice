package com.rocketmq.cloud.youxia.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmq.cloud.youxia.config.PressureMeasurementBatchConfig;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

@Component
@EnableScheduling
public class ProducerBatchTask {

    @Autowired
    private MessageCachePool messageCachePool;

    @Autowired
    private PressureMeasurementBatchConfig pressureMeasurementBatchConfig;

    private Map<String, DefaultMQProducer> producerMap=new ConcurrentHashMap<>();

    private volatile long golbalBeginTime=System.currentTimeMillis();

    @Autowired
    private GlobalCyclicBarrierLock globalCyclicBarrierLock;

    @Autowired
    private StatisticsPool statisticsPool;

    private volatile LongAdder longAdder=new LongAdder();

    @Autowired
    private ExecutorServiceCache executorServiceCache;

    private ObjectMapper objectMapper = new ObjectMapper();

    private volatile AtomicBoolean isFirstExecute=new AtomicBoolean(true);

    @Scheduled(fixedRate = 6000)
    public void producerBatchMessage() {
        executorServiceCache.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                if (pressureMeasurementBatchConfig.getOpenProducer().equals("true")) {
                    globalCyclicBarrierLock.getGlobalLock().lock();
                    Integer adder =0;
                    try {
                        if(statisticsPool.getIsIncrement().compareAndSet(false,true)){
                            statisticsPool.increment();
                            statisticsPool.getIsIncrement().set(true);
                        }
                        adder = statisticsPool.getLongAdder().intValue();
                    } finally {
                        globalCyclicBarrierLock.getGlobalLock().unlock();
                    }
                    long beginTime = System.currentTimeMillis();
                    if(isFirstExecute.compareAndSet(true,false)){
                        golbalBeginTime = System.currentTimeMillis();
                        isFirstExecute.set(false);
                    }
                    String topicName = pressureMeasurementBatchConfig.getTopicName();
                    String[] topicNameArrays = topicName.split(",");
                    Long totalMessageSize = 0L;
                    try {
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
                        Map<String, List<Message>> messagePool = messageCachePool.getBatchMessagePool();
                        for (String s : topicNameArrays) {
                            if (messagePool.containsKey(s)) {
                                DefaultMQProducer sendMQProducer = producerMap.get(s);
                                List<Message> producerMessage = messagePool.get(s);
                                Integer size = producerMessage.size();
                                totalMessageSize += size;
                                Integer start = 0;
                                Integer batchSize = pressureMeasurementBatchConfig.getBatchSize();
                                //求除数
                                Integer divisor = size / batchSize;
                                Integer divisorNum = 1;
                                Integer complementation = size % batchSize;
                                while (divisorNum <= divisor) {
                                    List<Message> sendMessageList = new CopyOnWriteArrayList<>();
                                    for (int i = start; i < divisorNum * batchSize; i++) {
                                        sendMessageList.add(producerMessage.get(i));
                                        start = i;
                                    }
                                    sendMQProducer.send(sendMessageList, 1000);
                                    divisorNum++;
                                }
                                if (complementation > 0) {
                                    List<Message> sendMessageList = new CopyOnWriteArrayList<>();
                                    for (int i = start; i < size; i++) {
                                        sendMessageList.add(producerMessage.get(i));
                                    }
                                    sendMQProducer.send(sendMessageList, 1000);
                                }
                            }
                        }
                        String key =StatisticsPool.constantFlag + "_" + adder;
                        Map<String, String> keyValue = new HashMap<>();
                        long endTime = System.currentTimeMillis();
                        long duration=endTime-golbalBeginTime;
                        longAdder.increment();
                        keyValue.put("执行次数",longAdder.intValue()+"");
                        keyValue.put("本次生产批量消息条数", totalMessageSize + "");
                        keyValue.put("本次生产批量消息耗时T2", (endTime - beginTime) + "ms");
                        keyValue.put("生产批量消息持续时间", duration + "ms");
                        try {
                            String json = objectMapper.writeValueAsString(keyValue);
                            statisticsPool.addTimeConsuming(key, json, StatisticsEnum.MULTI_PRODUCER);
                        } catch (IOException ioException) {
                        }
                        globalCyclicBarrierLock.getGlobalCyclic().await();
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
            }
        });
    }
}
