调用NettyRemotingAbstract类的invokeSyncImpl()方法向Broker Server发起RPC请求，具体代码如下：

```java
public RemotingCommand invokeSyncImpl(final Channel channel, final 
    RemotingCommand request,final long timeoutMillis)
    throws InterruptedException, RemotingSendRequestException, 
    RemotingTimeoutException {
    //①获取生产消息请求的唯一自增ID
    final int opaque = request.getOpaque();
    try {
        //②初始化一个异步Future响应结果对象ResponseFuture，并绑定通信渠道和生产消
        息请求的唯一自增ID
        final ResponseFuture responseFuture = new ResponseFuture(channel, 
            opaque, timeoutMillis, null, null);
        //③将异步Future响应结果对象存储在本地缓存responseTable中
        this.responseTable.put(opaque, responseFuture);
        final SocketAddress addr = channel.remoteAddress();
        //④用通信渠道客户端调用通信渠道服务端，将生产消息的请求同步地推送给Broker 
        Server服务端
        channel.writeAndFlush(request).addListener(new 
            ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) 
                throws Exception {
                //⑤利用Netty的通信渠道监听器ChannelFuture同步地等待Broker   
                  Server响应的生产消息的结果。如果成功，则设置异步Future响应结果
                  对象的sendRequestOK为true，否则设置为false
if (f.isSuccess()) {
                    responseFuture.setSendRequestOK(true);
                    return;
                } else {
                    responseFuture.setSendRequestOK(false);
                }
                //⑥从本地缓存中删除当前生产消息的异步Future响应结果对象
                responseTable.remove(opaque);
                responseFuture.setCause(f.cause());
                responseFuture.putResponse(null);
                log.warn("send a request command to channel <" + addr + "> 
                    failed.");
            }
        });
        //⑦同步等待响应结果
        RemotingCommand responseCommand = 
            responseFuture.waitResponse(timeoutMillis);
        //⑧如果没有获取到响应的结果，则处理异常
        if (null == responseCommand) {
            //⑨在处理异常时，如果发送成功，则响应通信渠道响应超时的异常，否则响应通信
             渠道发送消息请求的异常
            if (responseFuture.isSendRequestOK()) {
                throw new RemotingTimeoutException(RemotingHelper.
                    parseSocketAddressAddr(addr), timeoutMillis,
                    responseFuture.getCause());
            } else {
                throw new RemotingSendRequestException(RemotingHelper.
                   parseSocketAddressAddr(addr), responseFuture.getCause());
            }
        }
        //⑩同步地返回响应的结果
        return responseCommand;
    } finally {
        this.responseTable.remove(opaque);
    }
}
```

