### RemotingSerializable

主要用抽象模板RemotingSerializable类封装fastjson，并实现JSON类型的序列化/反序列化，代码分析如下：

```java
public abstract class RemotingSerializable {
    //①代码统一采用UTF_8的编码格式
    public final static Charset CHARSET_UTF8 = StandardCharsets.UTF_8;
    //②编码阶段，将对象obj进行序列化
    public static byte[] encode(final Object obj) {
        final String json = toJson(obj, false);
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }
    //③调用fastjson的JSON类的toJSONString()方法，完成编码阶段的对象序列化
    public static String toJson(final Object obj, boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }
    //④解码阶段，将byte类型的数组对象反序列化
    public static <T> T decode(final byte[] data, Class<T> classOfT) {
        return fromJson(data, classOfT);
    }
    //⑤调用fastjson的JSON类的parseObject()方法，完成解码阶段的对象的反序列化
    private static <T> T fromJson(byte[] data, Class<T> classOfT) {
        return JSON.parseObject(data, classOfT);
    }
    ...
}
```

