QueryMessageProcessor类是处理查询消息的命令事件处理器，用queryMessage()方法处理命令事件“RequestCode.QUERY_MESSAGE”时，会使用零拷贝具体代码如下：

```java
try {
        //①将封装查询消息的结果对象QueryMessageResult转换为零拷贝对象
            QueryMessageTransfer
    FileRegion fileRegion =
        new QueryMessageTransfer(response.encodeHeader(queryMessageResult
            .getBufferTotalSize()), queryMessageResult);
    //②将零拷贝对象QueryMessageTransfer写入Netty通信渠道中
    ctx.channel().writeAndFlush(fileRegion).addListener(new 
        ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception 
           {
            //③释放QueryMessageResult对象中封装的映射文件MappedFile资源
            queryMessageResult.release();
            if (!future.isSuccess()) {
                log.error("transfer query message by page cache failed, ", 
                  future.cause());
                }
            }
        });
} catch (Throwable e) {
    log.error("", e);
    queryMessageResult.release();
}

```

