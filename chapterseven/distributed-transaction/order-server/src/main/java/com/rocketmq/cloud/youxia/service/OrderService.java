package com.rocketmq.cloud.youxia.service;

import com.rocketmq.cloud.youxia.bo.SevenOrderBo;
import com.rocketmq.cloud.youxia.common.CommonCode;
import com.rocketmq.cloud.youxia.common.CreateOrderRequest;
import com.rocketmq.cloud.youxia.common.CreateOrderService;
import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.SevenOrderDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(group = "rocketmq-practice",version = "1.0.0")
public class OrderService implements SevenOrderService{

    @Autowired
    private CreateOrderService createOrderService;

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private DistributedService distributedService;

    /**
     * 创建后置订单
     * @param sevenOrderBo
     * @return
     */
    @Override
    public Result<SevenOrderDto> createAfterOrder(SevenOrderBo sevenOrderBo) {
        return null;
    }

    /**
     * 创建前置订单
     * @param sevenOrderBo
     * @return
     */
    @Override
    public Result<SevenOrderDto> createBeforeOrder(SevenOrderBo sevenOrderBo) {
        //设置已经收到交易服务创建前置订单的请求，并返回成功的结果。
        Result<SevenOrderDto> responseResult=new Result<SevenOrderDto>(CommonCode.SUCCESS.getCode(), "成功");
        //接收请求，并插入一条日志标记已经成功接收该请求
        try {
            final CreateOrderRequest createOrderRequest = new CreateOrderRequest();
            //传递全局ID
            createOrderRequest.setGobalUuid(sevenOrderBo.getGobalUuid());
            //传递上游接口的父ID
            createOrderRequest.setParentUuid(sevenOrderBo.getCurrentUuid());
            //设置当前请求的唯一ID
            createOrderRequest.setCurrentUuid(distributedService.nextId());
            //设置商品ID
            createOrderRequest.setGoodId(sevenOrderBo.getGoodId());
            //设置库存
            createOrderRequest.setNum(sevenOrderBo.getNum());
            //设置商品价格
            createOrderRequest.setPrice(sevenOrderBo.getPrice());
            //设置用户ID
            createOrderRequest.setUserId(sevenOrderBo.getUserId());
            //用线程任务异步的创建订单
            System.out.println("交易服务trade-server异步处理API层的下单操作:"+createOrderRequest.toString());
            //异步处理创建前置订单的请求
            createOrderService.executePullRequestImmediately(createOrderRequest);
        }catch (Exception e){
            responseResult.setErrorCode(CommonCode.SERVER_ERROR.getCode());
            responseResult.setMsg(e.getCause().getMessage());
        }
        //异步的返回创建前置订单的结果
        return responseResult;
    }

    @Override
    public Result<SevenOrderDto> queryByUuid(SevenOrderBo sevenOrderBo) {
        return null;
    }

    /**
     * 处理在线支付的请求
     * @param sevenOrderBo
     * @return
     */
    @Override
    public Result<SevenOrderDto> onlinePay(SevenOrderBo sevenOrderBo) {
        return null;
    }
}
