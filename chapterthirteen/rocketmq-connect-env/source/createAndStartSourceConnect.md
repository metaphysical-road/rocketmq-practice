创建一个新的Source Connector的HTTP命令如下，其中IP地址和端口号，还有filename可以结合自己的环境去配置。

http://192.168.0.123:8082/connectors/fileSourceConnector?config={
"connector.class": "org.apache.rocketmq.connect.file.FileSourceConnector",
"topic": "fileTest",
"filename": "/Users/huxian/Downloads/rocketmq-env/rocketmq-connect-standalone/file/source-file.txt",
"source-record-converter": "org.apache.rocketmq.connect.runtime.converter.JsonConverter"
}


http://192.168.0.123:8083/connectors/fileSourceConnector?config={
"connector.class": "org.apache.rocketmq.connect.file.FileSourceConnector",
"topic": "fileTest",
"filename": "/Users/huxian/Downloads/rocketmq-env/rocketmq-connect-node1/file/source-file.txt",
"source-record-converter": "org.apache.rocketmq.connect.runtime.converter.JsonConverter"
}

http://192.168.0.123:8084/connectors/fileSourceConnector?config={
"connector.class": "org.apache.rocketmq.connect.file.FileSourceConnector",
"topic": "fileTest",
"filename": "/Users/huxian/Downloads/rocketmq-env/rocketmq-connect-node2/file/source-file.txt",
"source-record-converter": "org.apache.rocketmq.connect.runtime.converter.JsonConverter"
}

http://192.168.0.123:8085/connectors/fileSourceConnector?config={
"connector.class": "org.apache.rocketmq.connect.file.FileSourceConnector",
"topic": "fileTest",
"filename": "/Users/huxian/Downloads/rocketmq-env/rocketmq-connect-node3/file/source-file.txt",
"source-record-converter": "org.apache.rocketmq.connect.runtime.converter.JsonConverter"
}