package com.rocketmq.cloud.youxia.dto;

import lombok.Data;

//@Data
public class CreateBeforeOrderMessageDTO {
    private Long uuid;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }
}
