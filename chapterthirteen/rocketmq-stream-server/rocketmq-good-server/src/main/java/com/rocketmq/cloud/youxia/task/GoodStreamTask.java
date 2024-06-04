package com.rocketmq.cloud.youxia.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rocketmq.cloud.youxia.config.GoodConfig;
import com.rocketmq.cloud.youxia.function.GoodFunction;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.streams.core.RocketMQStream;
import org.apache.rocketmq.streams.core.function.ValueMapperAction;
import org.apache.rocketmq.streams.core.rstream.StreamBuilder;
import org.apache.rocketmq.streams.core.serialization.KeyValueSerializer;
import org.apache.rocketmq.streams.core.topology.TopologyBuilder;
import org.apache.rocketmq.streams.core.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
@EnableScheduling
public class GoodStreamTask {

    private volatile boolean on=false;

    @Autowired
    private GoodConfig goodConfig;

    @Scheduled(fixedRate = 1000)
    public void doWordCountTask() {
        if (goodConfig.getIsOpenStream().equals("true")&&on==false) {
            StreamBuilder builder = new StreamBuilder(goodConfig.getStreamJobId());
            final ObjectMapper objectMapper = new ObjectMapper();
            builder.source(goodConfig.getSourceTopicName(), total -> {
                String value = new String(total, StandardCharsets.UTF_8);
                return new Pair<>(null, value);
            }).flatMap((ValueMapperAction<String, List<String>>) value -> {
                String[] splits = value.toLowerCase().split("-");
                return Arrays.asList(splits);
            }).keyBy(value -> value)
                    .max(GoodFunction::getSaleNum).sink(goodConfig.getSinkTopicName(), new KeyValueSerializer<String, String>() {
                @Override
                public byte[] serialize(String o, String data) throws Throwable {
                    ObjectNode objectNode = objectMapper.createObjectNode();
                    objectNode.put(o, data);
                    String result = objectNode.toPrettyString();
                    return objectMapper.writeValueAsBytes(result);
                }
            });
            TopologyBuilder topologyBuilder = builder.build();
            Properties properties = new Properties();
            properties.put(MixAll.NAMESRV_ADDR_PROPERTY, goodConfig.getNamesrvAddr());

            RocketMQStream rocketMQStream = new RocketMQStream(topologyBuilder, properties);
            Runtime.getRuntime().addShutdownHook(new Thread("wordcount-shutdown-hook") {
                @Override
                public void run() {
                    rocketMQStream.stop();
                }
            });
            rocketMQStream.start();
            on = true;
        }
    }
}
