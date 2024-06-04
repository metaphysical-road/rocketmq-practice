用RemotingCommand类的decode()方法完成通信协议的解码，具体代码如下:

```java
public static RemotingCommand decode(final ByteBuffer byteBuffer) {
    int length = byteBuffer.limit();
    int oriHeaderLen = byteBuffer.getInt();
    int headerLength = getHeaderLength(oriHeaderLen);
    byte[] headerData = new byte[headerLength];
    //①从Netty的NIO通信通道中解析出消息头数据
    byteBuffer.get(headerData);
    //②解码消息头数据
    RemotingCommand cmd = headerDecode(headerData, 
        getProtocolType(oriHeaderLen));
    int bodyLength = length - 4 - headerLength;
    byte[] bodyData = null;
    if (bodyLength > 0) {
        //③从Netty的NIO通信通道中解析出消息主体数据
        bodyData = new byte[bodyLength];
        byteBuffer.get(bodyData);
    }
    cmd.body = bodyData;
    return cmd;
}
private static RemotingCommand headerDecode(byte[] headerData, 
    SerializeType type) {
        switch (type) {
    case JSON:
        //④如果采用JSON序列化方式，则调用RemotingSerializable类的decode()方法
           完成消息头数据的解码
        RemotingCommand resultJson = RemotingSerializable.decode(
            headerData, RemotingCommand.class);
        resultJson.setSerializeTypeCurrentRPC(type);
        return resultJson;
    case ROCKETMQ:
        //⑤如果采用ROCKETMQ序列化方式，则调用RocketMQSerializable类的
        rocketMQProtocolDecode()方法完成消息头数据的解码
        RemotingCommand resultRMQ = 
            RocketMQSerializable.rocketMQProtocolDecode(headerData);
        resultRMQ.setSerializeTypeCurrentRPC(type);
        return resultRMQ;
    default:
        break;
    }
    return null;
}
```

