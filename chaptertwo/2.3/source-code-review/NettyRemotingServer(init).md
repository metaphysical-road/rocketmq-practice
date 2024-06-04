初始化服务端通信渠道的Netty的配置信息、通信渠道监听器、Netty服务端启动类和处理RocketMQ服务端通信渠道事件的线程池，具体代码如下:

```java
public class NettyRemotingServer extends NettyRemotingAbstract implements 
RemotingServer {
    private final ServerBootstrap serverBootstrap;
    private final NettyServerConfig nettyServerConfig;
    private final ExecutorService publicExecutor;
    private final ChannelEventListener channelEventListener;
    private final ExecutorService  publicExecutor;

    public NettyRemotingServer(final NettyServerConfig 
nettyServerConfig,final ChannelEventListener channelEventListener) {
        //①初始化“最多发送一次”和“异步”模式服务端的信号量个数，前者默认为256个，
后者默认为64个
        super(nettyServerConfig.getServerOnewaySemaphoreValue(), 
nettyServerConfig.getServerAsyncSemaphoreValue());
        //②初始化一个Netty服务端启动对象ServerBootstrap
        this.serverBootstrap = new ServerBootstrap();
        this.nettyServerConfig = nettyServerConfig;
        //③初始化服务端通信渠道监听器
        this.channelEventListener = channelEventListener;
        //④加载处理RocketMQ服务端通信渠道事件的线程池中的核心线程数，默认为4个
        int publicThreadNums = 
            nettyServerConfig.getServerCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }
        //⑤初始化一个处理RocketMQ服务端通信渠道事件的线程池
        this.publicExecutor = Executors.newFixedThreadPool(
            publicThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            //⑥新建一个线程
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyServerPublicExecutor_" + 
                    this.threadIndex.incrementAndGet());
            }
        });
        ...
    }
}

```

