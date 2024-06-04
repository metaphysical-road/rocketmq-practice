在Push模式的消费者客户端注册了一个监听器MessageListenerConcurrently类后，消费者客户端会解析这个监听器，从而初始化ConsumeMessageConcurrentlyService类，具体代码如下：

```java
public class DefaultMQPushConsumerImpl implements MQConsumerInner {
    private boolean consumeOrderly = false;
    private ConsumeMessageService consumeMessageService;
    public synchronized void start() throws MQClientException {
        ...
        //①如果监听器类型是MessageListenerOrderly，则初始化一个顺序消费消息的对
            象ConsumeMessageOrderlyService
        if (this.getMessageListenerInner() instanceof 
            MessageListenerOrderly) {
            this.consumeOrderly = true;
            //②初始化一个顺序消费消息的对象ConsumeMessageOrderlyService
            this.consumeMessageService =
                new ConsumeMessageOrderlyService(this,  
                (MessageListenerOrderly)this.getMessageListenerInner());
        //③如果监听器是MessageListenerConcurrently，则初始化一个并发消费消息的 
            对象MessageListenerConcurrently
        } else if (this.getMessageListenerInner() instanceof 
            MessageListenerConcurrently) {
            this.consumeOrderly = false;
            //④初始化一个并行消费的对象ConsumeMessageConcurrentlyService
            this.consumeMessageService =
                new ConsumeMessageConcurrentlyService(this, 
               (MessageListenerConcurrently) this.
                    getMessageListenerInner());
        }
        //⑤开启定时任务
        this.consumeMessageService.start();
    }
}
```

