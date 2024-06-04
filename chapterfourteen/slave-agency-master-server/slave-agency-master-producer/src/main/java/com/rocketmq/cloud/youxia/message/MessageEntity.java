package com.rocketmq.cloud.youxia.message;

import lombok.Data;

import java.io.Serializable;

//@Data
public class MessageEntity implements Serializable {
    private Long uk;

    public Long getUk() {
        return uk;
    }

    public void setUk(Long uk) {
        this.uk = uk;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    private String messageId;

}
