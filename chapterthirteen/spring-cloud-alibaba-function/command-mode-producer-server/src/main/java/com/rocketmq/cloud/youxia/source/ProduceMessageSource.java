package com.rocketmq.cloud.youxia.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProduceMessageSource {
    @Output("output1")
    MessageChannel output1();
}
