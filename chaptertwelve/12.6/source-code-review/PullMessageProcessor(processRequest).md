PullMessageProcessor类是处理“push”模式和“pull”模式消费消息请求的命令事件处理器，其中用processRequest()方法处理消费消息的请求并与零拷贝相关的具体代码如下：

```java
//①校验是否开启堆内内存(即使用JVM中预分配的堆内存)，默认开启
if (this.brokerController.getBrokerConfig().isTransferMsgByHeap()) {
//②省略“push”模式和“pull”模式处理堆内内存消息消息请求的相关代码
} else {
    try {
//③如果开启堆外内存（零拷贝），则将封装拉取消息的结果对象GetMessageResult，转换为
ManyMessageTransfer对象
        FileRegion fileRegion =new ManyMessageTransfer(response.
            encodeHeader(getMessageResult.getBufferTotalSize()), 
               getMessageResult);
        //④将零拷贝对象ManyMessageTransfer写入Netty通信渠道中
        channel.writeAndFlush(fileRegion).addListener(new 
              ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws 
               Exception {
                //⑤释放映射文件MappedFile的资源
                getMessageResult.release();
                if (!future.isSuccess()) {
                    log.error("transfer many message by pagecache failed, {}", 
                       channel.remoteAddress(), future.cause());
                }
            }
        });
    } catch (Throwable e) {
        log.error("transfer many message by pagecache exception", e);
        getMessageResult.release();
    }
    response = null;
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
```

