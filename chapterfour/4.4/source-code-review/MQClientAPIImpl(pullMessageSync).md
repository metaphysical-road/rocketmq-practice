用pullMessageSync()方法同步拉取消息的具体代码如下：

```java
private PullResult pullMessageSync(final String addr,final RemotingCommand
     request,final long timeoutMillis) throws RemotingException, 
         InterruptedException, MQBrokerException {
    //①调用通信渠道客户端NettyRemotingClient类的invokeSync()方法，同步地调用服
        务端Broker Server并拉取消息
    RemotingCommand response = this.remotingClient.invokeSync(addr,request,   
       timeoutMillis);
    assert response != null;
    //②处理Broker Server返回的结果，并返回结果对象PullResult 
    return this.processPullResponse(response, addr);
}
```

