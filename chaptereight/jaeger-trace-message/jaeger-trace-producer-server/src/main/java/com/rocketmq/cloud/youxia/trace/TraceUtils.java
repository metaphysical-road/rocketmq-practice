package com.rocketmq.cloud.youxia.trace;

import com.rocketmq.cloud.youxia.config.ProducerConfig;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TraceUtils {

    @Autowired
    private ProducerConfig producerConfig;

    public Tracer initTracer() {
        Configuration.SamplerConfiguration samplerConfig = Configuration.
                SamplerConfiguration.fromEnv().withType(ConstSampler.TYPE).
                withParam(1);
        Configuration.SenderConfiguration senderConfiguration=Configuration.
                SenderConfiguration.fromEnv().withAgentHost(producerConfig.getAgentHost()).
                withAgentPort(producerConfig.getAgentPort()).withEndpoint(producerConfig.getEndpoint());
        Configuration.ReporterConfiguration reporterConfig = Configuration.
                ReporterConfiguration.fromEnv().withLogSpans(true).withSender(senderConfiguration);
        Configuration config = new Configuration(producerConfig.getServiceName()).withSampler(
                samplerConfig).withReporter(reporterConfig);
        GlobalTracer.registerIfAbsent(config.getTracer());
        return config.getTracer();
    }
}
