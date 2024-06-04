用MQClientAPIImpl类的popMessageAsync()方法拉取消息，具体代码如下：

```java
public void popMessageAsync(
    final String brokerName, final String addr, final 
        PopMessageRequestHeader requestHeader,final long timeoutMillis, 
            final PopCallback popCallback
    ) throws RemotingException, InterruptedException {
    //①构造一个编码为RequestCode.POP_MESSAGE的命令事件对象RemotingCommand 
final RemotingCommand request = RemotingCommand.createRequestCommand
    (RequestCode.POP_MESSAGE, requestHeader);
//②异步地从Broker Server拉取取消息
    this.remotingClient.invokeAsync(addr, request, timeoutMillis, new 
        BaseInvokeCallback(MQClientAPIImpl.this) {
        @Override
        public void onComplete(ResponseFuture responseFuture) {
        //③返回的结果对象ResponseFuture中获取拉取消息的结果 
            RemotingCommand response = 
responseFuture.getResponseCommand();
            if (response != null) {
                try {
                    //④处理拉取消息的结果，并生成结果对象PopResult 
                    PopResult popResult = MQClientAPIImpl.this
                          .processPopResponse(brokerName, response,
                requestHeader.getTopic(), requestHeader);
                    assert popResult != null;
                    //⑤用回调类popCallback，通知拉取消息的结果
                    popCallback.onSuccess(popResult);
                } catch (Exception e) {
                    popCallback.onException(e);
                }
            } else {
                //⑥用回调函数通知调用者，拉取消息请求不成功的异常
                if (!responseFuture.isSendRequestOK()) {
                    popCallback.onException(new MQClientException("send    
                      request failed to " + addr + ". Request: " + request, 
                                responseFuture.getCause()));
                //⑦用回调函数通知调用者，拉取消息超时的异常
                } else if (responseFuture.isTimeout()) {
                    popCallback.onException(new MQClientException("wait 
                    response from " + addr + " timeout :" + responseFuture.
                          getTimeoutMillis() + "ms" + ". Request: " + request,
                        responseFuture.getCause()));
                //⑧用回调函数通知调用者，拉取消息通信渠道客户端的异常
                } else {
                    popCallback.onException(new MQClientException("unknown 
                        reason. addr: " + addr + ", timeoutMillis: " + 
                            timeoutMillis +". Request: " + request, 
                            responseFuture.getCause()));
                }
            }
        }
    });
}
```

