package com.rocketmq.cloud.youxia.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/pay")
public class OnlinePayController {
    //模拟用户在购物车中买商品
    @PostMapping(value = "/onlinePay")
    public Map<String,Object> onlinePay() {
        Map<String,Object> result=new HashMap<>();
        result.put("code","000000");
        result.put("message","支付成功！");
        return result;
    }
}
