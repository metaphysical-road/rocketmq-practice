package com.rocketmq.cloud.youxia.constant;

public enum LoadBalanceStrategyEnum {

    AllocateMessageQueueAveragely(0, "基于平均Hash队列负载均衡策略"),
    AllocateMessageQueueAveragelyByCircle(1, "基于循环平均Hash队列负载均衡策略"),
    AllocateMessageQueueByConfig(2, "基于配置负载均衡策略"),
    AllocateMessageQueueByMachineRoom(3, "基于逻辑机房Hash队列负载均衡策略"),
    AllocateMessageQueueConsistentHash(4, "基于一致Hash队列负载均衡策略"),
    AllocateMachineRoomNearby(5, "基于机房邻近优先级负载均衡策略");

    private int key;
    private String value;

    LoadBalanceStrategyEnum(int key, String value) {
        this.key=key;
        this.value=value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
