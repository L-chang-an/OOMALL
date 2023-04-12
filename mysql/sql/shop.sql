-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: mysql    Database: shop
-- ------------------------------------------------------
-- Server version	8.0.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `shop_region_template`
--

DROP TABLE IF EXISTS `shop_region_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_region_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `template_id` bigint DEFAULT NULL,
  `region_id` bigint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  `object_id` varchar(128) DEFAULT NULL,
  `unit` int NOT NULL DEFAULT '1',
  `template_dao` varchar(128) DEFAULT NULL,
  `upper_limit` int NOT NULL DEFAULT '10000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='地区运费';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_region_template`
--

LOCK TABLES `shop_region_template` WRITE;
/*!40000 ALTER TABLE `shop_region_template` DISABLE KEYS */;
INSERT INTO `shop_region_template` VALUES (105,2,248059,1,'admin',NULL,NULL,'2022-12-09 18:35:36',NULL,'63930f78d4f468435d9420fb',2,'pieceTemplateDao',10),(106,2,0,1,'admin',NULL,NULL,'2022-12-09 18:35:36',NULL,'63930f78d4f468435d9420fc',2,'pieceTemplateDao',10),(107,1,247478,1,'admin',NULL,NULL,'2022-12-09 18:35:36',NULL,'63930f78d4f468435d9420fd',100,'weightTemplateDao',500000),(108,1,248059,1,'admin',NULL,NULL,'2022-12-09 18:35:36',NULL,'63930f78d4f468435d9420fe',100,'weightTemplateDao',500000),(109,1,191020,1,'admin',NULL,NULL,'2022-12-09 18:35:36',NULL,'63930f78d4f468435d9420ff',100,'weightTemplateDao',500000),(110,1,251197,1,'admin',NULL,NULL,'2022-12-09 18:35:36',NULL,'63930f78d4f468435d942100',100,'weightTemplateDao',500000);
/*!40000 ALTER TABLE `shop_region_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_service_product`
--

DROP TABLE IF EXISTS `shop_service_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_service_product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint DEFAULT NULL,
  `maintainer_id` bigint NOT NULL,
  `begin_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `invalid` tinyint NOT NULL DEFAULT '0',
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  `region_id` bigint DEFAULT NULL,
  `priority` int NOT NULL DEFAULT '1000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_service_product`
--

LOCK TABLES `shop_service_product` WRITE;
/*!40000 ALTER TABLE `shop_service_product` DISABLE KEYS */;
INSERT INTO `shop_service_product` VALUES (1,5107,45,'2022-05-23 20:44:23','2027-11-23 20:44:33',0,1,'admin',NULL,NULL,'2022-11-23 12:45:00',NULL,152,1000),(2,5108,45,'2022-05-23 20:45:17','2026-11-23 20:45:25',0,1,'admin',NULL,NULL,'2022-11-23 20:45:40',NULL,1,1000),(3,5109,45,'2022-05-23 21:03:50','2022-08-23 21:03:56',0,1,'admin',NULL,NULL,'2022-11-23 21:04:10',NULL,2,1000);
/*!40000 ALTER TABLE `shop_service_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_shop`
--

DROP TABLE IF EXISTS `shop_shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_shop` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `deposit` bigint DEFAULT NULL,
  `deposit_threshold` bigint DEFAULT NULL,
  `status` tinyint DEFAULT '0',
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  `region_id` bigint DEFAULT NULL,
  `address` varchar(512) DEFAULT NULL,
  `consignee` varchar(64) DEFAULT NULL,
  `mobile` varchar(64) DEFAULT NULL,
  `type` tinyint NOT NULL DEFAULT '0',
  `free_threshold` int NOT NULL DEFAULT '0' COMMENT '默认免邮门槛',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商铺';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_shop`
--

LOCK TABLES `shop_shop` WRITE;
/*!40000 ALTER TABLE `shop_shop` DISABLE KEYS */;
INSERT INTO `shop_shop` VALUES (1,'OOMALL自营商铺',5000000,1000000,2,1,'admin',NULL,NULL,'2021-11-11 13:24:49',NULL,10,'黄图岗南街112','张三','111111',0,10000),(2,'甜蜜之旅',5000000,1000000,2,1,'admin',NULL,NULL,'2021-11-11 13:24:49',NULL,452449,'麓山村10号','李四','22222',0,2000),(3,'向往时刻',5000000,1000000,2,1,'admin',NULL,NULL,'2021-11-11 13:24:49',NULL,248038,'何厝2号','王五','2222',0,0),(4,'努力向前',5000000,1000000,2,1,'admin',NULL,NULL,'2021-11-11 13:24:49',NULL,248057,'大帽山农场9号','赵六','2222',0,0),(5,'坚持就是胜利',5000000,1000000,1,1,'admin',NULL,NULL,'2021-11-11 13:24:49',NULL,253982,'古塘村居委会','周器','33333',0,0),(6,'一口气',5000000,1000000,1,1,'admin',NULL,NULL,'2021-11-11 13:24:49',NULL,161817,'北京路100号','苏巴','33444',0,0),(7,'商铺7',5000000,1000000,1,1,'admin',NULL,NULL,'2021-11-11 13:24:49',NULL,420850,'五福巷199号','刘宏','556',0,0),(8,'商铺8',5000000,1000000,0,1,'admin',NULL,NULL,'2021-11-11 13:24:49',NULL,167934,'锁一巷7号','常遇春','66667',0,0),(9,'商铺9',5000000,1000000,0,1,'admin',NULL,NULL,'2021-11-11 13:24:49',NULL,191030,'涌金门19号','汪其','788888',0,0),(10,'商铺10',5000000,1000000,0,1,'admin',NULL,NULL,'2021-11-11 13:24:49','2022-12-14 06:57:33',367413,'郑州日报社','刘才','999999',0,0),(45,'服务商1',1000000,500000,1,1,'admin',NULL,NULL,'2022-11-29 10:57:53',NULL,367413,'郑州日报社','书孤','2213233',1,0),(71,'服务商2',1222222,50000,2,1,'admin',NULL,NULL,'2022-12-24 17:19:50',NULL,67750,'山西省太原市小店区亲贤北街368号水工大厦','克强','122333',1,0),(72,'服务商3',122333,60000,0,1,'admin',NULL,NULL,'2022-12-24 17:22:16',NULL,517935,'北海大道中26号鸿海大厦210室','汪五','222222',1,0),(73,'服务商4',1222343,70000,1,1,'admin',NULL,NULL,'2022-12-24 14:42:21',NULL,247933,'厦门大学翔安校区','周轴','1223434',1,0);
/*!40000 ALTER TABLE `shop_shop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_template`
--

DROP TABLE IF EXISTS `shop_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL,
  `name` varchar(128) DEFAULT NULL,
  `default_model` tinyint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='运费模板';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_template`
--

LOCK TABLES `shop_template` WRITE;
/*!40000 ALTER TABLE `shop_template` DISABLE KEYS */;
INSERT INTO `shop_template` VALUES (1,1,'计重模板',1,1,'admin11',NULL,NULL,'2022-11-15 17:59:20',NULL),(2,1,'计件模板',0,1,'admin11',NULL,NULL,'2022-11-15 17:58:24',NULL),(14,3,'删除模板',0,1,'admin',NULL,NULL,'2022-12-25 14:59:46',NULL),(15,3,'计重模板',1,1,'admin12',NULL,NULL,'2022-12-28 15:10:54',NULL);
/*!40000 ALTER TABLE `shop_template` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-29 22:29:12
