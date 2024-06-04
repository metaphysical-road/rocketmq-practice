package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.ProducerConfig;
import org.apache.commons.collections.list.SynchronizedList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用多线程来模拟多进程，生产同一个订单的不同状态的消息，并生产到Broker Server中
 * Consumer使用顺序消息来消费消息，打印消息日志，并对比是否是有顺序的
 * 首先多个进程生产订单的不同状态的消息，肯定是有顺序的，比如创建订单的消息，肯定是在支付成功的消息之前，待发货的订单肯定是在支付成功
 * 的消息以后，模拟同一个订单的这三个订单状态，创建订单->支付成功->待发货->已发货->已收货
 */
@Component
@EnableScheduling
public class SimulatingMultithreadedOrder {

    private Map<String, DefaultMQProducer> cacheProducer=new ConcurrentHashMap<>();

    private volatile AtomicBoolean isFirst=new AtomicBoolean(false);

    @Autowired
    private SimulateOrderManager simulateOrderManager;

    private volatile LongAdder producerClientNumAdder = new LongAdder();

    private Lock lock=new ReentrantLock();

    List<DefaultMQProducer> stringList =SynchronizedList.decorate(new CopyOnWriteArrayList());

    @Autowired
    private ProducerConfig producerConfig;

    @Scheduled(fixedRate = 200)
    public void producerMessage() throws MQClientException {
        lock.lock();
        try {
            LongAdder orderStatus = new LongAdder();
            if (cacheProducer.size() == 0 && isFirst.equals(false)) {
                for (int i = 0; i < producerConfig.getClientNum(); i++) {
                    String instanceName="testCommonOrderMessage" + RandomUtils.nextLong(100, 1000000000);
                    while (cacheProducer.containsKey(instanceName)){
                        instanceName="testCommonOrderMessage" + RandomUtils.nextLong(900000, 1000000000);
                    }
                    DefaultMQProducer mqProducer = new DefaultMQProducer("testCommonOrderMessage");
                    //设置Name Server的IP地址
                    mqProducer.setNamesrvAddr("127.0.0.1:9876");
                    mqProducer.setInstanceName(instanceName);
                    mqProducer.setClientIP("127.0.0.1:100" + i);
                    //启动生产者
                    mqProducer.start();
                    cacheProducer.put(mqProducer.getInstanceName(), mqProducer);
                    stringList.add(mqProducer);
                }
                isFirst.set(true);
            } else if (cacheProducer.size() < producerConfig.getClientNum()) {
                int add = producerConfig.getClientNum() - cacheProducer.size();
                for (int i = 0; i < producerConfig.getClientNum(); i++) {
                    String instanceName="testCommonOrderMessage" + RandomUtils.nextLong(100, 1000000000);
                    while (cacheProducer.containsKey(instanceName)){
                        instanceName="testCommonOrderMessage" + RandomUtils.nextLong(900000, 1000000000);
                    }
                    DefaultMQProducer mqProducer = new DefaultMQProducer("testCommonOrderMessage");
                    //设置Name Server的IP地址
                    mqProducer.setNamesrvAddr("127.0.0.1:9876");
                    mqProducer.setInstanceName(instanceName);
                    mqProducer.setClientIP("127.0.0.1:300" + cacheProducer.size() + i);
                    //启动生产者
                    mqProducer.start();
                    cacheProducer.put(mqProducer.getInstanceName(), mqProducer);
                    stringList.add(mqProducer);
                }
            }
            //模拟同一个订单的这五个订单状态，创建订单->支付成功->待发货->已发货->已收货
            final Long orderId = simulateOrderManager.randomId();
            int producerNum=0;
            while (producerClientNumAdder.intValue() < producerConfig.getClientNum()) {
                String messageType = "";
                if (orderStatus.intValue() == 0) {
                    messageType = "创建订单";
                } else if (orderStatus.intValue() == 1) {
                        messageType = "支付成功";
                } else if (orderStatus.intValue() == 2) {
                    messageType = "待发货";
                } else if (orderStatus.intValue() == 3) {
                    messageType = "已发货";
                } else if (orderStatus.intValue() == 4) {
                    messageType = "已收货";
                }
                //定义消息主题名称
                String topic = "testCommonOrderMessage";

                Thread thread = new Thread(new SendMessageWorker(stringList.get(producerNum), topic, orderId, messageType));
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                producerClientNumAdder.increment();
                orderStatus.increment();
                producerNum++;
            }
            System.out.println("结束本次定时任务");
            producerClientNumAdder.reset();
        }finally {
            lock.unlock();
        }
    }
}
