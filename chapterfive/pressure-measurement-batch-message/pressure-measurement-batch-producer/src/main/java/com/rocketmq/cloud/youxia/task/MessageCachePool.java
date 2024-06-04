package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.PressureMeasurementBatchConfig;
import org.apache.commons.collections.list.SynchronizedList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@EnableScheduling
public class MessageCachePool {

    private Map<String, List<Message>> messagePool = new ConcurrentHashMap<>();

    private Map<String, List<Message>> batchMessagePool = new ConcurrentHashMap<>();

    public Map<String, List<Message>> getMessagePool() {
        return messagePool;
    }

    public Map<String, List<Message>> getBatchMessagePool() {
        return batchMessagePool;
    }

    @Autowired
    private PressureMeasurementBatchConfig pressureMeasurementBatchConfig;

    @Scheduled(fixedRate = 200)
    public void generateData() throws UnsupportedEncodingException {
        Integer messageNum = pressureMeasurementBatchConfig.getMessageNum();
        String topicName = pressureMeasurementBatchConfig.getTopicName();
        String[] topicNameArray = topicName.split(",");
        if (topicNameArray.length > 0) {
            for (String s : topicNameArray) {
                packageData(messageNum, s, messagePool);
                packageData(messageNum, s, batchMessagePool);
            }
        }
    }

    private void packageData(Integer messageNum, String s, Map<String, List<Message>> batchMessagePool)
            throws UnsupportedEncodingException {
        if (batchMessagePool.containsKey(s)) {
            List<Message> messageList = batchMessagePool.get(s);
            Integer size = messageList.size();
            if (size < messageNum) {
                Integer add = messageNum - size;
                for (int i = 0; i < add; i++) {
                    String message = "this is a test message" + RandomUtils.nextLong(100, 1000000000);
                    Message msg = new Message(s, (message).getBytes(RemotingHelper.DEFAULT_CHARSET));
                    messageList.add(msg);
                }
            }
        } else {
            List<Message> messageList = SynchronizedList.decorate(new CopyOnWriteArrayList());
            batchMessagePool.put(s, messageList);
            for (int i = 0; i < messageNum; i++) {
                String message = "this is a test message" + RandomUtils.nextLong(100, 1000000000);
                Message msg = new Message(s, (message).getBytes(RemotingHelper.DEFAULT_CHARSET));
                messageList.add(msg);
            }
        }
    }
}
