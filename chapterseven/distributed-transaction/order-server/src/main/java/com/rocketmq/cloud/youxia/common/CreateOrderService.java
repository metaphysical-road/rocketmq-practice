package com.rocketmq.cloud.youxia.common;
import com.rocketmq.cloud.youxia.bo.SevenGoodBo;
import com.rocketmq.cloud.youxia.service.SevenGoodService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.common.ServiceThread;
import org.apache.rocketmq.common.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.*;
/**
 * 先锁库存成功，再去创建前置订单
 */
//@Slf4j
@Component
public class CreateOrderService extends ServiceThread {

    @Autowired
    private InsertBeforeOrderService insertBeforeOrderService;

    @DubboReference(group = "rocketmq-practice", version = "1.0.0")
    private SevenGoodService goodService;

    //异步队列
    private final LinkedBlockingQueue<CreateOrderRequest> pullRequestQueue = new LinkedBlockingQueue<CreateOrderRequest>();

    private final ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "PullMessageServiceScheduledThread");
                }
            });

    //延迟执行
    public void executePullRequestLater(final CreateOrderRequest createOrderRequest, final long timeDelay) {
        if (!isStopped()) {
            this.scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    CreateOrderService.this.executePullRequestImmediately(createOrderRequest);
                }
            }, timeDelay, TimeUnit.MILLISECONDS);
        } else {
            System.out.println("PullMessageServiceScheduledThread has shutdown");
        }
    }

    //实时执行
    public void executePullRequestImmediately(final CreateOrderRequest createOrderRequest) {
        try {
            this.pullRequestQueue.put(createOrderRequest);
        } catch (InterruptedException e) {
            System.out.println("executePullRequestImmediately pullRequestQueue.put:"+ e.getMessage());
        }
    }

    //创建前置订单
    private void createOrder(final CreateOrderRequest createOrderRequest) {
        //调用商品服务去锁商品库存
        SevenGoodBo sevenGoodBo = new SevenGoodBo();
        sevenGoodBo.setGoodId(createOrderRequest.getGoodId());
        sevenGoodBo.setUserId(createOrderRequest.getUserId());
        sevenGoodBo.setNum(createOrderRequest.getNum());
        sevenGoodBo.setPrice(createOrderRequest.getPrice());
        sevenGoodBo.setGobalUuid(createOrderRequest.getGobalUuid());
        sevenGoodBo.setCurrentUuid(createOrderRequest.getCurrentUuid());
        sevenGoodBo.setUk(createOrderRequest.getCurrentUuid());
        //第一步预备锁前台库存
//        Result<SevenGoodDto> lockResult=goodService.lockInventory(sevenGoodBo);
        //第二步执行创建前置订单的异步请求
//        if(lockResult.getErrorCode().equals(CommonCode.SUCCESS.getCode())){
        insertBeforeOrderService.executePullRequestLater(createOrderRequest, 2000L);
//        }
    }

    @Override
    public void run() {
        System.out.println(this.getServiceName() + " service started");

        while (!this.isStopped()) {
            try {
                CreateOrderRequest createOrderRequest = this.pullRequestQueue.take();
                this.createOrder(createOrderRequest);
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                System.out.println("Pull Message Service Run Method exception:"+e.getMessage());
            }
        }
        System.out.println(this.getServiceName() + " service end");
    }

    @Override
    public void shutdown(boolean interrupt) {
        super.shutdown(interrupt);
        ThreadUtils.shutdownGracefully(this.scheduledExecutorService, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getServiceName() {
        return CreateOrderService.class.getSimpleName();
    }

}
