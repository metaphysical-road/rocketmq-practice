则可以修改源码以开启自定义线程池的高并发性的机制，具体需要修改的代码如下所示：

```java
private final ExecutorService checkTransactionStateExecutor;
//①自定义一个独立的线程池
this.checkTransactionStateExecutor = Executors.newFixedThreadPool
    (publicThreadNums, new ThreadFactory() {
    private AtomicInteger threadIndex = new AtomicInteger(0);
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "NettyClientPublicExecutor_" + 
           this.threadIndex.incrementAndGet());
    }
});

public MQClientAPIImpl(final NettyClientConfig nettyClientConfig,
    final ClientRemotingProcessor clientRemotingProcessor,
    RPCHook rpcHook, final ClientConfig clientConfig) {
    ...
    //②注册回查分布式事务消息中的本地事务状态的命令事件处理器，并设置线程池
    this.remotingClient.registerProcessor(RequestCode.
            CHECK_TRANSACTION_STATE, this.clientRemotingProcessor, 
                checkTransactionStateExecutor);
    ...
}
```

