-- MySQL dump 10.13  Distrib 8.0.20, for macos10.15 (x86_64)
--
-- Host: 127.0.0.1    Database: rocketmq_eventbridge
-- ------------------------------------------------------
-- Server version	8.0.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `event_api_destination`
--

DROP TABLE IF EXISTS `event_api_destination`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_api_destination` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) NOT NULL COMMENT 'event_api_destination account id',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称',
  `protocol` varchar(128) NOT NULL DEFAULT '' COMMENT '类型',
  `api_params` text NOT NULL COMMENT 'API 参数',
  `connection_name` varchar(128) DEFAULT NULL COMMENT '连接信息',
  `invocation_rate_limit_per_second` int DEFAULT NULL COMMENT '每秒推送速率',
  `description` varchar(255) DEFAULT NULL COMMENT 'a description about the event_api_destination',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='event api destination meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_api_destination`
--

LOCK TABLES `event_api_destination` WRITE;
/*!40000 ALTER TABLE `event_api_destination` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_api_destination` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_bus`
--

DROP TABLE IF EXISTS `event_bus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_bus` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) NOT NULL COMMENT 'bus account id',
  `name` varchar(255) NOT NULL COMMENT 'bus name',
  `description` varchar(256) DEFAULT NULL COMMENT 'bus description',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`account_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='event bus meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_bus`
--

LOCK TABLES `event_bus` WRITE;
/*!40000 ALTER TABLE `event_bus` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_bus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_connection`
--

DROP TABLE IF EXISTS `event_connection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_connection` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) NOT NULL COMMENT 'event_connection account id',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称',
  `authorization_type` varchar(128) DEFAULT NULL COMMENT '授权类型',
  `auth_parameters` text,
  `network_type` varchar(128) NOT NULL DEFAULT '' COMMENT '网络类型',
  `network_parameters` text COMMENT '网络配置',
  `description` varchar(255) DEFAULT NULL COMMENT 'a description about the event_connection',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='event connection meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_connection`
--

LOCK TABLES `event_connection` WRITE;
/*!40000 ALTER TABLE `event_connection` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_connection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_rule`
--

DROP TABLE IF EXISTS `event_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) NOT NULL COMMENT 'bus account id',
  `bus` varchar(255) NOT NULL COMMENT 'bus name',
  `name` varchar(255) NOT NULL COMMENT 'rule name',
  `filter_pattern` varchar(4096) DEFAULT NULL COMMENT 'event filter pattern',
  `status` tinyint NOT NULL COMMENT '0:disable, 1:enable',
  `description` varchar(255) DEFAULT NULL COMMENT 'a description about the event rule',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `name_uniq_key` (`account_id`,`name`,`bus`)
) ENGINE=InnoDB AUTO_INCREMENT=51815 DEFAULT CHARSET=utf8 COMMENT='event rule meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_rule`
--

LOCK TABLES `event_rule` WRITE;
/*!40000 ALTER TABLE `event_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_source`
--

DROP TABLE IF EXISTS `event_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_source` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) DEFAULT 'SYSTEM' COMMENT 'source account id',
  `bus` varchar(255) NOT NULL COMMENT 'bus name',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'source name',
  `status` int NOT NULL DEFAULT '0' COMMENT '0:disable, 1:enable',
  `type` int NOT NULL DEFAULT '1' COMMENT 'event source type',
  `class_name` varchar(255) DEFAULT NULL COMMENT 'event source class name',
  `config` text COMMENT 'event source runner config',
  `description` varchar(1024) DEFAULT NULL COMMENT 'event source description',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`account_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='event source meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_source`
--

LOCK TABLES `event_source` WRITE;
/*!40000 ALTER TABLE `event_source` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_source` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_source_class`
--

DROP TABLE IF EXISTS `event_source_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_source_class` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'source class name',
  `api_params` text NOT NULL COMMENT 'event source api params',
  `required_params` text NOT NULL COMMENT 'event source required params',
  `transform` text NOT NULL COMMENT 'transform the event source data',
  `visual_config` text COMMENT 'event source fore-end visual config',
  `description` varchar(255) DEFAULT NULL COMMENT 'a description about the source class',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='event source class meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_source_class`
--

LOCK TABLES `event_source_class` WRITE;
/*!40000 ALTER TABLE `event_source_class` DISABLE KEYS */;
INSERT INTO `event_source_class` VALUES (1,'acs.mns','{\n    \"RegionId\":{\n        \"type\":\"String\",\n        \"desc\":\"the region of aliyun mns.\",\n        \"required\":true,\n        \"defaultValue\":\"\"\n    },\n    \"QueueName\":{\n        \"type\":\"String\",\n        \"desc\":\"the queue name of aliyun mns.\",\n        \"required\":true,\n        \"defaultValue\":\"\"\n    },\n    \"IsBase64Decode\":{\n        \"type\":\"boolean\",\n        \"desc\":\"base64 decode or not\"\n    },\n    \"AliyunAccountId\":{\n        \"type\":\"String\",\n        \"desc\":\"the account id of aliyun mns.\",\n        \"required\":true\n    },\n    \"AccessKeyId\":{\n        \"type\":\"String\",\n        \"desc\":\"the access key id of aliyun mns.\",\n        \"required\":true\n    },\n    \"AccessKeySecret\":{\n        \"type\":\"String\",\n        \"desc\":\"the access key idsecret of aliyun mns.\",\n        \"required\":true\n    }\n}','{\n    \"accountEndpoint\":\"http://${AliyunAccountId}.mns.${RegionId}.aliyuncs.com\",\n    \"accountId\":\"${AliyunAccountId}\",\n    \"queueName\":\"${QueueName}\",\n    \"isBase64Decode\":\"${IsBase64Decode}\",\n    \"accessKeyId\":\"${AccessKeyId}\",\n    \"accessKeySecret\":\"${AccessKeySecret}\",\n    \"class\":\"org.apache.rocketmq.connect.mns.source.MNSSourceConnector\"\n}','{\n    \"data\":\"{\\\"value\\\":\\\"$.data\\\",\\\"form\\\":\\\"JSONPATH\\\"}\",\n    \"subject\":\"{\\\"value\\\":\\\"acs:mns:${RegionId}:${AliyunAccountId}:queues/${QueueName}\\\",\\\"form\\\":\\\"CONSTANT\\\"}\",\n    \"type\":\"{\\\"value\\\":\\\"mns.sendMsg\\\",\\\"form\\\":\\\"CONSTANT\\\"}\"\n}',NULL,'aliyun mns source','2023-04-11 04:39:06','2023-04-11 04:39:06');
/*!40000 ALTER TABLE `event_source_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_source_runner`
--

DROP TABLE IF EXISTS `event_source_runner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_source_runner` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) DEFAULT 'SYSTEM' COMMENT 'source account id',
  `bus` varchar(255) NOT NULL COMMENT 'bus name',
  `source` varchar(255) NOT NULL DEFAULT '' COMMENT 'source name',
  `run_options` varchar(1024) DEFAULT NULL COMMENT 'event source runner options',
  `run_context` varchar(1024) DEFAULT NULL COMMENT 'event source running context',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`account_id`,`bus`,`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='event source runner meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_source_runner`
--

LOCK TABLES `event_source_runner` WRITE;
/*!40000 ALTER TABLE `event_source_runner` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_source_runner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_target`
--

DROP TABLE IF EXISTS `event_target`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_target` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) DEFAULT 'SYSTEM' COMMENT 'target account id',
  `bus` varchar(255) NOT NULL COMMENT 'bus name',
  `rule` varchar(255) NOT NULL DEFAULT '' COMMENT 'rule name',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'target name',
  `class_name` varchar(255) NOT NULL COMMENT 'event target class name',
  `config` text NOT NULL COMMENT 'event target runner config',
  `run_options` varchar(1024) DEFAULT NULL COMMENT 'event target runner options',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`account_id`,`bus`,`rule`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='event target meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_target`
--

LOCK TABLES `event_target` WRITE;
/*!40000 ALTER TABLE `event_target` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_target` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_target_class`
--

DROP TABLE IF EXISTS `event_target_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_target_class` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'target class name',
  `api_params` text NOT NULL COMMENT 'event target api params',
  `target_transform` text,
  `required_params` text NOT NULL COMMENT 'event target required params',
  `visual_config` text COMMENT 'event target fore-end visual config',
  `description` varchar(255) DEFAULT NULL COMMENT 'a description about the target class',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='event target class meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_target_class`
--

LOCK TABLES `event_target_class` WRITE;
/*!40000 ALTER TABLE `event_target_class` DISABLE KEYS */;
INSERT INTO `event_target_class` VALUES (1,'acs.dingtalk','{\n    \"WebHook\":{\n        \"type\":\"String\",\n        \"desc\":\"the endpoint of webhook.\",\n        \"required\":true\n    },\n    \"SecretKey\":{\n        \"type\":\"String\",\n        \"desc\":\"the secret key.\",\n        \"required\":true\n    },\n    \"Body\":{\n        \"type\":\"boolean\",\n        \"desc\":\"the content of request\"\n    }\n}','{     \"data\":\"${Body}\" }','{\n    \"webHook\":\"${WebHook}\",\n    \"secretKey\":\"${SecretKey}\",\n    \"class\":\"org.apache.rocketmq.connect.dingtalk.sink.DingTalkSinkConnector\"\n}',NULL,'aliyun dingtalk connector config','2023-04-11 04:39:36','2023-04-11 04:39:36'),(2,'acs.eventbridge','{\n    \"RegionId\":{\n        \"type\":\"String\",\n        \"desc\":\"the region of aliyun eventbridge.\",\n        \"required\":true\n    },\n    \"AliyunAccountId\":{\n        \"type\":\"String\",\n        \"desc\":\"the account id of aliyun eventbridge.\",\n        \"required\":true\n    },\n    \"AliyunEventBus\":{\n        \"type\":\"String\",\n        \"desc\":\"the bus of aliyun eventbridge.\",\n        \"required\":true\n    },\n    \"AccessKeyId\":{\n        \"type\":\"String\",\n        \"desc\":\"the accessKeyId of aliyun eventbridge.\",\n        \"required\":true\n    },\n    \"AccessKeySecret\":{\n        \"type\":\"String\",\n        \"desc\":\"the accessKeySecret of aliyun eventbridge.\",\n        \"required\":true\n    }\n}','{\n    \"data\":\"{\\\"form\\\":\\\"JSONPATH\\\",\\\"value\\\":\\\"$.data\\\"}\",\n    \"id\":\"{\\\"form\\\":\\\"JSONPATH\\\",\\\"value\\\":\\\"$.id\\\"}\",\n    \"type\":\"{\\\"form\\\":\\\"JSONPATH\\\",\\\"value\\\":\\\"$.type\\\"}\",\n    \"specversion\":\"{\\\"form\\\":\\\"JSONPATH\\\",\\\"value\\\":\\\"$. specversion\\\"}\",\n    \"subject\":\"{\\\"form\\\":\\\"JSONPATH\\\",\\\"value\\\":\\\"$.subject\\\"}\",\n    \"source\":\"{\\\"form\\\":\\\"JSONPATH\\\",\\\"value\\\":\\\"$.source\\\"}\"\n}','{\n    \"aliyuneventbusname\":\"${AliyunEventBus}\",\n    \"accessKeyId\":\"${AccessKeyId}\",\n    \"accessKeySecret\":\"${AccessKeySecret}\",\n    \"accountEndpoint\":\"${AliyunAccountId}.eventbridge.${RegionId}.aliyuncs.com\",\n    \"class\":\"org.apache.rocketmq.connect.eventbridge.sink.EventBridgeSinkConnector\"\n}',NULL,'aliyun eventbridge connector config','2023-04-11 04:39:54','2023-04-11 04:39:54');
/*!40000 ALTER TABLE `event_target_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_target_runner`
--

DROP TABLE IF EXISTS `event_target_runner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_target_runner` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) DEFAULT 'SYSTEM' COMMENT 'target account id',
  `bus` varchar(255) NOT NULL COMMENT 'bus name',
  `rule` varchar(255) NOT NULL DEFAULT '' COMMENT 'rule name',
  `target` varchar(255) NOT NULL DEFAULT '' COMMENT 'target name',
  `run_context` varchar(1024) DEFAULT NULL COMMENT 'event target running context',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`account_id`,`bus`,`rule`,`target`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='event target runner meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_target_runner`
--

LOCK TABLES `event_target_runner` WRITE;
/*!40000 ALTER TABLE `event_target_runner` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_target_runner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_topic`
--

DROP TABLE IF EXISTS `event_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_topic` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) DEFAULT 'SYSTEM' COMMENT 'source account id',
  `bus` varchar(255) NOT NULL COMMENT 'bus name',
  `name` varchar(255) NOT NULL COMMENT 'topic name',
  `msg_ttl` int NOT NULL COMMENT 'msg ttl',
  `cluster` varchar(255) NOT NULL COMMENT 'the cluster of topic',
  `status` tinyint NOT NULL COMMENT '0:disable, 1:enable',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_topic`
--

LOCK TABLES `event_topic` WRITE;
/*!40000 ALTER TABLE `event_topic` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_type`
--

DROP TABLE IF EXISTS `event_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_type` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) NOT NULL COMMENT 'bus account id',
  `bus` varchar(255) NOT NULL COMMENT 'bus name',
  `source` varchar(255) NOT NULL DEFAULT '' COMMENT 'event source name',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'event type name',
  `description` varchar(255) DEFAULT NULL COMMENT 'a description about the event type',
  `gmt_create` datetime DEFAULT NULL COMMENT 'create time',
  `gmt_modify` datetime DEFAULT NULL COMMENT 'modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_uniq_key` (`source`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='event type meta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_type`
--

LOCK TABLES `event_type` WRITE;
/*!40000 ALTER TABLE `event_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_type` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-01-03 18:25:47
-- MySQL dump 10.13  Distrib 8.0.20, for macos10.15 (x86_64)
--
-- Host: 127.0.0.1    Database: rocketmq_sink
-- ------------------------------------------------------
-- Server version	8.0.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_name` varchar(45) NOT NULL COMMENT '用户名称',
  `sex` int NOT NULL COMMENT '用户性别',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `is_deleted` int NOT NULL COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-01-03 18:25:47
-- MySQL dump 10.13  Distrib 8.0.20, for macos10.15 (x86_64)
--
-- Host: 127.0.0.1    Database: rocketmq_source
-- ------------------------------------------------------
-- Server version	8.0.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_name` varchar(45) NOT NULL COMMENT '用户名称',
  `sex` int NOT NULL COMMENT '用户性别',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `is_deleted` int NOT NULL COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` VALUES (1,'测试1',1,'2021-06-25 06:13:04','2021-06-25 06:13:04',1),(2,'测试2',1,'2021-06-25 06:13:04','2021-06-25 06:13:04',1);
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-01-03 18:25:47
