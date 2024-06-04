package com.rocketmq.cloud.youxia.config;

import com.rocketmq.cloud.youxia.dispatch.ConsumerAsyncTraceDispatcher;
import org.apache.rocketmq.client.trace.TraceDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DispatchConfig {

    @Autowired
    private CustomHookConfig customHookConfig;

    @Bean
    public ConsumerAsyncTraceDispatcher traceDispatcher() {
        ConsumerAsyncTraceDispatcher traceDispatcher = new
                ConsumerAsyncTraceDispatcher(customHookConfig.getConsumerGroup(),
                TraceDispatcher.Type.CONSUME, customHookConfig.getTraceTopic(), null);
        return traceDispatcher;
    }
}
