调用NettyRemotingClient类的invokeSync ()方法发起“同步”模式生产消息的RPC请求，具体代码分析如下：

```java
@Override
public RemotingCommand invokeSync(String addr, final RemotingCommand 
    request, long timeoutMillis)
        throws InterruptedException, RemotingConnectException, 
        RemotingSendRequestException, RemotingTimeoutException {
    long beginStartTime = System.currentTimeMillis();
    //①创建Netty通信渠道。如果已经存在，则复用通信渠道
    final Channel channel = this.getAndCreateChannel(addr);
    //②判断通信渠道是否可用。如果可用，则继续执行同步生产消息的流程
    if (channel != null && channel.isActive()) {
        try {
            //③在同步生产消息之前执行RPChook函数
            doBeforeRpcHooks(addr, request);
            long costTime = System.currentTimeMillis() - beginStartTime;
            //④如果耗时大于超时时间，则抛“同步调用失败”的异常
            if (timeoutMillis < costTime) {
                throw new RemotingTimeoutException("invokeSync call 
                    timeout");
            }
            //⑤调用NettyRemotingAbstract类的invokeSyncImpl()方法，同步地发送
            生产消息的RPC请求
            RemotingCommand response = this.invokeSyncImpl(channel, request, 
                timeoutMillis - costTime);
            //⑥在同步生产消息完成后，执行RPChook函数
                doAfterRpcHooks(RemotingHelper.
                     parseChannelRemoteAddr(channel),request, response);
            return response;
        } catch (RemotingSendRequestException e) {
            log.warn("invokeSync: send request exception, so close the 
                channel[{}]", addr);
            //⑦处理通信渠道发送消息请求的异常
            this.closeChannel(addr, channel);
            throw e;
        } catch (RemotingTimeoutException e) {
            //⑧处理通信渠道响应超时的异常
            if (nettyClientConfig.isClientCloseSocketIfTimeout()) {
                this.closeChannel(addr, channel);
            }
                throw e;
        }
    } else {
        //⑨如果通信渠道部可用，则关闭通信渠道，再抛出“通信渠道连接失败”的异常
        this.closeChannel(addr, channel);
        throw new RemotingConnectException(addr);
    }
}
```

