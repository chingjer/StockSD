-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- 主機: 127.0.0.1
-- 產生時間： 2015-04-15: 18:59:05
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
-- 資料表結構 `divergence`
--

CREATE TABLE IF NOT EXISTS `divergence` (
`ID` bigint(20) unsigned NOT NULL,
  `typ` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `idx` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `stockid` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `dte` date DEFAULT NULL,
  `price` double DEFAULT NULL,
  `p_stop` double DEFAULT '0',
  `vol` double DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `divergence`
--
ALTER TABLE `divergence`
 ADD UNIQUE KEY `ID` (`ID`);

--
-- 在匯出的資料表使用 AUTO_INCREMENT
--

--
-- 使用資料表 AUTO_INCREMENT `divergence`
--
ALTER TABLE `divergence`
MODIFY `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
