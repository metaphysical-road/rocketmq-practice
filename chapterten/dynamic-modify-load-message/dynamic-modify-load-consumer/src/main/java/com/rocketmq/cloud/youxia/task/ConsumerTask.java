package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.allocate.DefaultMachineRoomResolver;
import com.rocketmq.cloud.youxia.config.DynamicModifyLoadConfig;
import com.rocketmq.cloud.youxia.constant.LoadBalanceStrategyEnum;
import com.rocketmq.cloud.youxia.listener.IMessageListenerConcurrently;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
public class ConsumerTask {

    private List<DefaultMQPushConsumer> defaultMQPushConsumerList = new ArrayList<>();

    private Map<String, DefaultMQPushConsumer> defaultMQPushConsumerMap = new ConcurrentHashMap<>();

    @Autowired
    private DynamicModifyLoadConfig multiProcessConcurrentConfig;

    @Scheduled(fixedRate = 1000)
    public void consumeMessage() {
        String isModifyLoadBalanceStrategy=multiProcessConcurrentConfig.getIsModifyLoadBalanceStrategy();
        if(isModifyLoadBalanceStrategy.equals("true")){
            if(CollectionUtils.isNotEmpty(defaultMQPushConsumerList)){
                for (DefaultMQPushConsumer item :defaultMQPushConsumerList) {
                    item.shutdown();
                }
            }
            defaultMQPushConsumerList.clear();
            defaultMQPushConsumerMap.clear();
        }
        startNewConsumer();
        //下线消费者客户端
        if(multiProcessConcurrentConfig.getIsOffline().equals("true")){
            String offlineInstance=multiProcessConcurrentConfig.getOfflineInstance();
            String[] offlineInstanceArray=null;
            if(offlineInstance.contains(",")){
                offlineInstanceArray=offlineInstance.split(",");
            }else{
                offlineInstanceArray=new String[]{offlineInstance};
            }
            for(String s:offlineInstanceArray){
                if(defaultMQPushConsumerMap.containsKey(s)){
                    DefaultMQPushConsumer item=defaultMQPushConsumerMap.get(s);
                    item.shutdown();
                    defaultMQPushConsumerMap.remove(s);
;                   defaultMQPushConsumerList.remove(item);
                }
            }
        }
    }

    private void startNewConsumer() {
        String instanceName = multiProcessConcurrentConfig.getInstanceName();
        String[] instanceNameArray = instanceName.split(",");
        for (String s : instanceNameArray) {
            try {
                if (!defaultMQPushConsumerMap.containsKey(s)) {
                    if (multiProcessConcurrentConfig.getIsOffline()
                            .equals("true") && multiProcessConcurrentConfig.getOfflineInstance().contains(s)) {
                        //跳过
                    } else {
                        start(s);
                    }
                }
            } catch (MQClientException e) {
                System.out.println(e.getCause().getMessage());
            }
        }
    }

