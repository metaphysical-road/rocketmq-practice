package com.rocketmq.cloud.youxia.service;

import com.rocketmq.cloud.youxia.bo.SevenGoodBo;
import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.SevenGoodDto;

public interface SevenGoodService {
    //扣减库存
    Result<SevenGoodDto> deductionInventory(SevenGoodBo sevenGoodBo);
    //锁库存
    Result<SevenGoodDto> lockInventory(SevenGoodBo sevenGoodBo);
    //查询商品库存
    Result<Long> queryGoodNum(SevenGoodBo sevenGoodBo);
}
