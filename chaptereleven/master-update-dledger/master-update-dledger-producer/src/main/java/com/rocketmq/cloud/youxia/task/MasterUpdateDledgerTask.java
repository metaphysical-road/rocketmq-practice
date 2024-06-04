package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.MasterUpdateDledgerConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.list.SynchronizedList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@EnableScheduling
public class MasterUpdateDledgerTask {
    private List<DefaultMQProducer> defaultMQProducerList = new ArrayList<>();

    private Map<String, DefaultMQProducer> defaultMQProducerMap = new ConcurrentHashMap<>();

    private List<Message> arrayList=new CopyOnWriteArrayList<>();

    private Lock globalLock=new ReentrantLock();

    private volatile boolean isFirst=true;

    @Autowired
    private MasterUpdateDledgerConfig handleDledgerProducerConfig;

    @Scheduled(fixedRate = 1000)
    public void producerMessage() {
        globalLock.lock();
        try {
            String isSingleInstance = handleDledgerProducerConfig.getIsSingleInstance();
            //开启切换集群的开关
            if (handleDledgerProducerConfig.getChangeCluster().equals("true")) {
                if (CollectionUtils.isNotEmpty(defaultMQProducerList)) {
                    if (isFirst == true) {
                        for (DefaultMQProducer defaultMQProducer : defaultMQProducerList) {
                            defaultMQProducer.shutdown();
                        }
                        isFirst = false;
                        defaultMQProducerList.clear();
                        defaultMQProducerMap.clear();
                    }
                    startProducer(isSingleInstance);
                }
            }else{
                startProducer(isSingleInstance);
            }
            String moveMessageSuccess = handleDledgerProducerConfig.getMoveMessageSuccess();
            if (CollectionUtils.isNotEmpty(defaultMQProducerList)) {
                DefaultMQProducer random = defaultMQProducerList.get(RandomUtils.nextInt(0, defaultMQProducerList.size()));
                random.getDefaultMQProducerImpl().getTopicPublishInfoTable();
                if (handleDledgerProducerConfig.getSendLatencyFaultEnable().equals("true")) {
                    random.setSendLatencyFaultEnable(true);
                }
                try {
                    //生产消息
                    String topic = handleDledgerProducerConfig.getTopic();
                    //构造消息体
                    Message msg = new Message(topic,
                            ("this is a test message" + RandomUtils.nextLong(1, 20000000)).
                                    getBytes(RemotingHelper.DEFAULT_CHARSET));
                    //开启延迟
                    msg.putUserProperty("delay", handleDledgerProducerConfig.getDelay());
                    //开启延迟时间
                    msg.putUserProperty("delayTime", handleDledgerProducerConfig.getDelayTime());
                    //表示已经切换到多副本架构集群
                    if (handleDledgerProducerConfig.getChangeCluster().equals("true")&&
                            StringUtils.isNotEmpty(moveMessageSuccess) && moveMessageSuccess.equals("false")) {
                        System.out.println("正在进行数据迁移，将消息缓存在本地缓存中！");
                        arrayList.add(msg);
                        return;
                    }
                    if(handleDledgerProducerConfig.getChangeCluster().equals("true")&&
                            StringUtils.isNotEmpty(moveMessageSuccess) && moveMessageSuccess.equals("true")){
                        if (CollectionUtils.isNotEmpty(arrayList)) {
                            for (Message message : arrayList) {
                                random.send(message);
                            }
                        }
                        arrayList.clear();
                    }
                    SendResult result = random.send(msg);
                    if (result.getSendStatus().equals(SendStatus.SEND_OK)) {
                        System.out.println("生产消息成功，消息ID为:" + result.getMsgId());
                    }
                } catch (UnsupportedEncodingException e1) {
                    System.out.println(e1.getMessage());
                } catch (MQClientException e2) {
                    System.out.println(e2.getMessage());
                } catch (RemotingException e3) {
                    System.out.println(e3.getMessage());
                } catch (MQBrokerException e4) {
                    System.out.println(e4.getMessage());
                } catch (InterruptedException e5) {
                    System.out.println(e5.getMessage());
                }
            }
        } finally {
            globalLock.unlock();
        }
    }

    private void startProducer(String isSingleInstance) {
        if (isSingleInstance.equals("false")) {
            String instanceName = handleDledgerProducerConfig.getInstanceName();
            String[] instanceNameList = instanceName.split(",");
            if (instanceNameList.length > 0) {
                for (String s : instanceNameList) {
                    if (!defaultMQProducerMap.containsKey(s)) {
                        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(handleDledgerProducerConfig.getProducerGroup());
                        if(handleDledgerProducerConfig.getChangeCluster().equals("true")){
                            defaultMQProducer.setNamesrvAddr(handleDledgerProducerConfig.getNewNamesrvAddr());
                        }else {
                            defaultMQProducer.setNamesrvAddr(handleDledgerProducerConfig.getNamesrvAddr());
                        }
                        defaultMQProducer.setInstanceName(s);
                        defaultMQProducer.setClientIP(handleDledgerProducerConfig.getClientIp());
                        try {
                            defaultMQProducer.start();
                            defaultMQProducerList.add(defaultMQProducer);
                            defaultMQProducerMap.put(s, defaultMQProducer);
                        } catch (MQClientException e) {
                            System.out.println(e.getCause().getMessage());
                        }
                    }
                }
            }
        }else {
            String singleInstanceName = handleDledgerProducerConfig.getSingleInstanceName();
            String multiProducerGroup = handleDledgerProducerConfig.getMultiProducerGroup();
            String[] multiProducerGroupArray = multiProducerGroup.split(",");
            for (String groupName : multiProducerGroupArray) {
                if (!defaultMQProducerMap.containsKey(groupName)) {
                    DefaultMQProducer defaultMQProducer = new DefaultMQProducer(groupName);
                    if(handleDledgerProducerConfig.getChangeCluster().equals("true")){
                        defaultMQProducer.setNamesrvAddr(handleDledgerProducerConfig.getNewNamesrvAddr());
                    }else {
                        defaultMQProducer.setNamesrvAddr(handleDledgerProducerConfig.getNamesrvAddr());
                    }
                    defaultMQProducer.setInstanceName(singleInstanceName);
                    defaultMQProducer.setClientIP(handleDledgerProducerConfig.getClientIp());
                    try {
                        defaultMQProducer.start();
                        defaultMQProducerList.add(defaultMQProducer);
                        defaultMQProducerMap.put(groupName, defaultMQProducer);
                    } catch (MQClientException e) {
                        System.out.println(e.getCause().getMessage());
                    }
                }
            }
        }
    }
}
