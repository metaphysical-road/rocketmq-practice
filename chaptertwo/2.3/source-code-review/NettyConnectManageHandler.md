NettyConnectManageHandler类继承了Netty的ChannelDuplexHandler类，这样即可拦截Netty的通信连接请求，具体代码如下：

```java
@ChannelHandler.Sharable
class NettyConnectManageHandler extends ChannelDuplexHandler {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws 
        Exception {
        ...
        //①注册NIO通信通道
        super.channelRegistered(ctx);
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws 
        Exception {
        ...
        //②取消注册NIO通信通道
        super.channelUnregistered(ctx);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ...
        if (NettyRemotingServer.this.channelEventListener != null) {
            //③如果激活NIO通信通道，则向通信渠道监听器的事件队列中put一个连接事件
            NettyRemotingServer.this.putNettyEvent(new 
                NettyEvent(NettyEventType.CONNECT, remoteAddress, 
                     ctx.channel()));
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) 
        throws Exception {
        ...
        if (NettyRemotingServer.this.channelEventListener != null) {
            //④如果取消激活NIO通信通道，则向通信渠道监听器的事件队列中put一个关闭连
                 接事件
            NettyRemotingServer.this.putNettyEvent(new 
                NettyEvent(NettyEventType.CLOSE, remoteAddress, 
                    ctx.channel()));
        }
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) 
        throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.ALL_IDLE)) {
                ...
                if (NettyRemotingServer.this.channelEventListener != null) {
                    //⑤如果挂起NIO通信通道，则向通信渠道监听器的事件队列中put一个挂
                        起连接事件
                    NettyRemotingServer.this
                        .putNettyEvent(new NettyEvent(NettyEventType.IDLE, 
                            remoteAddress, ctx.channel()));
                }
            }
        }
        ctx.fireUserEventTriggered(evt);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
        throws Exception {
        ...
        if (NettyRemotingServer.this.channelEventListener != null) {
            //⑥如果NIO通信通道出现异常，则向通信渠道监听器的事件队列中put一个连接异
                 常事件
            NettyRemotingServer.this.putNettyEvent(new NettyEvent(
                NettyEventType.EXCEPTION, remoteAddress, ctx.channel()));
        }
        RemotingUtil.closeChannel(ctx.channel());
    }
}

```

