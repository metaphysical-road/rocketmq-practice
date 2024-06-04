package com.rocketmq.cloud.youxia.controller;

import com.rocketmq.cloud.youxia.bo.SevenOrderBo;
import com.rocketmq.cloud.youxia.bo.SevenTradeBo;
import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.SevenOrderDto;
import com.rocketmq.cloud.youxia.dto.SevenTradeDto;
import com.rocketmq.cloud.youxia.service.DistributedService;
import com.rocketmq.cloud.youxia.service.SevenOrderService;
import com.rocketmq.cloud.youxia.service.SevenTradeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/entry")
public class EntryController {

    @DubboReference(group = "rocketmq-practice", version = "1.0.0")
    private SevenTradeService sevenTradeService;

    @DubboReference(group = "rocketmq-practice", version = "1.0.0")
    private SevenOrderService sevenOrderService;

    @DubboReference(version = "1.0.0", group = "rocketmq-practice")
    private DistributedService distributedService;

    //模拟用户在购物车中买商品
    @GetMapping(value = "/buyGood")
    public String buyGood(String goodId, String skuId,String num,String price) {
        //生成一个全局ID，用来标记当前购买商品请求的唯一性。
        final Long gobalTraceId = distributedService.nextId();
        //构造请求入参数
        SevenTradeBo sevenTradeBo = new SevenTradeBo();
        //设置商品ID
        sevenTradeBo.setGoodId(Long.valueOf(goodId));
        //设置SkuID
        sevenTradeBo.setSkuId(skuId);
        //设置购买商品的数量
        sevenTradeBo.setNum(Integer.valueOf(num));
        //设置商品价格
        sevenTradeBo.setPrice(price);
        //设置全局唯一ID
        sevenTradeBo.setGobalUuid(gobalTraceId);
        sevenTradeBo.setCurrentUuid(gobalTraceId);
        sevenTradeBo.setUserId(123456L);
        //调用交易服务，也可以叫做购物车服务，发起用户下单的请求。
        System.out.println("调用交易服务，发起下单操作:" + sevenTradeBo.toString());
        //调用交易服务发起下单请求
        Result<SevenTradeDto> result = sevenTradeService.buyGood(sevenTradeBo);
        //返回下单的结果
        //一般是立即返回
        return result.getMsg();
    }

    /**
     * 模拟在线支付
     * @param orderId
     * @param goodId
     * @param skuId
     * @return
     */
    @GetMapping(value = "/onlinePay")
    public String onlinePay(String orderId,String goodId, String skuId){
        SevenOrderBo sevenOrderBo=new SevenOrderBo();
        Result<SevenOrderDto> result=sevenOrderService.onlinePay(sevenOrderBo);
        return result.getMsg();
    }
}
