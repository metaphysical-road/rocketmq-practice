package com.rocketmq.cloud.youxia.service;

import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.SevenPayDto;

public interface SevenPayService {
    Result<SevenPayDto> pay(Long orderId);
}
