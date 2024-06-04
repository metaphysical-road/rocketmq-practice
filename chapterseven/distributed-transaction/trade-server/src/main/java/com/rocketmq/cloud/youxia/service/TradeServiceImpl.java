package com.rocketmq.cloud.youxia.service;

import com.rocketmq.cloud.youxia.bo.SevenGoodBo;
import com.rocketmq.cloud.youxia.bo.SevenOrderBo;
import com.rocketmq.cloud.youxia.bo.SevenTradeBo;
import com.rocketmq.cloud.youxia.common.CommonCode;
import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.SevenOrderDto;
import com.rocketmq.cloud.youxia.dto.SevenTradeDto;
import com.rocketmq.cloud.youxia.rocketmq.ProducerLockStorageMessage;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 交易服务处理下单请求
 */
@DubboService(group = "rocketmq-practice",version = "1.0.0")
public class TradeServiceImpl implements SevenTradeService {

    @DubboReference(group = "rocketmq-practice", version = "1.0.0")
    private SevenOrderService orderService;

    @DubboReference(group = "rocketmq-practice", version = "1.0.0")
    private SevenGoodService goodService;

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private DistributedService distributedService;

    @Autowired
    private ProducerLockStorageMessage producerLockStorageMessage;

    //购买商品的起点
    @Override
    public Result<SevenTradeDto> buyGood(SevenTradeBo sevenTradeBo) {
        Result<SevenTradeDto> responseResult = new Result<>(CommonCode.SUCCESS.getCode(), "成功");
        //记录交易服务处理内部API服务发起的购买商品的唯一性
        final Long processOnUuid = distributedService.nextId();
        //第一步，校验商品库存，库存校验不通过直接返回
        SevenGoodBo querySevenGoodBo = new SevenGoodBo();
        querySevenGoodBo.setSkuId(sevenTradeBo.getSkuId());
        querySevenGoodBo.setGoodId(sevenTradeBo.getGoodId());
        Result<Long> queryResult = goodService.queryGoodNum(querySevenGoodBo);
        if (queryResult.getData() <= 0) {
            //已经库存紧张
            responseResult.setErrorCode(CommonCode.SERVER_ERROR.getCode());
            responseResult.setMsg("库存紧张！");
            return responseResult;
        }
        //第二步，创建前置订单
        SevenOrderBo sevenOrderBo = new SevenOrderBo();
        //设置skuID
        sevenOrderBo.setSkuId(sevenTradeBo.getSkuId());
        //设置商品ID
        sevenOrderBo.setGoodId(sevenTradeBo.getGoodId());
        //获取父全局ID
        sevenOrderBo.setParentUuid(sevenTradeBo.getCurrentUuid());
        //设置当前请求的唯一ID
        sevenOrderBo.setCurrentUuid(processOnUuid);
        //设置全局ID，用来串联所有同步和异步的请求
        sevenOrderBo.setGobalUuid(sevenTradeBo.getGobalUuid());
        //设置购买商品的用户ID
        sevenOrderBo.setUserId(sevenTradeBo.getUserId());
        //设置购买的商品的数量
        sevenOrderBo.setNum(sevenTradeBo.getNum());
        //设置购买的商品的价格
        sevenOrderBo.setPrice(sevenTradeBo.getPrice());
        //调用订单服务去创建前置订单
        System.out.println("订单服务order-server处理API层的下单操作:" + sevenOrderBo.toString());
        //1.1下单的RPC请求，交易服务调用订单服务，生成前置订单
        Result<SevenOrderDto> createOrderResult = orderService.createBeforeOrder(sevenOrderBo);
        //异步的发送锁库存的事务消息
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("processOnUuid",processOnUuid+"");
        messageMap.put("gobalUuid",sevenTradeBo.getGobalUuid()+"");
        messageMap.put("goodId",sevenTradeBo.getGoodId()+"");
        messageMap.put("skuId",sevenTradeBo.getSkuId()+"");
        messageMap.put("num",sevenTradeBo.getNum()+"");
        //生产锁前台库存的预处理消息
        producerLockStorageMessage.produceLockStorageMessage(messageMap);
        //确认创建前置订单成功之后，利用监听器去生产锁前台库存的确认消息
        //详情可以参考监听器LockStorageTransactionListener
        //第三步处理创建前置订单的结果
        if (Result.isSuccess(createOrderResult)) {
            responseResult.setErrorCode(CommonCode.SUCCESS.getCode());
        } else {
            responseResult.setErrorCode(CommonCode.SERVER_ERROR.getCode());
        }
        return responseResult;
    }
}
