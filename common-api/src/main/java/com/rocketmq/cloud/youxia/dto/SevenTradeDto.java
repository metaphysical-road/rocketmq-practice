package com.rocketmq.cloud.youxia.dto;

import lombok.Data;

import java.io.Serializable;
//@Data
public class SevenTradeDto implements Serializable {
    private Long uuid;
    private Long goodId;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }
}
