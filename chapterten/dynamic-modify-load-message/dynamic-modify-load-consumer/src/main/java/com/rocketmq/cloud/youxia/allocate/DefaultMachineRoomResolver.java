package com.rocketmq.cloud.youxia.allocate;

import org.apache.rocketmq.client.consumer.rebalance.AllocateMachineRoomNearby;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.stereotype.Component;

@Component
public class DefaultMachineRoomResolver implements AllocateMachineRoomNearby.MachineRoomResolver {

    //①Broker Server名称规则“192.168.0.182-room1@broker-a”
    @Override
    public String brokerDeployIn(MessageQueue messageQueue) {
        //②解析出Broker Server中消息队列所在的机房名称
        return messageQueue.getBrokerName().split("@")[0];
    }

    //③clientID名称的规则“127.0.0.1:8989@loadBalanceTest1@192.168.0.182-room1”
    @Override
    public String consumerDeployIn(String clientID) {
        //④解析出消费者所在的机房名称“192.168.0.182-room1”
        return clientID.split("@")[2];
    }

}
