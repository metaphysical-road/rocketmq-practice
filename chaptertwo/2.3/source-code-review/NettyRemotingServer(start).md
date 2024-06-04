启动服务端，具体代码如下所示:

```java
ServerBootstrap childHandler =
    //①通过ServerBootstrap的group()方法，主从线程池
    this.serverBootstrap.group(this.eventLoopGroupBoss, 
        this.eventLoopGroupSelector)
        //②指定通道channel的类型，如果RocketMQ已经配置支持Epoll，则使用
              EpollServerSocketChannel，否则使用默认的NioServerSocketChannel
        .channel(useEpoll() ? EpollServerSocketChannel.class : 
            NioServerSocketChannel.class)
        //③配置ServerSocketChannel的通信连接的选项
        .option(ChannelOption.SO_BACKLOG, 1024)
        .option(ChannelOption.SO_REUSEADDR, true)
        .option(ChannelOption.SO_KEEPALIVE, false)
        //④配置子通道SocketChannel的通信连接的选项
        .childOption(ChannelOption.TCP_NODELAY, true)
        .childOption(ChannelOption.SO_SNDBUF, 
            nettyServerConfig.getServerSocketSndBufSize())
        .childOption(ChannelOption.SO_RCVBUF, 
            nettyServerConfig.getServerSocketRcvBufSize())
        .localAddress(new 
            InetSocketAddress(this.nettyServerConfig.getListenPort()))
        //⑤设置子通道SocketChannel的处理器，通常是与业务功能相关的处理器
        .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                    //⑥设置通信连接处理握手事件的处理器
                   .addLast(defaultEventExecutorGroup, 
                       HANDSHAKE_HANDLER_NAME, handshakeHandler)
                    //⑦设置Netty消息解码处理器
                    .addLast(defaultEventExecutorGroup,
                        encoder,new NettyDecoder(),
                        //⑧设置用于管理通信连接的处理器和处理RocketMQ消息相关事件的
                           处理器
                        new IdleStateHandler(0, 0,nettyServerConfig.
                            getServerChannelMaxIdleTimeSeconds()),
                                connectionManageHandler,serverHandler);
            }
        });
if (nettyServerConfig.isServerPooledByteBufAllocatorEnable()) {
    //⑨开启Netty的内存管理，默认使用PooledByteBufAllocator类
    childHandler.childOption(ChannelOption.ALLOCATOR, 
        PooledByteBufAllocator.DEFAULT);
}
try {
    //⑩调用ServerBootstrap类的bind()方法启动服务端，同步等待客户端的连接
    ChannelFuture sync = this.serverBootstrap.bind().sync();
} catch (InterruptedException e1) {
}

```

