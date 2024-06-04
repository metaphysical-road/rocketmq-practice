package com.rocketmq.cloud.youxia.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.bo.SevenOrderBo;
import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.LockStorageMessageDTO;
import com.rocketmq.cloud.youxia.dto.SevenOrderDto;
import com.rocketmq.cloud.youxia.service.SevenOrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class LockStorageTransactionListener implements TransactionListener {

    @DubboReference(group = "rocketmq-practice", version = "1.0.0")
    private SevenOrderService orderService;

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try {
            //和订单服务对应的生产前置订单的数据操作，形成本地事务。
            LockStorageMessageDTO lockStorageMessageDTO = (LockStorageMessageDTO) arg;
            SevenOrderBo sevenOrderBo = new SevenOrderBo();
            sevenOrderBo.setGobalUuid(Long.valueOf(lockStorageMessageDTO.getGobalUuid()));
            sevenOrderBo.setCurrentUuid(Long.valueOf(lockStorageMessageDTO.getProcessOnUuid()));
            //查询前置订单是否创建成功！！！
            Result<SevenOrderDto> sevenOrderDto = orderService.queryByUuid(sevenOrderBo);
            //前置订单已经创建成功
            if (null != sevenOrderDto) {
                return LocalTransactionState.COMMIT_MESSAGE;
            } else {
                return LocalTransactionState.UNKNOW;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return LocalTransactionState.UNKNOW;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        //如果执行本地事务出现问题，也就是需要校验事务状态
        String content = new String(msg.getBody(), Charsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> map = objectMapper.readValue(content, Map.class);
            SevenOrderBo sevenOrderBo = new SevenOrderBo();
            if (map.containsKey("processOnUuid")) {
                Long processOnUuid = Long.valueOf(map.get("processOnUuid"));
                sevenOrderBo.setCurrentUuid(processOnUuid);
            }
            if (map.containsKey("gobalUuid")) {
                Long gobalUuid = Long.valueOf(map.get("gobalUuid"));
                sevenOrderBo.setGobalUuid(gobalUuid);
            }

            Result<SevenOrderDto> sevenOrderDto = orderService.queryByUuid(sevenOrderBo);
            //前置订单已经创建成功
            if (null != sevenOrderDto) {
                return LocalTransactionState.COMMIT_MESSAGE;
            } else {
                return LocalTransactionState.ROLLBACK_MESSAGE;
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
    }
}
