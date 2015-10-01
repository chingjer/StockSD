-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- 主機: 127.0.0.1
-- 產生時間： 2015-02-14: 20:07:14
-- 伺服器版本: 5.6.21
-- PHP 版本： 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 資料庫： `mystk`
--

-- --------------------------------------------------------

--
-- 資料表結構 `squeeze`
--

DROP TABLE IF EXISTS `squeeze`;
CREATE TABLE IF NOT EXISTS `squeeze` (
  `stockid` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `dte` date NOT NULL DEFAULT '0000-00-00',
  `minbw` double DEFAULT NULL,
  `minbw_dte` date DEFAULT NULL,
  `sqdays` double DEFAULT NULL,
  `bandwidth` double DEFAULT NULL,
  `vol` double DEFAULT NULL,
  `va5` double DEFAULT NULL,
  `va10` double DEFAULT NULL,
  `updown` double DEFAULT NULL,
  `price` double DEFAULT NULL,
  `ub` double DEFAULT NULL,
  `percentb` double DEFAULT NULL,
  `sc_osc` int(11) DEFAULT NULL,
  `mfi` double DEFAULT NULL,
  `ptbrsi12` double DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `squeeze`
--
ALTER TABLE `squeeze`
 ADD PRIMARY KEY (`stockid`,`dte`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
