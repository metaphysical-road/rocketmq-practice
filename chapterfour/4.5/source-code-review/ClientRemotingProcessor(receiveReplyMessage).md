Producer解析Broker Server的应答消息，具体代码实现如下：

```java
private RemotingCommand receiveReplyMessage(ChannelHandlerContext ctx,
    RemotingCommand request) throws RemotingCommandException {
    final RemotingCommand response = RemotingCommand.
        createResponseCommand(null);
    long receiveTime = System.currentTimeMillis();
    //①解析出ReplyMessageRequestHeader
    ReplyMessageRequestHeader requestHeader = (ReplyMessageRequestHeader)       
      request.decodeCommandCustomHeader(ReplyMessageRequestHeader.class);
    try {
        MessageExt msg = new MessageExt();
        //②构造消息，设置消息主题
        msg.setTopic(requestHeader.getTopic());
        //③构造消息，设置消息队列ID
        msg.setQueueId(requestHeader.getQueueId());
        //④构造消息，设置存储消息的时间
        msg.setStoreTimestamp(requestHeader.getStoreTimestamp());
        //⑤构造消息，设置存储消息的机器IP地址
        if (requestHeader.getBornHost() != null) {
            msg.setBornHost(RemotingUtil.string2SocketAddress
               (requestHeader.getBornHost()));
        }
        if (requestHeader.getStoreHost() != null) {
            msg.setStoreHost(RemotingUtil.string2SocketAddress
               (requestHeader.getStoreHost()));
        }
        //⑥解析应答消息中的消息体
        byte[] body = request.getBody();
        if ((requestHeader.getSysFlag() & MessageSysFlag.COMPRESSED_FLAG)
            == MessageSysFlag.COMPRESSED_FLAG) {
            try {
                body = UtilAll.uncompress(body);
            } catch (IOException e) {
                log.warn("err when uncompress constant", e);
            }
        }
        //⑦构造消息，设置消息体
        msg.setBody(body);
        msg.setFlag(requestHeader.getFlag());
        MessageAccessor.setProperties(msg, MessageDecoder.
                string2messageProperties(requestHeader.getProperties()));
        MessageAccessor.putProperty(msg, MessageConst.
          PROPERTY_REPLY_MESSAGE_ARRIVE_TIME, String.valueOf(receiveTime));
        //⑧构造消息，设置生产消息的时间
        msg.setBornTimestamp(requestHeader.getBornTimestamp());
        msg.setReconsumeTimes(requestHeader.getReconsumeTimes() == null ? 
0:requestHeader.getReconsumeTimes());
        log.debug("receive reply message :{}", msg);
        //⑨处理应答消息	
        processReplyMessage(msg);
        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
    } catch (Exception e) {
        log.warn("unknown err when receiveReplyMsg", e);
        response.setCode(ResponseCode.SYSTEM_ERROR);
        response.setRemark("process reply message fail");
    }
    //⑩将处理应答消息的结果返给Broker Server
    return response;
}
```

