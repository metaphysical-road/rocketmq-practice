package com.rocketmq.cloud.youxia.service;

import com.rocketmq.cloud.youxia.bo.SevenOrderBo;
import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.SevenOrderDto;

public interface SevenOrderService {
    Result<SevenOrderDto> createBeforeOrder(SevenOrderBo sevenOrderBo);
    Result<SevenOrderDto> createAfterOrder(SevenOrderBo sevenOrderBo);
    Result<SevenOrderDto> queryByUuid(SevenOrderBo sevenOrderBo);
    Result<SevenOrderDto> onlinePay(SevenOrderBo sevenOrderBo);
}
