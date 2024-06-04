package com.rocketmq.cloud.youxia.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.streams.core.RocketMQStream;
import org.apache.rocketmq.streams.core.function.ValueMapperAction;
import org.apache.rocketmq.streams.core.rstream.StreamBuilder;
import org.apache.rocketmq.streams.core.serialization.KeyValueSerializer;
import org.apache.rocketmq.streams.core.topology.TopologyBuilder;
import org.apache.rocketmq.streams.core.util.Pair;
import org.apache.rocketmq.streams.core.window.Time;
import org.apache.rocketmq.streams.core.window.WindowBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@EnableScheduling
@Component
public class WindowTask {

    private volatile boolean on=true;

    private volatile boolean on1=false;

    @Scheduled(fixedRate = 1000)
    public void doWindowTask() {
        if (on == false) {
            StreamBuilder builder = new StreamBuilder("WindowAvg");
            builder.source("avgSource", source -> {
                String value = new String(source, StandardCharsets.UTF_8);
                Integer num = Integer.parseInt(value);
                return new Pair<>(null, num);
            }).foreach(value -> System.out.println(String.format("time:%s, input:%d", LocalTime.now(), value)))
                    .filter(value -> value > 0)
                    .keyBy(value -> "key")
                    .window(WindowBuilder.tumblingWindow(Time.seconds(5)))
                    .avg()
                    .toRStream()
                    .print();

            TopologyBuilder topologyBuilder = builder.build();

            Properties properties = new Properties();
            properties.putIfAbsent(MixAll.NAMESRV_ADDR_PROPERTY, "192.168.0.182:9876");

            RocketMQStream rocketMQStream = new RocketMQStream(topologyBuilder, properties);
            Runtime.getRuntime().addShutdownHook(new Thread("WindowAvg-shutdown-hook") {
                @Override
                public void run() {
                    rocketMQStream.stop();
                }
            });
            rocketMQStream.start();
            on = true;
        }
    }

    @Scheduled(fixedRate = 1000)
    public void doWordCountTask() {
        if(on1==false) {
            StreamBuilder builder = new StreamBuilder("wordCount");

            builder.source("sourceTopic", total -> {
                String value = new String(total, StandardCharsets.UTF_8);
                return new Pair<>(null, value);
            })
                    .flatMap((ValueMapperAction<String, List<String>>) value -> {
                        String[] splits = value.toLowerCase().split("\\W+");
                        return Arrays.asList(splits);
                    })
                    .keyBy(value -> value)
                    .count()
                    .sink("wordCountSink", new KeyValueSerializer<String, Integer>() {
                        final ObjectMapper objectMapper = new ObjectMapper();

                        @Override
                        public byte[] serialize(String o, Integer data) throws Throwable {
                            ObjectNode objectNode = objectMapper.createObjectNode();
                            objectNode.put(o, data);

                            String result = objectNode.toPrettyString();
                            return objectMapper.writeValueAsBytes(result);
                        }
                    });

            builder.source("sourceTopic", total -> {
                String value = new String(total, StandardCharsets.UTF_8);
                return new Pair<>(null, value);
            })
                    .flatMap((ValueMapperAction<String, List<String>>) value -> {
                        String[] splits = value.toLowerCase().split("\\W+");
                        return Arrays.asList(splits);
                    })
                    .keyBy(value -> value)
                    .count()
                    .sink("wordCountSink", new KeyValueSerializer<String, Integer>() {
                        final ObjectMapper objectMapper = new ObjectMapper();

                        @Override
                        public byte[] serialize(String o, Integer data) throws Throwable {
                            ObjectNode objectNode = objectMapper.createObjectNode();
                            objectNode.put(o, data);

                            String result = objectNode.toPrettyString();
                            return objectMapper.writeValueAsBytes(result);
                        }
                    });
            TopologyBuilder topologyBuilder = builder.build();

            Properties properties = new Properties();
            properties.put(MixAll.NAMESRV_ADDR_PROPERTY, "192.168.0.182:9876");

            RocketMQStream rocketMQStream = new RocketMQStream(topologyBuilder, properties);
            Runtime.getRuntime().addShutdownHook(new Thread("wordcount-shutdown-hook") {
                @Override
                public void run() {
                    rocketMQStream.stop();
                }
            });
            rocketMQStream.start();
            on1=true;
        }
    }
}
