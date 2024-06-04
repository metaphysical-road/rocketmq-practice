创建一个新的Source Connector的HTTP命令如下，其中IP地址和端口号，还有filename可以结合自己的环境去配置。

http://192.168.0.123:8083/connectors/MySQLCDCSource?config={
"connector.class": "org.apache.rocketmq.connect.debezium.mysql.DebeziumMysqlConnector",
"max.task": "1",
"connect.topicname": "debezium-mysql-source-topic",
"kafka.transforms": "Unwrap",
"kafka.transforms.Unwrap.delete.handling.mode": "none",
"kafka.transforms.Unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
"kafka.transforms.Unwrap.add.headers": "op,source.db,source.table",
"database.history.skip.unparseable.ddl": true,
"database.history.name.srv.addr": "192.168.0.182:9876",
"database.history.rocketmq.topic": "db-history-debezium-topic",
"database.history.store.only.monitored.tables.ddl": true,
"include.schema.changes": false,
"database.server.name": "rocketmq_source_datasource",
"database.port": 3306,
"database.hostname": "127.0.0.1",
"database.connectionTimeZone": "UTC",
"database.user": "root",
"database.password": "123456huxian",
"table.include.list": "rocketmq_source.t_user",
"max.batch.size": 50,
"database.include.list": "rocketmq_source",
"snapshot.mode": "when_needed",
"database.server.id": "184054",
"key.converter": "org.apache.rocketmq.connect.runtime.converter.record.json.JsonConverter",
"value.converter": "org.apache.rocketmq.connect.runtime.converter.record.json.JsonConverter"
}