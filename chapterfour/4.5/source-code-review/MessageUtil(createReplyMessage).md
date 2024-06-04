下面看一下RocketMQ提供的工具类是如何创建应答消息的，具体代码如下：

```java
public static Message createReplyMessage(final Message requestMessage, final 
    byte[] body) throws MQClientException {
    //requestMessage指需要消费的消息
    if (requestMessage != null) {
        //构造一条应答消息
        Message replyMessage = new Message();
        //从消息属性列表中获取MessageConst.PROPERTY_CLUSTER的值，主要是Broker 
            Server的集群名称
        String cluster = requestMessage.getProperty(MessageConst.
            PROPERTY_CLUSTER);
        //从消息属性列表中获取生产者的客户端ID
        String replyTo = requestMessage.getProperty(MessageConst.
            PROPERTY_MESSAGE_REPLY_TO_CLIENT);
        //从消息属性列表中获取全局correlationId
        String correlationId = requestMessage.getProperty(MessageConst.
            PROPERTY_CORRELATION_ID);
        //从消息的属性列表中获取MessageConst.PROPERTY_MESSAGE_TTL，主要是生产
消息的超时时间
        String ttl = requestMessage.getProperty(MessageConst.
            PROPERTY_MESSAGE_TTL);
        //设置消息体
        replyMessage.setBody(body);
        if (cluster != null) {
            //组装生产响应消息的消息主题，规则为 “集群名称 + — + REPLY_TOPIC”
            String replyTopic = MixAll.getReplyTopic(cluster);
            //设置消息主题
            replyMessage.setTopic(replyTopic);
            //向响应消息中设置消息类型为“请求/响应”消息
            MessageAccessor.putProperty(replyMessage, MessageConst.
               PROPERTY_MESSAGE_TYPE, MixAll.REPLY_MESSAGE_FLAG);
            //向响应消息中设置全局correlationId，这样即可将其和Producer生产的消息关联起来
            MessageAccessor.putProperty(replyMessage, MessageConst.
                PROPERTY_CORRELATION_ID, correlationId);
            //向响应消息中设置生产者的客户端ID，这样即可将其和对应的Producer客户端关
                联起来
            MessageAccessor.putProperty(replyMessage, MessageConst.
                PROPERTY_MESSAGE_REPLY_TO_CLIENT, replyTo);
            //设置生产消息的超时时间
            MessageAccessor.putProperty(replyMessage, MessageConst.
                PROPERTY_MESSAGE_TTL, ttl);
            //返回响应消息
            return replyMessage;
         } else {
           throw new MQClientException(ClientErrorCode.
               CREATE_REPLY_MESSAGE_EXCEPTION, "create reply message fail, 
                   requestMessage error, property[" + MessageConst.
                           PROPERTY_CLUSTER + "] is null.");
        }
    }
    throw new MQClientException(ClientErrorCode.
        CREATE_REPLY_MESSAGE_EXCEPTION, "create reply message fail, 
            requestMessage cannot be null.");
}
```

