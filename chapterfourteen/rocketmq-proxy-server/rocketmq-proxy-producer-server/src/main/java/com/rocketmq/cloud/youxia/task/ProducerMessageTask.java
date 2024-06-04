package com.rocketmq.cloud.youxia.task;

import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@EnableScheduling
public class ProducerMessageTask {

    private List<Producer> producerList=new ArrayList<>();

    private List<ClientServiceProvider> providerList=new ArrayList<>();

    @Scheduled(fixedRate = 10000)
    public void producerMessage() {
        try {
            String topic = "yourTopic";
            if (CollectionUtils.isEmpty(producerList) &&
                    CollectionUtils.isEmpty(providerList)) {
                ClientServiceProvider provider = ClientServiceProvider.loadService();
                String accessKey = "yourAccessKey";
                String secretKey = "yourSecretKey";
                SessionCredentialsProvider sessionCredentialsProvider =
                        new StaticSessionCredentialsProvider(accessKey, secretKey);

                String endpoints = "192.168.0.182:8081";
                ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                        .setEndpoints(endpoints)
                        .setCredentialProvider(sessionCredentialsProvider)
                        .build();
                Producer producer = provider.newProducerBuilder()
                        .setClientConfiguration(clientConfiguration)
                        .setTopics(topic)
                        .build();
                producerList.add(producer);
                providerList.add(provider);
            }
            byte[] body = "This is a normal message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
            String tag = "yourMessageTagA";
            final Message message = providerList.get(0).newMessageBuilder()
                    .setTopic(topic)
                    .setTag(tag)
                    .setKeys("yourMessageKey-0e094a5f9d85")
                    .setBody(body)
                    .build();
            final CompletableFuture<SendReceipt> future = producerList.get(0).sendAsync(message);
            ExecutorService sendCallbackExecutor = Executors.newCachedThreadPool();
            future.whenCompleteAsync((sendReceipt, throwable) -> {
                if (null != throwable) {
                    System.out.println("Failed to send message:" + throwable.getMessage());
                    return;
                }
                System.out.println(sendReceipt.getMessageId());
            }, sendCallbackExecutor);
            Thread.sleep(Long.MAX_VALUE);
        } catch (ClientException e1) {
            System.out.println(e1.getMessage());
        } catch (InterruptedException e3) {
            System.out.println(e3.getMessage());
        } catch (Exception e2){
            System.out.println(e2.getMessage());
        }
    }
}
