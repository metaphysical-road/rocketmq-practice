package com.rocketmq.cloud.youxia.task;

import lombok.Data;
import org.apache.rocketmq.common.message.Message;
//@Data
public class SendMessageTask {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
