调用MQClientAPIImpl类的sendMessageAsync()方法异步地生产消息（为了方便大家阅读，这里省略了方法的部分入参，如果需要了解更多，可以查阅相关源码），具体代码如下:

```java
private void sendMessageAsync(final String addr,final String brokerName，…)    
    throws InterruptedException,RemotingException {
    final long beginStartTime = System.currentTimeMillis();
    //①调用NettyRemotingClient类的invokeAsync()方法异步地生产消息
    this.remotingClient.invokeAsync(addr, request, timeoutMillis, new 
        InvokeCallback() {
        //②定义一个通知生产消息结果的异步回调函数
        @Override
        public void operationComplete(ResponseFuture responseFuture) {
            long cost = System.currentTimeMillis() - beginStartTime;
            //③从异步结果对象ResponseFuture 中获取异步生产消息的结果
            RemotingCommand response = responseFuture.getResponseCommand();
            if (null == sendCallback && response != null) {
                try {
                    //④如果调用者没有自定义生产消息的回调类对象sendCallback，则只调 
                     用processSendResponse()方法处理生产消息的结果，不回调通知结果
                    SendResult sendResult = MQClientAPIImpl.this.
                        processSendResponse(brokerName, msg, response, addr);
                    if (context != null && sendResult != null) {
                        context.setSendResult(sendResult);
                        context.getProducer()
.executeSendMessageHookAfter(context);
                    }
                } catch (Throwable e) {
                }
            //⑤更新Broker Server的延迟故障的策略，不触发Broker Server的隔离时间
            producer.updateFaultItem(brokerName,System.currentTimeMillis() 
                    - responseFuture.getBeginTimestamp(), false);
                return;
            }
            if (response != null) {
                try {
            //⑥如果调用者没自定义生产消息的回调类对象sendCallback，则既调用
              processSendResponse()方法处理生产消息的结果，也要回调通知结果
                    SendResult sendResult = MQClientAPIImpl.this.
                        processSendResponse(brokerName, msg, response, addr);
                    assert sendResult != null;
                    if (context != null) {
                        context.setSendResult(sendResult);
                        context.getProducer()
.executeSendMessageHookAfter(context);
                    }
                    try {
                        sendCallback.onSuccess(sendResult);
                    } catch (Throwable e) {
                    }                  
                    producer.updateFaultItem(brokerName,System.
                        currentTimeMillis()–     
                            responseFuture.getBeginTimestamp(), false);
                } catch (Exception e) {
                //⑦处理Exception 异常，并更新Broker Server的延迟故障的策略，触发  
                    Broker Server的隔离时间
                    producer.updateFaultItem(brokerName,   
                        System.currentTimeMillis()- responseFuture.
                            getBeginTimestamp(), true);
                    onExceptionImpl(brokerName, msg, timeoutMillis - cost, 
                        request, sendCallback, topicPublishInfo, instance,
                            retryTimesWhenSendFailed, times, e, context, false, 
                                producer);
                }
            } else {
 //⑧处理生产消息不成功、超时和通信渠道客户端未知异常，并更新Broker Server的延迟
   故障的策略，触发Broker Server的隔离时间。如果需要重试，则重新发起一次异步生产消息
                producer.updateFaultItem(brokerName, System.
                    currentTimeMillis() - responseFuture.getBeginTimestamp(), 
                        true);
                //⑨处理生产消息不成功的异常
                if (!responseFuture.isSendRequestOK()) {
                    MQClientException ex = new MQClientException
                       ("send request failed", responseFuture.getCause());
                    onExceptionImpl(brokerName, msg, timeoutMillis - cost, 
                        request, sendCallback, topicPublishInfo, instance,
                            retryTimesWhenSendFailed, times, ex, context, true, 
                                producer);
                //⑩处理生产消息通信渠道响应超时的异常
                } else if (responseFuture.isTimeout()) {
                    MQClientException ex = new MQClientException("wait response  
                        timeout " + responseFuture.getTimeoutMillis() + "ms",
                            responseFuture.getCause());
                     onExceptionImpl(brokerName, msg, timeoutMillis - cost, 
                         request, sendCallback, topicPublishInfo, instance,
                            retryTimesWhenSendFailed, times, ex, context, true, 
                                producer);
                } else {
                    //处理未知的异常
                    MQClientException ex = new MQClientException("unknow 
                        reseaon", responseFuture.getCause());
                        onExceptionImpl(brokerName, msg, timeoutMillis - cost, 
                            request, sendCallback, topicPublishInfo, instance,
                            retryTimesWhenSendFailed, times, ex, context, true, 
                               producer);
                }
            }
        }
    });
}
```

