在并发消费消息时，使用的是JDK原生的线程池，具体代码实现如下：

```java
//①注意接口ConsumeMessageService并不是一个线程
public class ConsumeMessageConcurrentlyService implements 
    ConsumeMessageService {
    private final BlockingQueue<Runnable> consumeRequestQueue;
    private final ThreadPoolExecutor consumeExecutor;
    
    public ConsumeMessageConcurrentlyService(DefaultMQPushConsumerImpl 
        defaultMQPushConsumerImpl,
        MessageListenerConcurrently messageListener) {
        ...
        //②初始化一个阻塞队列LinkedBlockingQueue，用来存储线程池中的任务
        this.consumeRequestQueue = new LinkedBlockingQueue<Runnable>();
        this.consumeExecutor = new ThreadPoolExecutor(
            //③获取设置消费消息的线程池的最小核心线程数
            this.defaultMQPushConsumer.getConsumeThreadMin(),
            //④获取设置消费消息的线程池的最大核心线程数
            this.defaultMQPushConsumer.getConsumeThreadMax(),
            //⑤设置线程有效时间为60s
            1000 * 60,
            TimeUnit.MILLISECONDS,
            //⑥设置线程池的任务队列
            this.consumeRequestQueue,
            //⑦初始化一个线程工厂类ThreadFactoryImpl
            new ThreadFactoryImpl("ConsumeMessageThread_"));
    }
}
```