    private void start(String item) throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = null;
        defaultMQPushConsumer = new DefaultMQPushConsumer(multiProcessConcurrentConfig.getConsumerGroup());
        defaultMQPushConsumer.setInstanceName(item);
        defaultMQPushConsumer.setUnitName(multiProcessConcurrentConfig.getUnitName());
        defaultMQPushConsumer.setConsumerGroup(multiProcessConcurrentConfig.getConsumerGroup());
        defaultMQPushConsumer.setClientIP(multiProcessConcurrentConfig.getClientIp());
        defaultMQPushConsumer.subscribe(multiProcessConcurrentConfig.getTopic(), "");
        defaultMQPushConsumer.setNamesrvAddr(multiProcessConcurrentConfig.getNamesrvAddr());
        //设置消费消息的线程池的最小核心线程数
        defaultMQPushConsumer.setConsumeThreadMin(multiProcessConcurrentConfig.getConsumerThreadMin());
        //设置消费消息的线程池的最大核心线程数
        defaultMQPushConsumer.setConsumeThreadMax(multiProcessConcurrentConfig.getConsumerThreadMax());
        defaultMQPushConsumer.registerMessageListener(new IMessageListenerConcurrently());
        //设置负载均衡器
        if (multiProcessConcurrentConfig.getLoadBalanceStrategy().intValue()==
                LoadBalanceStrategyEnum.AllocateMessageQueueAveragely.getKey()) {
            defaultMQPushConsumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragely());
        } else if (multiProcessConcurrentConfig.getLoadBalanceStrategy().intValue()==
                LoadBalanceStrategyEnum.AllocateMessageQueueAveragelyByCircle.getKey()) {
            defaultMQPushConsumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragelyByCircle());
        } else if (multiProcessConcurrentConfig.getLoadBalanceStrategy().intValue()==
                LoadBalanceStrategyEnum.AllocateMessageQueueConsistentHash.getKey()) {
            Integer num=multiProcessConcurrentConfig.getVirtualNodeCnt();
            defaultMQPushConsumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueConsistentHash(num));
        } else if (multiProcessConcurrentConfig.getLoadBalanceStrategy().intValue()==
                LoadBalanceStrategyEnum.AllocateMessageQueueByMachineRoom.getKey()) {
            AllocateMessageQueueByMachineRoom allocateMessageQueueStrategy=new AllocateMessageQueueByMachineRoom();
            Set<String> consumeridcs=new HashSet<>();
            consumeridcs.add(multiProcessConcurrentConfig.getMachineName());
            allocateMessageQueueStrategy.setConsumeridcs(consumeridcs);
            defaultMQPushConsumer.setAllocateMessageQueueStrategy(allocateMessageQueueStrategy);
        } else if (multiProcessConcurrentConfig.getLoadBalanceStrategy().intValue()==
                LoadBalanceStrategyEnum.AllocateMachineRoomNearby.getKey()) {
            //如果是基于机房邻近优先级负载均衡策略，则需要再次定义真正的负载均衡算法
            if (multiProcessConcurrentConfig.getMachineRoomNearbyType().intValue()==
                    LoadBalanceStrategyEnum.AllocateMessageQueueByMachineRoom.getKey()) {
                AllocateMessageQueueByMachineRoom allocateMessageQueueStrategy=new AllocateMessageQueueByMachineRoom();
                Set<String> consumeridcs=new HashSet<>();
                consumeridcs.add(multiProcessConcurrentConfig.getMachineName());
                allocateMessageQueueStrategy.setConsumeridcs(consumeridcs);
                defaultMQPushConsumer.setAllocateMessageQueueStrategy(new AllocateMachineRoomNearby(
                        allocateMessageQueueStrategy, new DefaultMachineRoomResolver()
                ));
            } else if (multiProcessConcurrentConfig.getMachineRoomNearbyType().intValue()==
                    LoadBalanceStrategyEnum.AllocateMessageQueueConsistentHash.getKey()) {
                Integer num=multiProcessConcurrentConfig.getVirtualNodeCnt();
                defaultMQPushConsumer.setAllocateMessageQueueStrategy(new AllocateMachineRoomNearby(
                        new AllocateMessageQueueConsistentHash(num), new DefaultMachineRoomResolver()
                ));
            } else if (multiProcessConcurrentConfig.getMachineRoomNearbyType().intValue()==
                    LoadBalanceStrategyEnum.AllocateMessageQueueAveragely.getKey()) {
                defaultMQPushConsumer.setAllocateMessageQueueStrategy(new AllocateMachineRoomNearby(
                        new AllocateMessageQueueAveragely(), new DefaultMachineRoomResolver()
                ));
            } else if (multiProcessConcurrentConfig.getMachineRoomNearbyType().intValue()==
                    LoadBalanceStrategyEnum.AllocateMessageQueueAveragelyByCircle.getKey()) {
                defaultMQPushConsumer.setAllocateMessageQueueStrategy(new AllocateMachineRoomNearby(
                        new AllocateMessageQueueAveragelyByCircle(), new DefaultMachineRoomResolver()
                ));
            } else if (multiProcessConcurrentConfig.getMachineRoomNearbyType().intValue()==
                    LoadBalanceStrategyEnum.AllocateMessageQueueByConfig.getKey()) {
                AllocateMessageQueueByConfig allocateMessageQueueByConfig = getAllocateMessageQueueByConfig();
                defaultMQPushConsumer.setAllocateMessageQueueStrategy(new AllocateMachineRoomNearby(
                        allocateMessageQueueByConfig, new DefaultMachineRoomResolver()
                ));
            }
        } else if (multiProcessConcurrentConfig.getLoadBalanceStrategy().intValue()==
                LoadBalanceStrategyEnum.AllocateMessageQueueByConfig.getKey()) {
            AllocateMessageQueueByConfig allocateMessageQueueByConfig = getAllocateMessageQueueByConfig();
            defaultMQPushConsumer.setAllocateMessageQueueStrategy(allocateMessageQueueByConfig);
        }
        defaultMQPushConsumer.start();
        defaultMQPushConsumerList.add(defaultMQPushConsumer);
        defaultMQPushConsumerMap.put(defaultMQPushConsumer.getInstanceName(), defaultMQPushConsumer);
    }

    private AllocateMessageQueueByConfig getAllocateMessageQueueByConfig() {
        AllocateMessageQueueByConfig allocateMessageQueueByConfig = new AllocateMessageQueueByConfig();
        //从配置中心获取消息队列
        List<MessageQueue> messageQueueList = new ArrayList<>();
        String messageQueueAndBrokerName = multiProcessConcurrentConfig.getMessageQueueAndBrokerName();
        String[] messageQueueAndBrokerNameArray;
        if (messageQueueAndBrokerName.contains(",")) {
            messageQueueAndBrokerNameArray = messageQueueAndBrokerName.split(",");
        } else {
            messageQueueAndBrokerNameArray = new String[]{messageQueueAndBrokerName};
        }
        String topicName = multiProcessConcurrentConfig.getTopic();
        for (String s : messageQueueAndBrokerNameArray) {
            String[] item = s.split(":");
            MessageQueue messageQueue = new MessageQueue();
            messageQueue.setQueueId(Integer.valueOf(item[0]));
            messageQueue.setBrokerName(item[1]);
            messageQueue.setTopic(topicName);
            messageQueueList.add(messageQueue);
        }
        allocateMessageQueueByConfig.setMessageQueueList(messageQueueList);
        return allocateMessageQueueByConfig;
    }
}
