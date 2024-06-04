初始化Netty的Reactor NIO线程池，包括boss线程池和处理事件任务的工作线程池，具体代码如下：

```java
 //①如果使用Netty的Epoll NIO通信模型，则使用Reactor 线程池EpollEventLoopGroup
 if (useEpoll()) {
    //②初始化一个boss线程池对象EpollEventLoopGroup
    this.eventLoopGroupBoss = new EpollEventLoopGroup(1, new ThreadFactory() 
{
        private AtomicInteger threadIndex = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, String.format("NettyEPOLLBoss_%d", 
                this.threadIndex.incrementAndGet()));
        }
    });
     //③初始化一个处理事件的工作线程池对象EpollEventLoopGroup
    this.eventLoopGroupSelector = new EpollEventLoopGroup(
        nettyServerConfig.getServerSelectorThreads(), 
        new ThreadFactory() {
        private AtomicInteger threadIndex = new AtomicInteger(0);
        private int threadTotal = 
            nettyServerConfig.getServerSelectorThreads();
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, 
                String.format("NettyServerEPOLLSelector_%d_%d", threadTotal, 
                    this.threadIndex.incrementAndGet()));
        }
    });
//④如果使用Java NIO通信模型，则使用Reactor 线程池NioEventLoopGroup
} else {
    //⑤初始化一个boss线程池对象NioEventLoopGroup
    this.eventLoopGroupBoss = new NioEventLoopGroup(1, 
        new ThreadFactory() {
        private AtomicInteger threadIndex = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, String.format("NettyNIOBoss_%d", 
                this.threadIndex.incrementAndGet()));
        }
    });
    //⑥初始化一个处理事件的工作线程池对象NioEventLoopGroup
    this.eventLoopGroupSelector = new  
        NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads(),
            new ThreadFactory() {
        private AtomicInteger threadIndex = new AtomicInteger(0);
        private int threadTotal = 
            nettyServerConfig.getServerSelectorThreads();
        @Override
        public Thread newThread(Runnable r) {
           //⑦新建一个线程
            return new Thread(r, 
            String.format("NettyServerNIOSelector_%d_%d", threadTotal, 
                this.threadIndex.incrementAndGet()));
        }
    });
}

```

