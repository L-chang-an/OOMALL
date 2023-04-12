DROP TABLE IF EXISTS `wechatpay_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wechatpay_transaction`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `out_trade_no`     varchar(32)  DEFAULT NULL,
    `trade_state`      varchar(32)  DEFAULT NULL,
    `total`            int          DEFAULT NULL,
    `payer_total`      int          DEFAULT NULL,
    `success_time`     datetime     DEFAULT NULL,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信支付订单';



DROP TABLE IF EXISTS `wechatpay_refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wechatpay_refund`
(
    `id`                    bigint NOT NULL AUTO_INCREMENT,
    `out_refund_no`         varchar(64)  DEFAULT NULL,
    `out_trade_no`          varchar(32)  DEFAULT NULL,
    `status`                varchar(32)  DEFAULT NULL,
    `refund`                int          DEFAULT NULL,
    `payer_total`           int          DEFAULT NULL,
    `success_time`           datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信支付退款单';

DROP TABLE IF EXISTS `wechatpay_div_pay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wechatpay_div_pay`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `out_trade_no`     varchar(32)  DEFAULT NULL,
    `out_order_no`     varchar(32)  DEFAULT NULL,
    `state`           varchar(32)  DEFAULT NULL,
    `payer_total`      int          DEFAULT NULL,
    `success_time`     datetime     DEFAULT NULL,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信分账订单';



DROP TABLE IF EXISTS `wechatpay_div_refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wechatpay_div_refund`
(
    `id`                    bigint NOT NULL AUTO_INCREMENT,
    `out_return_no`         varchar(64)  DEFAULT NULL,
    `out_order_no`          varchar(32)  DEFAULT NULL,
    `amount`           int          DEFAULT NULL,
    `success_time`           datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信分账退回单';

DROP TABLE IF EXISTS `wechatpay_receiver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wechatpay_receiver`
(
    `id`                    bigint NOT NULL AUTO_INCREMENT,
    `appid`                 varchar(32)  DEFAULT NULL,
    `type`                  varchar(32)  DEFAULT NULL,
    `account`               varchar(64)  DEFAULT NULL,
    `name`                  varchar(1024)  DEFAULT NULL,
    `relation_type`         varchar(32)  DEFAULT NULL,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信分账接收方';

LOCK TABLES `wechatpay_refund` WRITE;
INSERT INTO `wechatpay_refund` VALUES (1, '10', '10', 'SUCCESS', 100, 100, '2021-12-02 15:08:42');
UNLOCK TABLES;

LOCK TABLES `wechatpay_transaction` WRITE;
INSERT INTO `wechatpay_transaction` VALUES (1, '1', 'SUCCESS', 100, 100, '2021-12-02 13:02:47');
INSERT INTO `wechatpay_transaction` VALUES (2, '2', 'SUCCESS', 100, 100, '2021-12-02 14:50:33');
INSERT INTO `wechatpay_transaction` VALUES (3, '3', 'SUCCESS', 100, 100, '2021-12-02 14:50:45');
INSERT INTO `wechatpay_transaction` VALUES (4, '4', 'SUCCESS', 100, 100, '2021-12-02 14:50:58');
INSERT INTO `wechatpay_transaction` VALUES (5, '5', 'SUCCESS', 100, 100, '2021-12-02 14:51:09');
INSERT INTO `wechatpay_transaction` VALUES (6, '6', 'SUCCESS', 100, 100, '2021-12-02 14:51:20');
INSERT INTO `wechatpay_transaction` VALUES (7, '7', 'SUCCESS', 100, 100, '2021-12-02 14:51:33');
INSERT INTO `wechatpay_transaction` VALUES (8, '8', 'SUCCESS', 100, 100, '2021-12-02 14:51:45');
INSERT INTO `wechatpay_transaction` VALUES (9, '9', 'CLOSED', 100, NULL, '2021-12-02 15:03:35');
INSERT INTO `wechatpay_transaction` VALUES (10, '10', 'REFUND', 100, 100, '2021-12-02 15:07:19');
INSERT INTO `wechatpay_transaction` VALUES (11, 'P20150806125346', 'REFUND', 100, 100, '2021-12-02 14:50:58');
UNLOCK TABLES;


LOCK TABLES `wechatpay_div_pay` WRITE;
INSERT INTO `wechatpay_div_pay` VALUES ('1','1','P20150806125346','SUCCESS',30,'2015-05-20T13:29:35+08:00');
UNLOCK TABLES;

LOCK TABLES `wechatpay_div_refund` WRITE;
INSERT INTO `wechatpay_div_refund` VALUES ('1','R20190516001','P20150806125346',100,'2015-05-20 13:29:35');
UNLOCK TABLES;

LOCK TABLES `wechatpay_receiver` WRITE;
INSERT INTO `wechatpay_receiver` VALUES ('1','wx8888888888888888','MERCHANT_ID',86693852,'hu89ohu89ohu89o','STORE');
UNLOCK TABLES;