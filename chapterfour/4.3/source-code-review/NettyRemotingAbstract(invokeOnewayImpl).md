调用NettyRemotingAbstract类的invokeOnewayImpl()方法向Broker Server发起RPC请求，具体代码如下:

```java
public void invokeOnewayImpl(final Channel channel, final RemotingCommand 
    request, final long timeoutMillis)
    throws InterruptedException, RemotingTooMuchRequestException, 
        RemotingTimeoutException, RemotingSendRequestException {
    //①标识RPC请求类型为RPC_ONEWAY
    request.markOnewayRPC();
    //②从容量为65535的信号量池中获取信号量，代表最大并发量为65535
    boolean acquired = this.semaphoreOneway.tryAcquire(timeoutMillis, 
       TimeUnit.MILLISECONDS);
     //③如果获取信号量成功，则可以生产消息
    if (acquired) {
        //④定义一个容量为1的信号量池，以保证生产消息过程中的线程安全
        final SemaphoreReleaseOnlyOnce once = new 
           SemaphoreReleaseOnlyOnce(this.semaphoreOneway);
        try {
            //⑤通信渠道客户端向通信渠道服务端发送RPC请求
            channel.writeAndFlush(request).addListener(new 
                ChannelFutureListener() {
                //⑥用监听器监听Netty通信渠道的响应结果
                @Override
                public void operationComplete(ChannelFuture f) throws 
                    Exception {           
               //⑦在发送一次后，无论发送成功还是失败，直接释放当前生产消息请求的信号量
                    once.release();
                    if (!f.isSuccess()) {
                    //⑧如果不成功，则只打印一条日志
                        log.warn("send a request command to channel <" + 
                            channel.remoteAddress() + "> failed.");
                    }
                }
            });
        } catch (Exception e) {
            //⑨处理异常Exception，并释放当前生产消息请求的信号量
            once.release();
            log.warn("write send a request command to channel <" + 
                channel.remoteAddress() + "> failed.");
            throw new RemotingSendRequestException(
                RemotingHelper.parseChannelRemoteAddr(channel), e);
        }
    } else {
        //⑩处理超时异常
        if (timeoutMillis <= 0) {
            throw new RemotingTooMuchRequestException("invokeOnewayImpl 
                invoke too fast");
        } else {
            String info = String.format(
                "invokeOnewayImpl tryAcquire semaphore timeout, %dms, waiting 
                    thread nums: %d semaphoreOnewayValue: %d",
                timeoutMillis,this.semaphoreOneway.getQueueLength()
                    ,this.semaphoreOneway.availablePermits());
            log.warn(info);
            throw new RemotingTimeoutException(info);
        }
    }
}
```

