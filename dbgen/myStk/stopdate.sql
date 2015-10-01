-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- 主機: 127.0.0.1
-- 產生時間： 2015-06-27: 22:37:24
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
-- 資料表結構 `stopdate`
--

CREATE TABLE IF NOT EXISTS `stopdate` (
  `stockid` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `startdate` date DEFAULT NULL,
  `enddate` date DEFAULT NULL,
  `rmk` varchar(20) COLLATE utf8_bin DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `stopdate`
--
ALTER TABLE `stopdate`
 ADD PRIMARY KEY (`stockid`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
