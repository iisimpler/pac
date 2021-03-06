/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.9-log : Database - win007
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`win007` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `win007`;

/*Table structure for table `company` */

DROP TABLE IF EXISTS `company`;

CREATE TABLE `company` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `type` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `company` */

LOCK TABLES `company` WRITE;

UNLOCK TABLES;

/*Table structure for table `country` */

DROP TABLE IF EXISTS `country`;

CREATE TABLE `country` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `nameTra` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `country` */

LOCK TABLES `country` WRITE;

UNLOCK TABLES;

/*Table structure for table `league` */

DROP TABLE IF EXISTS `league`;

CREATE TABLE `league` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `countryId` varchar(10) DEFAULT NULL,
  `hasSub` varchar(10) DEFAULT NULL COMMENT '是否有子联赛',
  `leagueType` varchar(10) DEFAULT NULL COMMENT '联赛 or 杯赛',
  `round` int(20) DEFAULT NULL,
  `season` varchar(255) DEFAULT NULL,
  `description` text,
  `nameTra` varchar(50) DEFAULT NULL,
  `nameEng` varchar(50) DEFAULT NULL,
  `nameAll` varchar(50) DEFAULT NULL,
  `nameAllTra` varchar(50) DEFAULT NULL,
  `nameAllEng` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `league` */

LOCK TABLES `league` WRITE;

UNLOCK TABLES;

/*Table structure for table `match` */

DROP TABLE IF EXISTS `match`;

CREATE TABLE `match` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `hostId` int(20) DEFAULT NULL,
  `guestId` int(20) DEFAULT NULL,
  `rankHost` varchar(20) DEFAULT NULL,
  `rankGuest` varchar(20) DEFAULT NULL,
  `scoreHalf` varchar(20) DEFAULT NULL,
  `scoreAll` varchar(20) DEFAULT NULL,
  `leagueId` int(20) DEFAULT NULL,
  `redHost` int(20) DEFAULT NULL,
  `redGuest` int(20) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `round` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `match` */

LOCK TABLES `match` WRITE;

UNLOCK TABLES;

/*Table structure for table `odds` */

DROP TABLE IF EXISTS `odds`;

CREATE TABLE `odds` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `matchId` int(20) DEFAULT NULL,
  `companyId` int(20) DEFAULT NULL,
  `hostOdds` float(6,4) DEFAULT NULL,
  `drawOdds` float(6,4) DEFAULT NULL,
  `guestOdds` float(6,4) DEFAULT NULL,
  `hostRate` float(5,4) DEFAULT NULL,
  `drawRate` float(5,4) DEFAULT NULL,
  `guestRate` float(5,4) DEFAULT NULL,
  `returnRate` float(5,4) DEFAULT NULL,
  `hostKelly` float(3,2) DEFAULT NULL,
  `drawKelly` float(3,2) DEFAULT NULL,
  `guestKelly` float(3,2) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `odds` */

LOCK TABLES `odds` WRITE;

UNLOCK TABLES;

/*Table structure for table `oddsmap` */

DROP TABLE IF EXISTS `oddsmap`;

CREATE TABLE `oddsmap` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `matchId` int(20) DEFAULT NULL,
  `companyId` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `oddsmap` */

LOCK TABLES `oddsmap` WRITE;

UNLOCK TABLES;

/*Table structure for table `pageurl` */

DROP TABLE IF EXISTS `pageurl`;

CREATE TABLE `pageurl` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `pageurl` */

LOCK TABLES `pageurl` WRITE;

UNLOCK TABLES;

/*Table structure for table `team` */

DROP TABLE IF EXISTS `team`;

CREATE TABLE `team` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `seasonLeagueId` varchar(255) DEFAULT NULL,
  `nameTra` varchar(50) DEFAULT NULL,
  `nameEng` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `team` */

LOCK TABLES `team` WRITE;

UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
