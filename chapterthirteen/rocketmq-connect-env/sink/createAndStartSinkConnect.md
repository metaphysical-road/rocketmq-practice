创建一个新的Sink Connector的HTTP命令如下，其中IP地址和端口号，还有filename可以结合自己的环境去配置。

http://192.168.0.123:8082/connectors/fileSinkConnector?config={
"connector.class": "org.apache.rocketmq.connect.file.FileSinkConnector",
"connect.topicnames": "fileTest",
"filename": "/Users/huxian/Downloads/rocketmq-env/rocketmq-connect-standalone/file/sink-file.txt",
"source-record-converter": "org.apache.rocketmq.connect.runtime.converter.JsonConverter"
}

http://192.168.0.123:8083/connectors/fileSinkConnector?config={
"connector.class": "org.apache.rocketmq.connect.file.FileSinkConnector",
"connect.topicnames": "fileTest",
"filename": "/Users/huxian/Downloads/rocketmq-env/rocketmq-connect-node1/file/sink-file.txt",
"source-record-converter": "org.apache.rocketmq.connect.runtime.converter.JsonConverter"
}

http://192.168.0.123:8084/connectors/fileSinkConnector?config={
"connector.class": "org.apache.rocketmq.connect.file.FileSinkConnector",
"connect.topicnames": "fileTest",
"filename": "/Users/huxian/Downloads/rocketmq-env/rocketmq-connect-node2/file/sink-file.txt",
"source-record-converter": "org.apache.rocketmq.connect.runtime.converter.JsonConverter"
}

http://192.168.0.123:8085/connectors/fileSinkConnector?config={
"connector.class": "org.apache.rocketmq.connect.file.FileSinkConnector",
"connect.topicnames": "fileTest",
"filename": "/Users/huxian/Downloads/rocketmq-env/rocketmq-connect-node3/file/sink-file.txt",
"source-record-converter": "org.apache.rocketmq.connect.runtime.converter.JsonConverter"
}