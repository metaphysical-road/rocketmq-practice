为了提高命令事件处理器的并发性，RocketMQ会为不同的命令时间处理器，单独生成一个线程池去处理RPC命令事件，具体代码如下所示：

```java
//①源码来自于NettyRemotingClient类的registerProcessor()方法
@Override
public void registerProcessor(int requestCode, NettyRequestProcessor 、
    processor, ExecutorService executor) {
    ExecutorService executorThis = executor;
    //②如果在注册命令事件处理器时，调用方已经自定义了线程池，则使用自定义的线程池，否
      则使用公共线程池
    if (null == executor) {
        executorThis = this.publicExecutor;
    }
    //③构造命令事件处理器和线程池的键值对
    Pair<NettyRequestProcessor, ExecutorService> pair = new 
        Pair<NettyRequestProcessor, 
        ExecutorService>(processor, executorThis);
        //④将键值对设置到本地缓存中
        this.processorTable.put(requestCode, pair);
}

```

