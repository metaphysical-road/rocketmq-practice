用RemotingCommand类的encodeHeader()方法完成通信协议的编码，具体代码实现如下：

```java
public ByteBuffer encodeHeader() {
    //①调用方法encodeHeader()完成消息的编码，其中，body为消息主体数据
    return encodeHeader(this.body != null ? this.body.length : 0);
}
//②编码通信协议的消息头
public ByteBuffer encodeHeader(final int bodyLength) {
    //③定义消息头的长度为4个字节
    int length = 4;
    //④定义消息头数据
    byte[] headerData;
    //⑤使用序列化框架完成消息头数据的序列化，默认采用json类型
    headerData = this.headerEncode();
    length += headerData.length;
    //⑥定义消息主数据的长度
    length += bodyLength;
    ByteBuffer result = ByteBuffer.allocate(4 + length - bodyLength);
    //⑦将消息的总长度设置到通信协议中
    result.putInt(length);
    //⑧将消息头的长度设置到通信协议中
    result.put(markProtocolType(headerData.length, 
      serializeTypeCurrentRPC));
    //⑨将消息头数据设置到通信协议中
    result.put(headerData);
    //⑩反转NIO字节缓冲区的读/写指针，并将读/写指针的position指向缓冲区的头部
    result.flip();
    return result;
}
```

