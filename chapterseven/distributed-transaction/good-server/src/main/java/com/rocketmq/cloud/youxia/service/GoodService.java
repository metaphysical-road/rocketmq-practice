package com.rocketmq.cloud.youxia.service;

import com.rocketmq.cloud.youxia.bo.SevenGoodBo;
import com.rocketmq.cloud.youxia.common.CommonCode;
import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.SevenGoodDto;
import com.rocketmq.cloud.youxia.entity.GoodsSkuEntity;
import com.rocketmq.cloud.youxia.manager.GoodsSkuManager;
//import com.rocketmq.cloud.youxia.rocketmq.ProduceCreateOrderMessage;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(group = "rocketmq-practice",version = "1.0.0")
public class GoodService implements SevenGoodService{

//    @Autowired
//    private ProduceCreateOrderMessage produceCreateOrderMessage;

    @Autowired
    private GoodsSkuManager goodsSkuManager;

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private DistributedService distributedService;

    //查询商品库存
    @Override
    public Result<Long> queryGoodNum(SevenGoodBo sevenGoodBo) {
        Result<Long> responseResult = new Result<>(CommonCode.SUCCESS.getCode(), "成功");
        GoodsSkuEntity goodsSkuEntity=new GoodsSkuEntity();
        goodsSkuEntity.setGoodsId(sevenGoodBo.getGoodId());
        goodsSkuEntity.setSkuId(sevenGoodBo.getSkuId());
        Integer num=goodsSkuManager.selectEntityNumByGoodsSkuEntity(goodsSkuEntity);
        responseResult.setData(Long.valueOf(num));
        return responseResult;
    }

    //扣减商品库存
    @Override
    public Result<SevenGoodDto> deductionInventory(SevenGoodBo sevenGoodBo) {
        return null;
    }

    //锁住商品库存
    @Override
    public Result<SevenGoodDto> lockInventory(SevenGoodBo sevenGoodBo) {
        Result<SevenGoodDto> responseResult = new Result<>(CommonCode.SUCCESS.getCode(), "成功");
        //第一步，执行本地锁库存的本地事务之前，需要发送一条创建前置订单的预处理消息
        //第二步，用监听器去执行本地事务，如果执行成功，则返回事务提交状态，如果执行失败，则返回事务回滚状态，如果本地
        //事务执行结果是未知的，则返回事务状态未知，RocketMQ需要启用定时任务定时的回查事务状态。
        //第三步，订单服务收到创建前置订单的确认消息之后，消费该条消息，并异步的创建前置订单。
        try {
            sevenGoodBo.setParentUuid(sevenGoodBo.getCurrentUuid());
            sevenGoodBo.setCurrentUuid(distributedService.nextId());
            sevenGoodBo.setUk(sevenGoodBo.getCurrentUuid());

            //执行第一步，发送锁库存的分布式事务请求中的预处理消息
//            produceCreateOrderMessage.prodBeforeOrderMessage(sevenGoodBo);
        } catch (Exception e) {
            System.out.println(e.getCause().getMessage());
            responseResult.setErrorCode(CommonCode.SERVER_ERROR.getCode());
            responseResult.setMsg("失败！");
        }
        //返回结果
        return responseResult;
    }
}
