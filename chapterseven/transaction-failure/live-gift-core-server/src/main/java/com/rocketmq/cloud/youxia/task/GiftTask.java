package com.rocketmq.cloud.youxia.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmq.cloud.youxia.config.LiveGiftConfig;
import com.rocketmq.cloud.youxia.entity.SevenLiveGiftEntity;
import com.rocketmq.cloud.youxia.manager.SevenLiveGiftManager;
import com.rocketmq.cloud.youxia.service.DistributedService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.LongAdder;

@EnableScheduling
@Component
public class GiftTask {

    @Autowired
    private GiftCachePool giftCachePool;

    @Autowired
    private ProducerCachePool producerCachePool;

    @Autowired
    private SevenLiveGiftManager sevenLiveGiftManager;

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private DistributedService distributedService;

    private ObjectMapper mapper = new ObjectMapper();

    private volatile LongAdder putGiftNum=new LongAdder();

    @Autowired
    private LiveGiftConfig liveGiftConfig;

    //定时的送礼物，发起分布式事务消息的流程
    @Scheduled(fixedRate = 1000)
    public void putGift() throws MQClientException, UnsupportedEncodingException,JsonProcessingException {
        Integer maxPutGiftNum=liveGiftConfig.getSimulateNum();
        if(putGiftNum.intValue()<maxPutGiftNum) {
            SevenLiveGiftEntity putGift = giftCachePool.randomGift();
            SevenLiveGiftEntity dbdata = sevenLiveGiftManager.selectBySevenLiveGiftEntity(putGift);
            //扣减总礼物数为一个
            Integer duceNum = dbdata.getNum() - 1;
            dbdata.setNum(duceNum);
            //每次送一个礼物
            dbdata.setGiveNum(1);
            //设置本次送礼物的一个唯一key
            dbdata.setUk(distributedService.nextId());
            //随机的使用一个生产者，模拟多集群中的负载均衡算法，使用生产者去生产事务消息。
            TransactionMQProducer transactionMQProducer = producerCachePool.randomProducer();
            dbdata.setProducerClientId(transactionMQProducer.getDefaultMQProducerImpl().getmQClientFactory().getClientId());
            String json = mapper.writeValueAsString(dbdata);
            String message = json;
            String topic = "giftMessage";
            Message transactionMessage = new Message(topic, (message).getBytes(RemotingHelper.DEFAULT_CHARSET));
            //生产事务消息
            transactionMQProducer.sendMessageInTransaction(transactionMessage, dbdata);
            putGiftNum.increment();
        }
    }
}
