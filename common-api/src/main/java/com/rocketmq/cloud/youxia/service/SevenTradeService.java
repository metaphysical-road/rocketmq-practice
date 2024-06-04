package com.rocketmq.cloud.youxia.service;

import com.rocketmq.cloud.youxia.bo.SevenTradeBo;
import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.SevenTradeDto;
public interface SevenTradeService {
    Result<SevenTradeDto> buyGood(SevenTradeBo sevenTradeBo);
}
