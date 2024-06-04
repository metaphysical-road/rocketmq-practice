执行批量消费消息的请求的具体代码如下所示：

```java
@Override
public void submitConsumeRequest(
    final List<MessageExt> msgs,final ProcessQueue processQueue,
    final MessageQueue messageQueue,final boolean dispatchToConsume) {
    final int consumeBatchSize = this.defaultMQPushConsumer.
        getConsumeMessageBatchMaxSize();
    //①如果批处理的消息的数量小于等于预先设置的阈值（阈值默认为1），则开发人员可以去自定义
    if (msgs.size() <= consumeBatchSize) {
        //②构造消费消息的请求ConsumeRequest
        ConsumeRequest consumeRequest = new ConsumeRequest(msgs, 
             processQueue, messageQueue);
        try {
            //③用并发消费消息的线程池ThreadPoolExecutor去处理消费消息的请求
            this.consumeExecutor.submit(consumeRequest);
        } catch (RejectedExecutionException e) {
            //④如果出现异常，为了确保消费消息的高可用性，则延迟投放消费消息的请求
            this.submitConsumeRequestLater(consumeRequest);
        }
    } else {
    //⑤如果批处理的消息的数量大于预先设置的阈值，则需要丢弃一部分消息，以确保只
      消费对应阈值数量的消息
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
            //⑥重新构造消费消息的请求ConsumeRequest
            ConsumeRequest consumeRequest = new ConsumeRequest(msgThis, 
                processQueue, messageQueue);
            try {
           //⑦重新用并发消费消息的线程池ThreadPoolExecutor去处理消费消息的请求
                this.consumeExecutor.submit(consumeRequest);
            } catch (RejectedExecutionException e) {
                for (; total < msgs.size(); total++) {
                    msgThis.add(msgs.get(total));
                }
           //⑧如果出现异常，为了确保消费消息的高可用性，则延迟投放消费消息的请求
                this.submitConsumeRequestLater(consumeRequest);
            }
        }
    }
}

```

