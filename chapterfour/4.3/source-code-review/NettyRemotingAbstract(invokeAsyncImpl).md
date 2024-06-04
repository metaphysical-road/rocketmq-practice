调用NettyRemotingAbstract类的invokeAsyncImpl()方法向Broker Server发起RPC请求，具体代码如下：

```java
public void invokeAsyncImpl(final Channel channel, final RemotingCommand 
    request, final long timeoutMillis,
    final InvokeCallback invokeCallback)
        throws InterruptedException, RemotingTooMuchRequestException, 
            RemotingTimeoutException, RemotingSendRequestException {
    long beginStartTime = System.currentTimeMillis();
    //①获取生产消息请求的唯一自增ID
    final int opaque = request.getOpaque();
    //②从异步信号量池中获取信号量，默认信号量池的容量为65535个，即异步一个生
         产者客户端同时只能同时生产65535条消息。调用者可以自定义信号量池的大小
    boolean acquired = this.semaphoreAsync.tryAcquire(timeoutMillis, 
        TimeUnit.MILLISECONDS);
    //③如果能够获取到信号量，则异步地生产消息
    if (acquired) {
        //④定义一个容量只有一个的信号量池，以保证生产消息过程中的线程安全
        final SemaphoreReleaseOnlyOnce once = new 
            SemaphoreReleaseOnlyOnce(this.semaphoreAsync);
        //⑤计算耗时。如果耗时大于超时时间，则释放信号量，直接抛出“超时”的异常
        long costTime = System.currentTimeMillis() - beginStartTime;
        if (timeoutMillis < costTime) {
            once.release();
            throw new RemotingTimeoutException("invokeAsyncImpl call 
                timeout");
        }
        //⑥初始化一个异步Future响应结果对象ResponseFuture，并绑定通信渠道和生产消
            息请求的唯一自增ID
        final ResponseFuture responseFuture = new ResponseFuture(channel, 
             opaque, timeoutMillis - costTime, invokeCallback, once);
        //⑦将异步Future响应结果对象存储在本地缓存responseTable中
        this.responseTable.put(opaque, responseFuture);
        try {
            channel.writeAndFlush(request).addListener(new 
                ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws 
                    Exception {
        //⑧处理Broker Server返回的响应生产消息请求的结果
                    if (f.isSuccess()) {
                        //⑨如果Broker Server收到生产消息请求，则设置sendRequestOK
                           为true，并返回空对象
                        responseFuture.setSendRequestOK(true);
                        return;
                    }
                    //⑩如果Broker Server没有收到生产消息请求，则执行回调函数通知
                        调用方
                    requestFail(opaque);
                    log.warn("send a request command to channel <{}> failed.", 
                        RemotingHelper.parseChannelRemoteAddr(channel));
                }
            });
        } catch (Exception e) {
            responseFuture.release();
            log.warn("send a request command to channel <" + RemotingHelper.
                parseChannelRemoteAddr(channel) + "> Exception", e);
            throw new RemotingSendRequestException(RemotingHelper.
                parseChannelRemoteAddr(channel), e);
        }
    } else {
        //如果不能获取信号量，则进行异常处理
        if (timeoutMillis <= 0) {
            throw new RemotingTooMuchRequestException("invokeAsyncImpl 
                invoke too fast");
        } else {
            String info =String.format("invokeAsyncImpl tryAcquire semaphore 
            timeout, %dms, waiting thread nums: %d semaphoreAsyncValue: %d",
            timeoutMillis,this.semaphoreAsync.getQueueLength(),this.
                semaphoreAsync.availablePermits());
            log.warn(info);
            throw new RemotingTimeoutException(info);
        }
    }
}
```

