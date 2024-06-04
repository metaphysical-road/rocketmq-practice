package com.rocketmq.cloud.youxia.config;

import com.rocketmq.cloud.youxia.listener.DeadQueueMessageListener;
import com.rocketmq.cloud.youxia.listener.IMessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean(name = "deadQueueMessageListener")
    public MessageListenerConcurrently initBean1() {
        return new DeadQueueMessageListener();
    }

    @Bean(name = "iMessageListenerConcurrently")
    public MessageListenerConcurrently initBean2() {
        return new IMessageListenerConcurrently();
    }
}
