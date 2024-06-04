PopMessageProcessor类是处理“pop”模式消费消息请求的命令事件处理器，其中用processRequest()方法处理消费消息的请求并与零拷贝相关的具体代码实现如下：

```java
//①校验是否开启堆内内存(即使用JVM中预分配的堆内存)，默认开启
if (this.brokerController.getBrokerConfig().isTransferMsgByHeap()) {
//②省略“pop”模式处理堆内内存消息消息请求的相关代码
} else {
    //③用final类型的变量拷贝拉取消息的结果对象，确保线程安全
    final GetMessageResult tmpGetMessageResult = getMessageResult;
    try {
    //④如果开启堆外内存（零拷贝），则将封装拉取消息的结果对象GetMessageResult，转
       换为ManyMessageTransfer对象
        FileRegion fileRegion =new ManyMessageTransfer(response.
            encodeHeader(getMessageResult.
getBufferTotalSize()),getMessageResult);
           //⑤将零拷贝对象ManyMessageTransfer写入Netty通信渠道中
            channel.writeAndFlush(fileRegion).addListener(new 
ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) 
throws Exception {
                    //⑥释放GetMessageResult 中封装的映射文件MappedFile的资源
                    tmpGetMessageResult.release();
                    if (!future.isSuccess()) {
                        POP_LOGGER.error("Fail to transfer messages from page 
                           cache to {}",
                            channel.remoteAddress(), future.cause());
                    }
                }
            });
        } catch (Throwable e) {
            POP_LOGGER.error("Error occurred when transferring messages from 
                page cache", e);
                getMessageResult.release();
        }
    response = null;
}
```

