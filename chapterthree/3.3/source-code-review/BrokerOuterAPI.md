用BrokerOuterAPI类的registerBroker()方法构造注册Broker Server信息的任务事件RemotingCommand对象，具体代码如下：

```java
private RegisterBrokerResult registerBroker(
        final String namesrvAddr,
        final boolean oneway,
        final int timeoutMills,
        final RegisterBrokerRequestHeader requestHeader,
        final byte[] body
    ) throws RemotingCommandException, MQBrokerException, 
      RemotingConnectException, RemotingSendRequestException, 
      RemotingTimeoutException,InterruptedException {
    //①构造注册Broker Server信息的命令事件，事件编码为103
    RemotingCommand request = RemotingCommand.
        createRequestCommand(RequestCode.REGISTER_BROKER, requestHeader);
    //②将消息体设置到事件任务中
    request.setBody(body);
    if (oneway) {
        try {
            //③如果是”只发送一次模式”，则调用通信渠道客户端的invokeOneway()方法，
                注册Broker Server信息
            this.remotingClient.invokeOneway(namesrvAddr, request, 
                timeoutMills);
        } catch (RemotingTooMuchRequestException e) {
        }
        return null;
    }
    //④否则，调用通信渠道客户端的invokeSync()方法，采用”同步模式”注册Broker 
        Server信息
    RemotingCommand response = this.remotingClient.invokeSync(namesrvAddr, 
        request, timeoutMills);
    assert response != null;
    //⑤同步处理Name Server返回的结果
    switch (response.getCode()) {
        case ResponseCode.SUCCESS: {
            //⑥解码响应请求，并将其转换为RegisterBrokerResponseHeader对象
            RegisterBrokerResponseHeader responseHeader =
                (RegisterBrokerResponseHeader)response.               
                       decodeCommandCustomHeader(
                           RegisterBrokerResponseHeader.class);
            RegisterBrokerResult result = new RegisterBrokerResult();
            //⑦设置角色为Master的Broker Server节点的IP地址
            result.setMasterAddr(responseHeader.getMasterAddr());
            //⑧设置角色为Slave的Broker Server节点的IP地址
            result.setHaServerAddr(responseHeader.getHaServerAddr());
            if (response.getBody() != null) {
                //⑨设置Name Server返回的键值对信息
                result.setKvTable(KVTable.decode(response.getBody(), 
                    KVTable.class));
            }
            //⑩返回注册Broker Server的结果
            return result;
        }
        default:
            break;
    }
    throw new MQBrokerException(response.getCode(), response.getRemark(), 
        requestHeader == null ? null : requestHeader.getBrokerAddr());
}

```

