package com.rocketmq.cloud.youxia.task;

public enum StatisticsEnum {
    SINGLE_PRODUCER("singleProducer", "0"),
    MULTI_PRODUCER("multiProducer", "1");

    private String key;
    private String value;

    StatisticsEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
