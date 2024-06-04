package com.rocketmq.cloud.youxia.source;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ConsumerMessageSink {
    @Input("input1")
    SubscribableChannel input1();
}
