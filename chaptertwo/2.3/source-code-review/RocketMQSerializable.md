### 序列化的具体代码

```java
public class RocketMQSerializable {
    public static byte[] rocketMQProtocolEncode(RemotingCommand cmd) {
        //①计算remark字段数据的长度
        byte[] remarkBytes = null;
        int remarkLen = 0;
        if (cmd.getRemark() != null && cmd.getRemark().length() > 0) {
            remarkBytes = cmd.getRemark().getBytes(CHARSET_UTF8);
            remarkLen = remarkBytes.length;
        }
        //②计算extFields字段数据的长度
        byte[] extFieldsBytes = null;
        int extLen = 0;
        if (cmd.getExtFields() != null && !cmd.getExtFields().isEmpty()) {
            extFieldsBytes = mapSerialize(cmd.getExtFields());
            extLen = extFieldsBytes.length;
        }
        int totalLen = calTotalLen(remarkLen, extLen);
        //③构造一个NIO字节缓冲区
        ByteBuffer headerBuffer = ByteBuffer.allocate(totalLen);
        //④向NIO字节缓冲区中设置code
        headerBuffer.putShort((short) cmd.getCode());
        //⑤向NIO字节缓冲区中设置language
        headerBuffer.put(cmd.getLanguage().getCode());
        //⑥向NIO字节缓冲区中设置version
        headerBuffer.putShort((short) cmd.getVersion());
        //⑦向NIO字节缓冲区中设置opaque
        headerBuffer.putInt(cmd.getOpaque());
        //⑧向NIO字节缓冲区中设置flag
        headerBuffer.putInt(cmd.getFlag());
        //⑨向NIO字节缓冲区中设置remark
        if (remarkBytes != null) {
            headerBuffer.putInt(remarkBytes.length);
            headerBuffer.put(remarkBytes);
        } else {
            headerBuffer.putInt(0);
        }
        //⑩向NIO字节缓冲区中设置extFields
        if (extFieldsBytes != null) {
            headerBuffer.putInt(extFieldsBytes.length);
            headerBuffer.put(extFieldsBytes);
        } else {
            headerBuffer.putInt(0);
        }
        return headerBuffer.array();
    }
    ...
}
```

### 反序列化的具体代码

```java
public class RocketMQSerializable {
   public static RemotingCommand rocketMQProtocolDecode(final byte[] headerArray) {
        RemotingCommand cmd = new RemotingCommand();
        //①将byte类型的数组转换为NIO字节缓冲区
        ByteBuffer headerBuffer = ByteBuffer.wrap(headerArray);
        //②解析字段code
        cmd.setCode(headerBuffer.getShort());
        //③解析字段language
        cmd.setLanguage(LanguageCode.valueOf(headerBuffer.get()));
        //④解析字段version
        cmd.setVersion(headerBuffer.getShort());
        //⑤解析字段opaque
        cmd.setOpaque(headerBuffer.getInt());
        //⑥解析字段flag
        cmd.setFlag(headerBuffer.getInt());
        //⑦解析字段remark
        int remarkLength = headerBuffer.getInt();
        if (remarkLength > 0) {
            byte[] remarkContent = new byte[remarkLength];
            headerBuffer.get(remarkContent);
            cmd.setRemark(new String(remarkContent, CHARSET_UTF8));
        }
        //⑧解析字段extFields
        int extFieldsLength = headerBuffer.getInt();
        if (extFieldsLength > 0) {
            byte[] extFieldsBytes = new byte[extFieldsLength];
            headerBuffer.get(extFieldsBytes);
            cmd.setExtFields(mapDeserialize(extFieldsBytes));
        }
        //⑨返回RemotingCommand对象
        return cmd;
    }
    ...
}
```

