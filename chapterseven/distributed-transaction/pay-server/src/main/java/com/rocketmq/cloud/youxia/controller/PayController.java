package com.rocketmq.cloud.youxia.controller;

import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.SevenPayDto;
import com.rocketmq.cloud.youxia.service.SevenPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayController {
    @Autowired
    private SevenPayService sevenPayService;

    //模拟用户在购物车中买商品
    @GetMapping(value = "/test/onlinePay")
    public SevenPayDto onlinePay() {
        Result<SevenPayDto> result= sevenPayService.pay(12823498934453L);
        return result.getData();
    }
}
