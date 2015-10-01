-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- 主機: 127.0.0.1
-- 產生時間： 2014-12-07: 15:00:18
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
-- 資料表結構 `sysparm`
--

DROP TABLE IF EXISTS `sysparm`;
CREATE TABLE IF NOT EXISTS `sysparm` (
  `ID` int(11) NOT NULL,
  `cost_op` double DEFAULT '0',
  `cost_tax` double DEFAULT '0',
  `private_loc` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `hist_begdate` date DEFAULT NULL,
  `public_loc` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `tot_money` double DEFAULT '0',
  `currdate` date DEFAULT NULL,
  `tourdays` int(4) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- 資料表的匯出資料 `sysparm`
--

INSERT INTO `sysparm` (`ID`, `cost_op`, `cost_tax`, `private_loc`, `hist_begdate`, `public_loc`, `tot_money`, `currdate`, `tourdays`) VALUES
(0, 1.425, 3, 'G:\\MY-NB2\\庫存與歷史', '2010-09-01', 'c:\\appserv\\web\\query\\', 0, '2010-10-18', 20);

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `sysparm`
--
ALTER TABLE `sysparm`
 ADD PRIMARY KEY (`ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
