QueryMessageProcessor类是处理查询消息的命令事件处理器，在用viewMessageById()方法处理命令事件“RequestCode.VIEW_MESSAGE_BY_ID”时，会使用零拷贝具体代码如下：

```java
try {
         //①将封装查询消息的结果对象QueryMessageResult转换为零拷贝对象
             OneMessageTransfer
    FileRegion fileRegion =new OneMessageTransfer(response.
encodeHeader(selectMappedBufferResult.getSize()),
           selectMappedBufferResult);
    //②将零拷贝对象OneMessageTransfer写入Netty通信渠道中
    ctx.channel().writeAndFlush(fileRegion).addListener(new 
        ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception 
           {
          //③释放SelectMappedBufferResult对象中封装的映射文件MappedFile资源
            selectMappedBufferResult.release();
            if (!future.isSuccess()) {
                log.error("Transfer one message from page cache failed, ", 
                  future.cause());
            }
        }
    });
} catch (Throwable e) {
    log.error("", e);
    selectMappedBufferResult.release();
}                    

```

