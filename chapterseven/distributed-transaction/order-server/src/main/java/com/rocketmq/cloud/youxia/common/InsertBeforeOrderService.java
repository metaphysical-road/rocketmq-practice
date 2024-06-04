package com.rocketmq.cloud.youxia.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.config.OrderConfig;
import com.rocketmq.cloud.youxia.entity.OrderEntity;
import com.rocketmq.cloud.youxia.entity.OrderItemEntity;
import com.rocketmq.cloud.youxia.manager.OrderItemManager;
import com.rocketmq.cloud.youxia.manager.OrderManager;
import com.rocketmq.cloud.youxia.service.DistributedService;
//import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.ServiceThread;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

//@Slf4j
@Component
public class InsertBeforeOrderService extends ServiceThread {
    private final LinkedBlockingQueue<CreateOrderRequest> pullRequestQueue = new LinkedBlockingQueue<CreateOrderRequest>();

    @Autowired
    private OrderItemManager orderItemManager;

    @Autowired
    private OrderManager orderManager;

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private DistributedService distributedService;

    @Override
    public void run() {
        System.out.println(this.getServiceName() + " service started");

        while (!this.isStopped()) {
            try {
                CreateOrderRequest createOrderRequest = this.pullRequestQueue.take();
                this.insertOrder(createOrderRequest);
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                System.out.println("Pull Message Service Run Method exception:"+e.getMessage());
            }
        }
       System.out.println(this.getServiceName() + " service end");
    }

    @Autowired
    private OrderConfig orderConfig;

    private volatile LongAdder longAdder=new LongAdder();

    private ObjectMapper objectMapper = new ObjectMapper();

    public void consumerMessage() throws MQClientException {
        while (longAdder.intValue()<orderConfig.getConsumerNum()){
            int add=orderConfig.getConsumerNum()-longAdder.intValue();
            for(int i=0;i<add;i++){
                DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(orderConfig.getConsumerGroup());
                defaultMQPushConsumer.subscribe(orderConfig.getTopic(), "");
                defaultMQPushConsumer.setNamesrvAddr(orderConfig.getNamesrvAddr());
                defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                    @Override
                    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                        if (CollectionUtils.isNotEmpty(msgs)) {
                            for (MessageExt messageExt : msgs) {
                                String content = new String(messageExt.getBody(), Charsets.UTF_8);
                                try {
                                    Map<String, Object> map = objectMapper.readValue(content, Map.class);
                                    //校验订单的全局UUID
                                } catch (JsonProcessingException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                        ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        return result;
                    }
                });
                defaultMQPushConsumer.start();
                longAdder.increment();
            }
        }
    }

    private void insertOrder(final CreateOrderRequest createOrderRequest) {

        //插入前置处理订单
        OrderEntity orderEntity=new OrderEntity();
        orderEntity.setId(distributedService.nextId());
        orderEntity.setPayPrice(new BigDecimal(createOrderRequest.getPrice()));
        orderEntity.setOrderPrice(new BigDecimal(createOrderRequest.getPrice()));
        //0代表移动端，1代表PC端
        orderEntity.setClientType(0);
        //0表示待付款
        orderEntity.setOrderStatus(0);
        //0表示待发货
        orderEntity.setDeliverStatus(0);
        //0表示支付宝
        orderEntity.setPaymentMethod(0);
        orderEntity.setUserId(createOrderRequest.getUserId());
        orderEntity.setSn(distributedService.nextId());
        orderEntity.setTradeSn(createOrderRequest.getCurrentUuid());
        orderEntity.setGmtCreate(new Date());
        orderEntity.setGmtModified(new Date());
        orderEntity.setIsDeleted(0);
        orderManager.insert(orderEntity);
        OrderItemEntity orderItemEntity=new OrderItemEntity();
        orderItemEntity.setOrderSn(orderEntity.getSn());
        orderItemEntity.setNum(createOrderRequest.getNum());
        orderItemEntity.setId(distributedService.nextId());
        orderItemEntity.setTradeSn(createOrderRequest.getCurrentUuid());
        orderItemEntity.setSn(distributedService.nextId());
        orderItemEntity.setFlowPrice(new BigDecimal(createOrderRequest.getPrice()));
        orderItemEntity.setGoodPrice(new BigDecimal(createOrderRequest.getPrice()));
        orderItemEntity.setSkuId(createOrderRequest.getSkuId());
        orderItemEntity.setSkuId(0L);
        orderItemEntity.setGmtCreate(new Date());
        orderItemEntity.setGmtModified(new Date());
        orderItemEntity.setIsDeleted(0);
        orderItemManager.insert(orderItemEntity);
    }

    private final ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "PullMessageServiceScheduledThread");
                }
            });

    public void executePullRequestLater(final CreateOrderRequest createOrderRequest, final long timeDelay) {
        if (!isStopped()) {
            this.scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    InsertBeforeOrderService.this.executePullRequestImmediately(createOrderRequest);
                }
            }, timeDelay, TimeUnit.MILLISECONDS);
        } else {
            System.out.println("PullMessageServiceScheduledThread has shutdown");
        }
    }

    public void executePullRequestImmediately(final CreateOrderRequest createOrderRequest) {
        try {
            this.pullRequestQueue.put(createOrderRequest);
        } catch (InterruptedException e) {
            System.out.println("executePullRequestImmediately pullRequestQueue.put:"+e.getMessage());
        }
    }


    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public String getServiceName() {
        return null;
    }
}
