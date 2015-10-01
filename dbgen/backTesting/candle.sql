-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- 主機: 127.0.0.1
-- 產生時間： 2015-05-10: 15:54:01
-- 伺服器版本: 5.6.21
-- PHP 版本： 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 資料庫： `backtesting`
--

-- --------------------------------------------------------

--
-- 資料表結構 `candle`
--

DROP TABLE IF EXISTS `candle`;
CREATE TABLE IF NOT EXISTS `candle` (
  `stockid` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `dte` date NOT NULL DEFAULT '0000-00-00',
  `price` double DEFAULT NULL,
  `datecode` varchar(1) COLLATE utf8_bin NOT NULL DEFAULT '',
  `ktype` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `p_open` double DEFAULT '0',
  `p_high` double DEFAULT '0',
  `p_low` double DEFAULT '0',
  `percentb` double DEFAULT '0',
  `bandwidth` double DEFAULT '0',
  `profit` double DEFAULT '0',
  `isclose` tinyint(1) DEFAULT NULL,
  `p_buy` double DEFAULT '0',
  `buydate` date DEFAULT NULL,
  `p_stop` double DEFAULT '0',
  `stopdate` date DEFAULT NULL,
  `days` int(11) DEFAULT '0',
  `times` int(11) DEFAULT '0',
  `indays` int(11) DEFAULT '0',
  `remark` varchar(50) COLLATE utf8_bin DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
