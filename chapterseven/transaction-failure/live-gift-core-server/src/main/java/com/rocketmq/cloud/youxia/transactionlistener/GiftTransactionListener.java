package com.rocketmq.cloud.youxia.transactionlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.config.LiveGiftConfig;
import com.rocketmq.cloud.youxia.entity.SevenLiveGiftEntity;
import com.rocketmq.cloud.youxia.entity.SevenLogEntity;
import com.rocketmq.cloud.youxia.manager.SevenLiveGiftManager;
import com.rocketmq.cloud.youxia.manager.SevenLogManager;
import com.rocketmq.cloud.youxia.service.DistributedService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.Date;
import java.util.Map;
/**
 * 礼物服务的本地事务状态的监听器
 */
@Component
public class GiftTransactionListener implements TransactionListener {

    @Autowired
    private SevenLiveGiftManager sevenLiveGiftManager;

    @Autowired
    private SevenLogManager sevenLogManager;

    @Autowired
    private LiveGiftConfig liveGiftConfig;

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private DistributedService distributedService;

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try {
            SevenLiveGiftEntity updateObject = (SevenLiveGiftEntity) arg;
            updateObject.setGmt_modified(new Date(System.currentTimeMillis()));
            Integer result = sevenLiveGiftManager.update(updateObject);
            //用日志和缓存来记录执行的结果
            SevenLogEntity sevenLogEntity = new SevenLogEntity();
            sevenLogEntity.setId(distributedService.nextId());
            sevenLogEntity.setType(1);
            String content = new String(msg.getBody(), Charsets.UTF_8);
            sevenLogEntity.setContent(content);
            //执行本地事务成功
            sevenLogEntity.setStatus(0);
            sevenLogEntity.setIsDeleted(0);
            sevenLogEntity.setGmt_create(new Date(System.currentTimeMillis()));
            sevenLogEntity.setGmt_modified(new Date(System.currentTimeMillis()));
            sevenLogEntity.setUk(updateObject.getUk() + "");
            sevenLogManager.insertLog(sevenLogEntity);
            System.out.println("[executeLocalTransaction],执行本地事务的生产者是："+updateObject.getProducerClientId()+" 消息对应的全局唯一ID uk为：" +
                    ""+updateObject.getUk());
            //如果开启故障植入，则植入一个故障，模拟生产者执行本地事务成功之后，但是返回给Broker Server超时
            //这样Broker Server就需要开启回调机制去查询本地事务的状态。
            if(liveGiftConfig.getOpenFaultInsertion().equals("true")){
                //植入一个故障
                SevenLiveGiftEntity test=new SevenLiveGiftEntity();
                //植入超时时间
                Thread.sleep(5000);
                if(test.getGiftName().equals("")){
                    //报一个空指针的业务异常
                }
            }
            //如果更新成功，则返回"提交状态"
            if (result > 0) {
                return LocalTransactionState.COMMIT_MESSAGE;
            } else {
                //否则返回"回滚状态"
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } catch (Exception e) {
            System.out.println(e.getCause().getMessage());
            //如果出现异常，则返回"回滚状态"
            return LocalTransactionState.UNKNOW;
        }
    }

    //校验本地事务状态的逻辑一定要通用，不然故障转移之后会出现问题
    //本实例采用校验日志的状态，如果读者需要提升性能，也可以采用redis来校验。
    @Override
    public LocalTransactionState checkLocalTransaction (MessageExt msg) {
        String content = new String(msg.getBody(), Charsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.readValue(content, Map.class);
            if (map.containsKey("uk")) {
                Long uk = (Long) map.get("uk");
                SevenLogEntity sevenLogEntity = new SevenLogEntity();
                sevenLogEntity.setUk(uk + "");
                SevenLogEntity fromDb = sevenLogManager.selectLog(sevenLogEntity);
                if (null != fromDb) {
                    Integer status = fromDb.getStatus();
                    if (status == 0) {
                        //设置全局唯一ID
                        msg.putUserProperty("uk",uk+"");
                        System.out.println("[checkLocalTransaction] 执行本地事务的生产者是：" + map.get("producerClientId") + " 消息对应的全局唯一ID uk为：" + uk);
                        return LocalTransactionState.COMMIT_MESSAGE;
                    }
                } else {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
            }
        } catch (JsonProcessingException e) {
            if (null != e) {
                System.out.println(e.getMessage());
            }
            return LocalTransactionState.UNKNOW;
        } catch (Exception e1) {
            if (null != e1) {
                System.out.println(e1.getMessage());
            }
            return LocalTransactionState.UNKNOW;
        }
        return LocalTransactionState.ROLLBACK_MESSAGE;
    }
}
