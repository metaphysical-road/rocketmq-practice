用pullMessageAsync()方法异步拉取消息的具体代码如下：

```java
private void pullMessageAsync(
        final String addr,
        final RemotingCommand request,
        final long timeoutMillis,
        final PullCallback pullCallback
    ) throws RemotingException, InterruptedException {
    //①调用通信渠道客户端NettyRemotingClient类的invokeAsync()方法异步地调用服
        务端Broker Server并拉取消息
    this.remotingClient.invokeAsync(addr, request, timeoutMillis, new 
        InvokeCallback() {
        //②用异步回调InvokeCallback类异步地获取Broker Server返回的结果对象
            ResponseFuture 
        @Override
        public void operationComplete(ResponseFuture responseFuture) {
            RemotingCommand response = responseFuture.getResponseCommand();
            if (response != null) {
                try {
                    //③异步处理拉取消息的结果，并生成处理后的结果对象PullResult 
                    PullResult pullResult = MQClientAPIImpl.this.
                        processPullResponse(response, addr);
                    //④用调用者自定义的回调函数PullCallback()，通知拉取消息的结果
                    pullCallback.onSuccess(pullResult);
                } catch (Exception e) {
                    pullCallback.onException(e);
                }
            } else {
                //⑤处理异常，并用自定义的回调函数PullCallback()通知异常信息
                if (!responseFuture.isSendRequestOK()) {
                    pullCallback.onException(new MQClientException("send    
                        request failed to " + addr + ". Request: " + request, 
                                responseFuture.getCause()));
                } else if (responseFuture.isTimeout()) {
                    pullCallback.onException(new MQClientException("wait   
                       response from " + addr + " timeout :" + responseFuture.
                          getTimeoutMillis() + "ms" + ". Request: " + request,
                            responseFuture.getCause()));
                } else {
                    pullCallback.onException(new MQClientException("unknown 
               reason. addr: " + addr + ", timeoutMillis: " + timeoutMillis 
                         + ". Request: " + request, responseFuture.getCause()));
                }
            }
        }
    });
}
```

