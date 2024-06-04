创建一个新的Sink Connector的HTTP命令如下，其中IP地址和端口号可以结合自己的环境去配置。

http://192.168.0.123:8083/connectors/mysqlConnectorSink?config={"source-rocketmq":"192.168.0.182:9876","source-cluster":"DefaultCluster",
"connector.class":"org.apache.rocketmq.connect.jdbc.connector.JdbcSinkConnector","dbUrl":"192.168.0.182","dbPort":"3306","dbUsername":"root",
"dbPassword":"123456huxian","connect.topicnames":"t_user","mode":"bulk","source-record-converter":"org.apache.rocketmq.connect.runtime.converter.JsonConverter"}
