执行线程PullTaskImpl 类的run()方法，具体代码如下：

```java
@Override
public void run() {
    ...
    long offset = 0L;
    try {
         //①获取pull消息的消费位置offset
         offset = nextPullOffset(messageQueue);
    } catch (Exception e) {
         return;
    }
    if (this.isCancelled() || processQueue.isDropped()) {
         return;
    }
    long pullDelayTimeMills = 0;
    try {
        //②获取对应消息主题的消息队列信息SubscriptionData 
        SubscriptionData subscriptionData;
        String topic = this.messageQueue.getTopic();
        if (subscriptionType == SubscriptionType.SUBSCRIBE) {
            subscriptionData = 
                rebalanceImpl.getSubscriptionInner().get(topic);
        } else {
            subscriptionData = FilterAPI.buildSubscriptionData(topic, 
                SubscriptionData.SUB_ALL);
        }
  //③调用DefaultLitePullConsumerImpl类的pull()方法，从Broker Server拉取消
       息
        PullResult pullResult = pull(messageQueue, subscriptionData, offset, 
            defaultLitePullConsumer.getPullBatchSize());
        if (this.isCancelled() || processQueue.isDropped()) {
            return;
        }
        switch (pullResult.getPullStatus()) {
            //④如果pull消息的结果为“FOUND”，则处理pull的消息列表
            case FOUND:
                final Object objLock = messageQueueLock
.fetchLockObject(messageQueue);
                synchronized (objLock) {
                    if (pullResult.getMsgFoundList() != null 
&& !pullResult.getMsgFoundList().isEmpty()
&& assignedMessageQueue.getSeekOffset(messageQueue)
 == -1) {
//⑤将pull消息的结果和消息列表设置到消息处理队列中
                          processQueue.putMessage(pullResult.getMsgFoundList());
                          //⑥提交消费消息的请求
                      submitConsumeRequest(new ConsumeRequest(pullResult.
getMsgFoundList(), messageQueue, processQueue));
                    }
                }
                break;
           case OFFSET_ILLEGAL:
                break;
           default:
                break;
            }
            //⑦更新消息队列中的消费消息的消费位置
            updatePullOffset(messageQueue, pullResult.getNextBeginOffset(), 
                processQueue);
        } catch (Throwable e) {
             pullDelayTimeMills = pullTimeDelayMillsWhenException;
        }
    ...
    }
}
```

