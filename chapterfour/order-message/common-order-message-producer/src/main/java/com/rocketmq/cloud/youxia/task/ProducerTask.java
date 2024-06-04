//package com.rocketmq.cloud.youxia.task;
//
//import com.rocketmq.cloud.youxia.config.ProducerConfig;
//import org.apache.commons.lang3.RandomUtils;
//import org.apache.rocketmq.client.exception.MQBrokerException;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.client.producer.MessageQueueSelector;
//import org.apache.rocketmq.client.producer.SendResult;
//import org.apache.rocketmq.common.message.Message;
//import org.apache.rocketmq.common.message.MessageQueue;
//import org.apache.rocketmq.remoting.common.RemotingHelper;
//import org.apache.rocketmq.remoting.exception.RemotingException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import java.io.UnsupportedEncodingException;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.LongAdder;
//
///**
// * 用多线程来模拟多进程，生产同一个订单的不同状态的消息，并生产到Broker Server中
// * Consumer使用顺序消息来消费消息，打印消息日志，并对比是否是有顺序的
// * 首先多个进程生产订单的不同状态的消息，肯定是有顺序的，比如创建订单的消息，肯定是在支付成功的消息之前，待发货的订单肯定是在支付成功
// * 的消息以后，模拟同一个订单的这三个订单状态，创建订单->支付成功->待发货
// */
////@Component
////@EnableScheduling
//public class ProducerTask {
//
//    @Autowired
//    private SimulateOrderManager simulateOrderManager;
//
//    private volatile LongAdder threaPooldNumAdder = new LongAdder();
//
//    private volatile List<ExecutorService> threadList=new CopyOnWriteArrayList<>();
//
//    private Map<String,DefaultMQProducer> cacheProducer=new ConcurrentHashMap<>();
//
//    @Autowired
//    private ProducerConfig producerConfig;
//
//    @Scheduled(cron = "*/5 * * * * ?")
//    public void producerMessage() throws MQClientException {
//        int threaPooldNum = producerConfig.getThreaPooldNum();
//        while (threaPooldNumAdder.intValue() < threaPooldNum) {
//            ExecutorService executorService = Executors.newFixedThreadPool(producerConfig.getClientNum());
//            for (int i = 0; i < producerConfig.getClientNum(); i++) {
//                //定义一个生产者对象mqProducer
//                DefaultMQProducer mqProducer = new DefaultMQProducer("testOrderMessage");
//                //设置Name Server的IP地址
//                mqProducer.setNamesrvAddr("127.0.0.1:9876");
//                mqProducer.setInstanceName("testOrderMessage" + RandomUtils.nextInt(0, 1000000000));
//                mqProducer.setClientIP("127.0.0.1:" + RandomUtils.nextInt(0, 1000000000));
//                //定义消息主题名称
//                String topic = "testOrderMessage";
//                //启动生产者
//                mqProducer.start();
//                executorService.execute(new SendMessageTask(mqProducer, topic));
//                cacheProducer.put(mqProducer.getInstanceName(), mqProducer);
//            }
//            threadList.add(executorService);
//            threaPooldNumAdder.increment();
//        }
//    }
//
//    class SendMessageTask implements Runnable {
//        private DefaultMQProducer defaultMQProducer;
//        private String topic;
//
//        public SendMessageTask(DefaultMQProducer defaultMQProducer, String topic) {
//            this.defaultMQProducer = defaultMQProducer;
//            this.topic = topic;
//        }
//
//        @Override
//        public void run() {
//            try {
//                while (true) {
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        System.out.println(e.getMessage());
//                    }
//                    producerOrderMessage(defaultMQProducer, topic);
//                }
//            } catch (MQClientException e1) {
//                System.out.println(e1.getMessage());
//            } catch (UnsupportedEncodingException e2) {
//                System.out.println(e2.getMessage());
//            } catch (RemotingException e3) {
//                System.out.println(e3.getMessage());
//            } catch (MQBrokerException e4) {
//                System.out.println(e4.getMessage());
//            } catch (InterruptedException e5) {
//                System.out.println(e5.getMessage());
//            }
//        }
//    }
//
//    void producerOrderMessage(DefaultMQProducer defaultMQProducer,String topic) throws MQClientException,
//            UnsupportedEncodingException, RemotingException, MQBrokerException, InterruptedException {
//        Long orderId = simulateOrderManager.randomId();
//        Message msg = new Message(topic, ("This is order message " + orderId).getBytes(RemotingHelper.DEFAULT_CHARSET));
//        //生产消息
//        SendResult sendResult = defaultMQProducer.send(msg, new MessageQueueSelector() {
//            @Override
//            public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
//                Long id = (Long) arg;
//                Long size = Long.valueOf(mqs.size());
//                //用选择器MessageQueueSelector，从下标为[0 7]之间选择一个值，并获取指定下标的消息队列
//                Long index = id % size;
//                return mqs.get(index.intValue());
//            }
//        }, orderId);
//    }
//}
