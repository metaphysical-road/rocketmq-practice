package com.rocketmq.cloud.youxia.config;

import com.rocketmq.cloud.youxia.dispatch.ProducerAsyncTraceDispatcher;
import org.apache.rocketmq.client.trace.TraceDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class DispatchConfig {
    @Autowired
    private CustomHookConfig customHookConfig;

    @Bean
    public ProducerAsyncTraceDispatcher traceDispatcher() {
        ProducerAsyncTraceDispatcher traceDispatcher = new
                ProducerAsyncTraceDispatcher(customHookConfig.getProducerGroup(),
                TraceDispatcher.Type.PRODUCE, customHookConfig.getTraceTopic(), null);
        return traceDispatcher;
    }
}

