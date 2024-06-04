package com.rocketmq.cloud.youxia.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.entity.GoodLockStorageEntity;
import com.rocketmq.cloud.youxia.entity.SevenLogEntity;
import com.rocketmq.cloud.youxia.manager.GoodLockStorageManager;
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

@Component
public class CreateOrderTransactionListener implements TransactionListener {

    @Autowired
    private GoodLockStorageManager goodLockStorageManager;

    @Autowired
    private SevenLogManager sevenLogManager;

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private DistributedService distributedService;

    //锁库存成功，通知订单服务去创建前置订单
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        //执行锁库存的本地事务操作，本项目采用插入一个用户ID、商品ID、skuId和锁库存数量的记录，并标记为锁定状态。
        GoodLockStorageEntity goodLockStorageEntity = (GoodLockStorageEntity) arg;
        //用日志和缓存来记录执行的结果
        try {
            SevenLogEntity sevenLogEntity = new SevenLogEntity();
            sevenLogEntity.setId(distributedService.nextId());
            sevenLogEntity.setType(0);
            String content = new String(msg.getBody(), Charsets.UTF_8);
            sevenLogEntity.setContent(content);
            //执行本地事务成功
            sevenLogEntity.setStatus(0);
            sevenLogEntity.setIsDeleted(0);
            sevenLogEntity.setGmt_create(new Date(System.currentTimeMillis()));
            sevenLogEntity.setGmt_modified(new Date(System.currentTimeMillis()));
            sevenLogEntity.setUk(goodLockStorageEntity.getUk() + "");
            //用日志来记录锁定库存的操作，后面需要用这张日志表去
            sevenLogManager.insertLog(sevenLogEntity);
        }catch (Exception e){
            System.out.println(e.getCause().getMessage());
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        //锁库存成功
        try {
            //确认事务消息的状态，比如提交、未知或者状态
            goodLockStorageEntity.setStatus(1);
            //用日志来记录预备扣减库存的记录
            Integer result = goodLockStorageManager.insertGoodLockStorage(goodLockStorageEntity);
            System.out.println("[executeLocalTransaction],执行本地事务的生产者是：" + goodLockStorageEntity.getProducerClientId() + " 消息对应的全局唯一ID uk为：" +
                    "" + goodLockStorageEntity.getUk());
            result=0;
            if (result > 0) {
                return LocalTransactionState.COMMIT_MESSAGE;
            } else {
                return LocalTransactionState.UNKNOW;
            }
        }catch (Exception e){
            System.out.println(e.getCause().getMessage());
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    //如果出现超时，则校验锁库存的本地事务状态，这里主要靠查询日志来完成，订单
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
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
                        //可以消费消息
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
