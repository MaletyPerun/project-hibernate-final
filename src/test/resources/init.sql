CREATE DATABASE  IF NOT EXISTS `test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `test`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `name` varchar(35) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                        `country_id` int(11) NOT NULL,
                        `district` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                        `population` int(11) NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        KEY `city_ibfk_1_idx` (`country_id`),
                        CONSTRAINT `city_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4080 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
                           `id` int(11) NOT NULL DEFAULT '0',
                           `code` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                           `code_2` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                           `name` varchar(52) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                           `continent` int(11) NOT NULL DEFAULT '0' COMMENT '0-ASIA, 1-EUROPE, 2-NORTH_AMERICA, 3-AFRICA, 4-OCEANIA, 5-ANTARCTICA, 6-SOUTH_AMERICA',
                           `region` varchar(26) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                           `surface_area` decimal(10,2) NOT NULL DEFAULT '0.00',
                           `indep_year` smallint(6) DEFAULT NULL,
                           `population` int(11) NOT NULL DEFAULT '0',
                           `life_expectancy` decimal(3,1) DEFAULT NULL,
                           `gnp` decimal(10,2) DEFAULT NULL,
                           `gnpo_id` decimal(10,2) DEFAULT NULL,
                           `local_name` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                           `government_form` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                           `head_of_state` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `capital` int(11) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `country_ibfk_1_idx` (`capital`),
                           CONSTRAINT `country_ibfk_1` FOREIGN KEY (`capital`) REFERENCES `city` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `country_language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country_language` (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `country_id` int(11) NOT NULL,
                                    `language` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                                    `is_official` tinyint(1) NOT NULL DEFAULT '0',
                                    `percentage` decimal(4,1) NOT NULL DEFAULT '0.0',
                                    PRIMARY KEY (`id`),
                                    KEY `country_language_ibfk_1_idx` (`country_id`),
                                    CONSTRAINT `country_language_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=985 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

