package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.LiveGiftConfig;
import com.rocketmq.cloud.youxia.transactionlistener.GiftTransactionListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.list.SynchronizedList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@EnableScheduling
public class ProducerCachePool {

    @Autowired
    private LiveGiftConfig liveGiftConfig;

    private static List<TransactionMQProducer> producerList=SynchronizedList.decorate(new CopyOnWriteArrayList());

    private volatile LongAdder longAdder=new LongAdder();

    private volatile AtomicInteger lastLiminateNum=new AtomicInteger(0);

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Lock readLock=lock.readLock();

    private final Lock writeLock=lock.writeLock();

    @Autowired
    private GiftTransactionListener giftTransactionListener;

    //默认启动三个生产者
    public void putProducer(String producerGroup,String instanceName,String clientID) throws MQClientException {
        TransactionMQProducer transactionMQProducer = new TransactionMQProducer(producerGroup);
        transactionMQProducer.setTransactionListener(giftTransactionListener);
        transactionMQProducer.setInstanceName(instanceName);
        transactionMQProducer.setClientIP(clientID);
        transactionMQProducer.setNamesrvAddr("127.0.0.1:9876");
        transactionMQProducer.start();
        producerList.add(transactionMQProducer);
    }

    public synchronized TransactionMQProducer randomProducer() {
        Integer index = RandomUtils.nextInt(0, producerList.size());
        return producerList.get(index);
    }

    @Scheduled(fixedRate = 3000)
    public void fixCreateProducer() {
        if(!liveGiftConfig.getOpenEliminate().equals(true)) {
            writeLock.lock();
            try {
                //num为总的Producer客户端的个数，如果不开启缩容，则内存中就会存在num个Producer客户端
                Integer num = liveGiftConfig.getProducerNum();
                String instanceName=liveGiftConfig.getInstanceName();
                String[] instanceNameArray=instanceName.split(",");
                String clientIp=liveGiftConfig.getClientIp();
                String[] clientIpArray=clientIp.split(",");
                while (longAdder.intValue() < num) {
                    Integer add = num - longAdder.intValue();
                    if (add > 0) {
                        for (int i = longAdder.intValue(); i < num; i++) {
                            try {
                                String producerGroup = "giftMessage";
                                putProducer(producerGroup,instanceNameArray[i],clientIpArray[i]);
                            } catch (MQClientException e) {
                                System.out.println(e.getCause().getMessage());
                            }
                            longAdder.increment();
                        }
                    }
                }
            } finally {
                writeLock.unlock();
            }
        }
    }

    @Scheduled(fixedRate = 3000)
    public void printProducer() {
        readLock.lock();
        try {
            if (CollectionUtils.isNotEmpty(producerList)) {
                StringBuilder sb = new StringBuilder();
                for (TransactionMQProducer item : producerList) {
                    if (sb.toString().equals("")) {
                        sb.append(item.getDefaultMQProducerImpl().getmQClientFactory().getClientId());
                    } else {
                        sb.append(";").append(item.getDefaultMQProducerImpl().getmQClientFactory().getClientId());
                    }
                }
                System.out.println("当前存活的事务消息生产者有：" + sb.toString());
            }
        } finally {
            readLock.unlock();
        }
    }

    @Scheduled(fixedRate = 5000)
    public void fixliminateProducer() {
        writeLock.lock();
        try {
            if (liveGiftConfig.getOpenEliminate().equals("true")) {
                Integer num = liveGiftConfig.getProducerNum();
                Integer liminateNum = liveGiftConfig.getEliminateNum();
                if (num < liminateNum) {
                    System.out.println("liminateNum不能大于num");
                    return;
                }
                String eliminateProducerClientId = liveGiftConfig.getEliminateProducerClientId();
                String[] eliminateProducerClientIdArray=null;
                if(eliminateProducerClientId.contains(",")){
                    eliminateProducerClientIdArray = eliminateProducerClientId.split(",");
                }else {
                    eliminateProducerClientIdArray=new String[]{eliminateProducerClientId};
                }
                List<TransactionMQProducer> cloneProducerList=new CopyOnWriteArrayList<>();
                for(String s:eliminateProducerClientIdArray){
                    for(int i=0;i<producerList.size();i++){
                        if(!producerList.get(i).getDefaultMQProducerImpl().
                                getmQClientFactory().getClientId().equals(s)) {
                            cloneProducerList.add(producerList.get(i));
                        }else{
                            producerList.get(i).shutdown();
                        }
                    }
                }
                producerList=cloneProducerList;
            }
        } finally {
            writeLock.unlock();
        }
    }

}
