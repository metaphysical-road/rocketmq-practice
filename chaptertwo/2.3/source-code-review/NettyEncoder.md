### 编码器具体代码实现

```java
//①定义一个编码器NettyEncoder类，并继承Netty的编码器MessageToByteEncoder类
@ChannelHandler.Sharable
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {
    @Override
    public void encode(ChannelHandlerContext ctx, RemotingCommand 
       remotingCommand, ByteBuf out)
        throws Exception {
        try {
            //②调用RemotingCommand类的encodeHeader()方法编码请求头数据，并返回
                一个NIO字节缓冲区对象
            ByteBuffer header = remotingCommand.encodeHeader();
            //③将请求头设置到通信渠道中
            out.writeBytes(header);
            //④获取消息主体数据
            byte[] body = remotingCommand.getBody();
            if (body != null) {
                //⑤将消息主体数据设置到通信渠道中
                out.writeBytes(body);
            }
        } catch (Exception e) {
            ...
        }
    }
}
```

