用ConsumeMessagePopConcurrentlyService类的submitConsumeRequest()方法消费消息的具体代码如下:

```java
public void submitConsumeRequest(
    final List<MessageExt> msgs,final ProcessQueue processQueue,
    final MessageQueue messageQueue,final boolean dispatchToConsume) {
    //①获取批量消费消息的批量值，默认为1条。调用者可以自定义这个值
    final int consumeBatchSize = this.defaultMQPushConsumer.
        getConsumeMessageBatchMaxSize();
    //②如果拉取到的消息列表中消息的条数小于等于批量值，则构造一个消费消息的线程
        ConsumeRequest对象
    if (msgs.size() <= consumeBatchSize) {
        ConsumeRequest consumeRequest = new ConsumeRequest(msgs, 
            processQueue, messageQueue);
        try {
            //③用线程池异步地执行线程，消费消息
            this.consumeExecutor.submit(consumeRequest);
        } catch (RejectedExecutionException e) {
            //④如果出现异常，则延迟消费当前消息列表
            this.submitConsumeRequestLater(consumeRequest);
        }
    } else {
        //⑤如果拉取到的消息列表中消息的条数大于批量值，则过滤掉多余的消息
        for (int total = 0; total < msgs.size(); ) {
            List<MessageExt> msgThis = new 
                ArrayList<MessageExt>(consumeBatchSize);
            for (int i = 0; i < consumeBatchSize; i++, total++) {
                if (total < msgs.size()) {
                    msgThis.add(msgs.get(total));
                } else {
                    break;
                }
            }
            //⑥构造消费消息的线程对象ConsumeRequest 
            ConsumeRequest consumeRequest = new ConsumeRequest(msgThis, 
                processQueue, messageQueue);
            try {
                //⑦用线程池异步地执行线程，消费消息
                this.consumeExecutor.submit(consumeRequest);
            } catch (RejectedExecutionException e) {
                //⑧如果出现异常，则将过滤掉的消息重新复制到消息列表中
                for (; total < msgs.size(); total++) {
                    msgThis.add(msgs.get(total));
                }
                //⑨延迟消费当前消息列表
                this.submitConsumeRequestLater(consumeRequest);
            }
        }
    }
}
```

