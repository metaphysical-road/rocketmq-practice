package com.rocketmq.cloud.youxia.task;

import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.config.AlertManagerProducerConfig;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableScheduling
@Component
public class ProducerTask {

    @Autowired
    private AlertManagerProducerConfig alertManagerProducerConfig;

    private Map<String,DefaultMQProducer> cacheProducer=new ConcurrentHashMap<>();

    private ExecutorService executorService=Executors.newFixedThreadPool(10);

    @Scheduled(fixedRate = 1000)
    public void producerMessage() throws MQClientException,InterruptedException, UnsupportedEncodingException,
            RemotingException, MQBrokerException {
        String onlineProducerInstanceName=alertManagerProducerConfig.getOnlineProducerInstanceName();
        String offlineProducerInstanceName=alertManagerProducerConfig.getOfflineProducerInstanceName();
        if(StringUtils.isNotEmpty(onlineProducerInstanceName)){
            String[] instanceNameArray=null;
            if(onlineProducerInstanceName.contains(",")){
                instanceNameArray=onlineProducerInstanceName.split(",");
            }else{
                instanceNameArray=new String[]{onlineProducerInstanceName};
            }
            for(String s:instanceNameArray){
                if(!cacheProducer.containsKey(s)){
                    //定义一个生产者对象mqProducer
                    DefaultMQProducer defaultMQProducer = new DefaultMQProducer(alertManagerProducerConfig.getProducerGroup());
                    //设置Name Server的IP地址
                    defaultMQProducer.setNamesrvAddr(alertManagerProducerConfig.getNameAddress());
                    defaultMQProducer.setInstanceName(s);
                    defaultMQProducer.setClientIP(alertManagerProducerConfig.getClientIp());
                    //启动生产者
                    defaultMQProducer.start();
                    cacheProducer.put(s,defaultMQProducer);
                }
            }
        }
        if(StringUtils.isNotEmpty(offlineProducerInstanceName)&&alertManagerProducerConfig.getOfflineProducer().equals("true")) {
            String[] instanceNameArray = null;
            if (onlineProducerInstanceName.contains(",")) {
                instanceNameArray = offlineProducerInstanceName.split(",");
            } else {
                instanceNameArray = new String[]{offlineProducerInstanceName};
            }
            for (String s : instanceNameArray) {
                if (cacheProducer.containsKey(s)) {
                    cacheProducer.remove(s);
                }
            }
        }

        if(cacheProducer.size()>0) {
            Iterator<String> keyIterator = cacheProducer.keySet().iterator();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                executorService.execute(new ProducerThread(key));
            }
        }
    }

    class ProducerThread implements Runnable{
        private String key;

        public ProducerThread(String key){
            this.key=key;
        }
        @Override
        public void run() {
            try {
                DefaultMQProducer defaultMQProducer = cacheProducer.get(key);
                Integer batchSize = alertManagerProducerConfig.getBatchSize();
                long sleepTime = alertManagerProducerConfig.getSleepTime();
                for (int i = 0; i < batchSize; i++) {
                    //定义消息主题名称
                    String topic = alertManagerProducerConfig.getTopic();
                    //构造消息体
                    Message msg = new Message(topic,
                            ("this is a test message" + RandomUtils.nextLong(1, 20000000)).
                                    getBytes(RemotingHelper.DEFAULT_CHARSET));
                    SendResult result = defaultMQProducer.send(msg);
                }
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }catch (InterruptedException e1){}
            catch (UnsupportedEncodingException e2){}
            catch (MQClientException e3){}
            catch (RemotingException e4){}
            catch (MQBrokerException e5){}
        }
    }
}
