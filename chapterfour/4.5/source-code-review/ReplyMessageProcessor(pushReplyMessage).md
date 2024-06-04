用Broker Server的ReplyMessageProcessor类的pushReplyMessage()方法，向Producer推送“Consumer消费消息成功”的应答消息，具体代码如下：

```java
private PushReplyResult pushReplyMessage(final ChannelHandlerContext ctx,
        final SendMessageRequestHeader requestHeader,
        final Message msg) {
    //①构造一个应答消息的RPC请求对象的请求头ReplyMessageRequestHeader
    ReplyMessageRequestHeader replyMessageRequestHeader = 
            new ReplyMessageRequestHeader();
    replyMessageRequestHeader.setProducerGroup(requestHeader.
        getProducerGroup());
    replyMessageRequestHeader.setTopic(requestHeader.getTopic());
    ...
    replyMessageRequestHeader.setUnitMode(requestHeader.isUnitMode());
    //②构造一个命令任务RemotingCommand，编码为
         RequestCode.PUSH_REPLY_MESSAGE_TO_CLIENT
    RemotingCommand request = RemotingCommand.createRequestCommand(
    RequestCode.PUSH_REPLY_MESSAGE_TO_CLIENT, replyMessageRequestHeader);
    //③设置当前请求的消息体
    request.setBody(msg.getBody());
    //④从当前请求的消息体中，获取Producer的通信渠道客户端ID
    String senderId = msg.getProperties().get(MessageConst.
        PROPERTY_MESSAGE_REPLY_TO_CLIENT);
    PushReplyResult pushReplyResult = new PushReplyResult(false);
    if (senderId != null) {
        //⑤从Broker Server的生产者本地缓存中，获取指定通信渠道客户端ID的Netty通
信通道
        Channel channel = this.brokerController.getProducerManager().
            findChannel(senderId);
        //⑥如果通信通道不为空，执行推送应答消息的逻辑
        if (channel != null) {
            msg.getProperties().put(MessageConst.PROPERTY_PUSH_REPLY_TIME, 
                String.valueOf(System.currentTimeMillis()));
            replyMessageRequestHeader.setProperties(MessageDecoder.
                messageProperties2String(msg.getProperties()));
            try {
                //⑦调用Broker2Client类的callClient()方法，向Producer推送应答
                   消息
                RemotingCommand pushResponse = this.brokerController.
                    getBroker2Client().callClient(channel, request);
                assert pushResponse != null;
                //⑧处理推送应答消息的结果
                switch (pushResponse.getCode()) {
                    case ResponseCode.SUCCESS: {
                        pushReplyResult.setPushOk(true);
                        break;
                    }
                    default: {
                        pushReplyResult.setPushOk(false);
                        pushReplyResult.setRemark("push reply message to " + 
                            senderId + "fail.");
                        log.warn("push reply message to <{}> return fail, 
                             response remark: {}", senderId, pushResponse.
                             getRemark());
                    }
                }
            } catch (RemotingException | InterruptedException e) {
                //⑨处理RemotingException异常和InterruptedException异常，并设
                    置推送的结果为false
                pushReplyResult.setPushOk(false);
                pushReplyResult.setRemark("push reply message to " + senderId 
                    + "fail.");
          }
        } else {
            //⑩如果通信通道为空，则设置应答请求对象的结果为false
            pushReplyResult.setPushOk(false);
            pushReplyResult.setRemark("push reply message fail, channel of <" 
               + senderId + "> not found.");
            log.warn(pushReplyResult.getRemark());
        }
    } else {
        pushReplyResult.setPushOk(false);
        pushReplyResult.setRemark("reply message properties[" + 
            MessageConst.PROPERTY_MESSAGE_REPLY_TO_CLIENT + "] is null");
    }
    return pushReplyResult;
}
```

