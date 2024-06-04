用异步线程任务NettyEventExecutor类扫描服务端通信渠道监听器中的渠道事件队列，具体代码分析如下：

```java
protected final NettyEventExecutor nettyEventExecutor = new 
     NettyEventExecutor();
//①如果通道事件监听器不为空，则开启异步任务线程，执行run()方法，扫描事件队列中的事件
     NettyEvent
if (this.channelEventListener != null) {
    //②启动异步线程任务
    this.nettyEventExecutor.start();
}
class NettyEventExecutor extends ServiceThread {
    private final LinkedBlockingQueue<NettyEvent> eventQueue = new 
        LinkedBlockingQueue<NettyEvent>();
    private final int maxSize = 10000;
    //③往事件队列中，添加需要处理的事件NettyEvent，事件队列的最大容量为10000，超过
         这个阈值则直接丢弃
    public void putNettyEvent(final NettyEvent event) {
        if (this.eventQueue.size() <= maxSize) {
            this.eventQueue.add(event);
        } else {
            log.warn("event queue size[{}] enough, so drop this event {}", 
                this.eventQueue.size(), event.toString());
        }
    }
    @Override
    public void run() {
        //④获取监听器对象ChannelEventListener
        final ChannelEventListener listener = 
            NettyRemotingAbstract.this.getChannelEventListener();
        while (!this.isStopped()) {
            try {
                //⑤从事件队列中取出事件NettyEvent对象
                NettyEvent event = this.eventQueue.poll(3000, 
                    TimeUnit.MILLISECONDS);
                if (event != null && listener != null) {
                    switch (event.getType()) {
                        case IDLE:
                //⑥如果事件类型为IDLE，则执行监听器的onChannelIdle()方法
                            listener.onChannelIdle(event.getRemoteAddr(), 
                                event.getChannel());
                            break;
                //⑦如果事件类型为CLOSE，则执行监听器的onChannelClose()方法
                        case CLOSE:
                            listener.onChannelClose(event.getRemoteAddr(), 
                                event.getChannel());
                            break;
               //⑧如果事件类型为CONNECT，则执行监听器的onChannelConnect()方法
                        case CONNECT:
                            listener.onChannelConnect(event.getRemoteAddr(), 
                                 event.getChannel());
                            break;
              //⑨如果事件类型为EXCEPTION，则执行监听器的onChannelException()方
                   法
                        case EXCEPTION:
                           listener.onChannelException(event.getRemoteAddr(), 
                                  event.getChannel());
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
```

