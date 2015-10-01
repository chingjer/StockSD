-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- 主機: 127.0.0.1
-- 產生時間： 2015-05-07: 09:37:51
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
-- 資料表結構 `bt_para`
--

CREATE TABLE IF NOT EXISTS `bt_para` (
`ID` bigint(20) unsigned NOT NULL,
  `sys` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `subsys` varchar(30) COLLATE utf8_bin NOT NULL DEFAULT '',
  `num` int(11) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `add_maxtimes` varchar(10) COLLATE utf8_bin NOT NULL,
  `add_cri` varchar(10) COLLATE utf8_bin NOT NULL,
  `sub_cri` varchar(10) COLLATE utf8_bin NOT NULL,
  `max_lose` varchar(10) COLLATE utf8_bin NOT NULL,
  `num_check` varchar(10) COLLATE utf8_bin NOT NULL,
  `firststop_cri` varchar(10) COLLATE utf8_bin NOT NULL,
  `maxdrop_cri` varchar(10) COLLATE utf8_bin NOT NULL,
  `in_days` varchar(10) COLLATE utf8_bin NOT NULL,
  `buy_mode` varchar(10) COLLATE utf8_bin NOT NULL,
  `holddays_cri` varchar(10) COLLATE utf8_bin NOT NULL,
  `min_profit` varchar(10) COLLATE utf8_bin NOT NULL,
  `chg_cri` varchar(10) COLLATE utf8_bin NOT NULL,
  `stop_ma` varchar(10) COLLATE utf8_bin NOT NULL,
  `filt_ma` varchar(10) COLLATE utf8_bin NOT NULL,
  `p14` varchar(10) COLLATE utf8_bin NOT NULL,
  `p15` varchar(10) COLLATE utf8_bin NOT NULL,
  `p16` varchar(10) COLLATE utf8_bin NOT NULL,
  `p17` varchar(10) COLLATE utf8_bin NOT NULL,
  `p18` varchar(10) COLLATE utf8_bin NOT NULL,
  `p19` varchar(10) COLLATE utf8_bin NOT NULL,
  `p20` varchar(10) COLLATE utf8_bin NOT NULL,
  `p21` varchar(10) COLLATE utf8_bin NOT NULL,
  `p22` varchar(10) COLLATE utf8_bin NOT NULL,
  `p23` varchar(10) COLLATE utf8_bin NOT NULL,
  `p24` varchar(10) COLLATE utf8_bin NOT NULL,
  `p25` varchar(10) COLLATE utf8_bin NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- 資料表的匯出資料 `bt_para`
--

INSERT INTO `bt_para` (`ID`, `sys`, `subsys`, `num`, `enabled`, `add_maxtimes`, `add_cri`, `sub_cri`, `max_lose`, `num_check`, `firststop_cri`, `maxdrop_cri`, `in_days`, `buy_mode`, `holddays_cri`, `min_profit`, `chg_cri`, `stop_ma`, `filt_ma`, `p14`, `p15`, `p16`, `p17`, `p18`, `p19`, `p20`, `p21`, `p22`, `p23`, `p24`, `p25`) VALUES
(1, 'cs', '+吊人', 1, 1, '1', '0.05', '0.03', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', ''),
(3, 'lvstg', '1', 1, 1, '1', '0.05', '0.03', '0', '3', '1', '-1', '5', '尾盤', '5', '14', '14', 'ma5', '0.96', '500', '2', '75', '4', '80', '1', '1.03', '', '', '', '', '');

-- --------------------------------------------------------

--
-- 資料表結構 `bt_paraname`
--

CREATE TABLE IF NOT EXISTS `bt_paraname` (
  `sys` varchar(20) NOT NULL,
  `subsys` varchar(30) NOT NULL,
  `p14` varchar(40) NOT NULL DEFAULT '''''',
  `p15` varchar(40) NOT NULL DEFAULT '''''',
  `p16` varchar(40) NOT NULL DEFAULT '''''',
  `p17` varchar(40) NOT NULL DEFAULT '''''',
  `p18` varchar(40) NOT NULL DEFAULT '''''',
  `p19` varchar(40) NOT NULL DEFAULT '''''',
  `p20` varchar(40) NOT NULL DEFAULT '''''',
  `p21` varchar(40) NOT NULL DEFAULT '''''',
  `p22` varchar(40) NOT NULL DEFAULT '''''',
  `p23` varchar(40) NOT NULL DEFAULT '''''',
  `p24` varchar(40) NOT NULL DEFAULT '''''',
  `p25` varchar(40) NOT NULL DEFAULT ''''''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 資料表的匯出資料 `bt_paraname`
--

INSERT INTO `bt_paraname` (`sys`, `subsys`, `p14`, `p15`, `p16`, `p17`, `p18`, `p19`, `p20`, `p21`, `p22`, `p23`, `p24`, `p25`) VALUES
('candle', '-吊人', 'BUY_CRI=<收盤*n ', 'KDD_CRI=KDK>n', 'MFI_CRI=MFI>n', 'RSI6_CRI=RSI>n', 'VOL_CRI=VOL>n', 'RECS_SKIP=RECS>n', 'PTB_CRI=%b>n', 'KD_RSI=OR/AND/KD/RSI', '', '', '', ''),
('lvstg', '1', 'MIN_VOL=', 'MIN_VOLVA5=', 'MIN_KDK=', 'MIN_SC_MACD=', 'MIN_MFI=', 'MIN_PRSI=', 'BUY_CRI=', '', '', '', '', '');

-- --------------------------------------------------------

--
-- 資料表結構 `bt_period`
--

CREATE TABLE IF NOT EXISTS `bt_period` (
  `datecode` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `begdate` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `enddate` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `enabled` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `rmk` varchar(50) COLLATE utf8_bin DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- 資料表的匯出資料 `bt_period`
--

INSERT INTO `bt_period` (`datecode`, `begdate`, `enddate`, `enabled`, `rmk`) VALUES
('A', '2008/10/30', '2009/3/5', 'Y', '暴跌後打底'),
('B', '2009/3/5', '2009/6/2', 'Y', '大空頭後初升急漲段'),
('C', '2009/6/2', '2010/1/20', 'Y', '震盪走高'),
('D', '2010/5/27', '2011/2/9', 'Y', '劇烈震盪走高'),
('E', '2011/3/18', '2011/8/5', 'Y', '盤整作頭'),
('F', '2011/12/21', '2012/4/4', 'Y', '急漲'),
('H', '2009/1/1', '2009/12/31', 'Y', '2009'),
('I', '2010/1/1', '2010/12/31', 'Y', '2010'),
('J', '2011/1/1', '2011/12/31', 'Y', '2011'),
('K', '2012/1/1', '2012/12/31', 'Y', '2012'),
('3', '2010/1/20', '2010/5/8', 'Y', '小空頭'),
('7', '2012/4/5', '2012/6/5', 'Y', ''),
('G', '2011/6/13', '2011/12/19', 'Y', '三千點大空頭'),
('X', '2008/11/20', '2008/11/20', 'Y', '反向價值投資選股'),
('Y', '2011/12/21', '2011/12/21', 'Y', 'F時段反向選股'),
('Z', '2010/5/27', '2011/2/9', 'Y', 'D時段獲利成長選股'),
('W', '2008/7/1', '2011/12/31', 'Y', 'K線全部'),
('R', '2014/1/8', '2014/1/8', 'Y', 'farmer 選股模式'),
('5', '2011/8/1', '2011/12/19', 'Y', '空頭'),
('1', '2014/1/2', '2014/6/30', 'Y', '2014上半年'),
('L', '2013-1-1', '2013-12-31', 'Y', '2013'),
('M', '2014-1-1', '2014-12-31', 'Y', '2014'),
('N', '2015-1-1', '2015-12-31', 'Y', '2015');

-- --------------------------------------------------------

--
-- 資料表結構 `bt_stat`
--

CREATE TABLE IF NOT EXISTS `bt_stat` (
  `sys` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `subsys` varchar(30) COLLATE utf8_bin NOT NULL DEFAULT '',
  `num` int(11) NOT NULL DEFAULT '0',
  `datecode` varchar(1) COLLATE utf8_bin NOT NULL DEFAULT '',
  `sel_recs` int(11) DEFAULT '0',
  `trade_recs` int(11) DEFAULT '0',
  `exp` double DEFAULT '0',
  `exp_r` double DEFAULT '0',
  `exp_quater` double DEFAULT '0',
  `avgdays` double DEFAULT '0',
  `winning` double DEFAULT '0',
  `net_profit` double DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- 資料表的匯出資料 `bt_stat`
--

INSERT INTO `bt_stat` (`sys`, `subsys`, `num`, `datecode`, `sel_recs`, `trade_recs`, `exp`, `exp_r`, `exp_quater`, `avgdays`, `winning`, `net_profit`) VALUES
('bbbro', '', 0, 'B', 74, 27, 10.82, 1.78, 67.69, 9.59, 48.15, 292.21),
('bbbro', '', 0, 'C', 306, 93, 2.85, 0.58, 32.35, 5.29, 51.61, 265.3),
('bbbro', '', 0, 'D', 158, 36, 1.9, 0.3, 21.08, 5.42, 52.78, 68.52),
('bbbro', '', 0, 'E', 48, 13, 4.51, 1.23, 37.4, 7.23, 38.46, 58.6),
('bbbro', '', 0, 'F', 76, 27, 4.18, 1.07, 46.34, 5.41, 70.37, 112.76),
('dbox', '', 0, 'E', 367, 102, 1.77, 0.71, 33.68, 3.15, 56.86, 180.17),
('dbox', '', 0, 'A', 149, 25, -3.05, -0.68, -64.5, 2.84, 20, -76.33),
('dbox', '', 0, 'B', 61, 8, -7.38, -1, -186.47, 2.38, 0, -59.05),
('dbox', '', 0, 'C', 716, 140, 0.43, 0.13, 8.42, 3.03, 52.86, 59.53),
('dbox', '', 0, 'D', 835, 117, -1.14, -0.29, -23.11, 2.95, 39.32, -132.9),
('dbox', '', 0, 'F', 184, 52, 1.24, 0.45, 25.24, 2.94, 69.23, 64.35),
('dbox', '', 0, '3', 305, 167, 1.07, 0.32, 19.58, 3.26, 56.29, 177.87),
('dbox', '', 0, '5', 29, 5, -0.15, -0.02, -2.09, 4.2, 40, -0.73),
('dbox', '', 0, 'G', 397, 159, 2.3, 0.56, 41.98, 3.29, 61.01, 365.96),
('dbox', '', 0, 'H', 860, 154, -0.73, -0.18, -14.94, 2.93, 43.51, -112.28),
('dbox', '', 0, 'I', 1100, 296, -0.29, -0.08, -5.79, 3.04, 44.93, -86.86),
('dbox', '', 0, 'J', 910, 276, 1.39, 0.37, 26.23, 3.17, 53.99, 383.01),
('dbox', '', 0, 'K', 895, 207, 0.21, 0.06, 4.12, 3, 50.72, 42.73),
('bears', '', 0, 'G', 67, 27, 4.36, 0.62, 30.29, 8.63, 51.85, 117.63),
('bears', '', 0, 'E', 31, 18, 8.91, 1.92, 41.47, 12.89, 72.22, 160.36),
('lvstg', '1', 1, 'D', 90, 37, 2.83, 0.49, 35.28, 4.81, 59.46, 104.65),
('lvstg', '1', 1, 'G', 40, 11, -0.04, 0, -0.51, 4.36, 54.55, -0.41),
('ebox', '', 1, 'I', 75, 49, -0.05, -0.01, -0.76, 4.33, 40.82, -2.67),
('ebox', '', 2, 'J', 19, 16, 0.95, 0.32, 15, 3.81, 43.75, 15.25),
('ebox', '', 2, 'D', 32, 17, -0.52, -0.12, -7.58, 4.12, 41.18, -8.84),
('ebox', '', 2, 'H', 101, 89, 11.28, 2.68, 123.18, 5.49, 51.69, 1003.89),
('ebox', '', 2, 'I', 75, 51, -0.37, -0.08, -6.41, 3.51, 43.14, -19.11),
('ebox', '', 2, 'C', 113, 101, 9.67, 2.1, 112.08, 5.18, 49.5, 976.95),
('ebox', '', 2, 'E', 13, 10, 1.5, 0.47, 23.05, 3.9, 40, 14.98),
('ebox', '', 2, 'F', 2, 1, -9.05, -1, -181, 3, 0, -9.05),
('ebox', '', 1, 'F', 2, 1, -9.05, -1, -181, 3, 0, -9.05),
('ebox', '', 1, 'J', 19, 16, 0.95, 0.32, 15, 3.81, 43.75, 15.25),
('ebox', '', 2, 'K', 23, 18, -1.08, -0.24, -16.26, 4, 38.89, -19.51),
('ebox', '', 1, 'C', 113, 84, 5.21, 1.14, 69.47, 4.5, 46.43, 437.68),
('ebox', '', 1, 'E', 13, 10, 1.5, 0.47, 23.05, 3.9, 40, 14.98),
('ebox', '', 0, 'F', 2, 1, -9.05, -1, -181, 3, 0, -9.05),
('ebox', '', 2, 'G', 8, 4, 3.29, 0.96, 49.35, 4, 50, 13.16),
('ebox', '', 1, 'H', 101, 73, 6.37, 1.54, 80.63, 4.74, 49.32, 464.96),
('ebox', '', 2, 'M', 119, 84, 0.07, 0.02, 0.96, 4.26, 39.29, 5.73),
('ebox', '', 2, 'L', 148, 107, 1.1, 0.32, 15.35, 4.3, 52.34, 117.68),
('ebox', '', 1, 'L', 148, 100, 1.37, 0.41, 19.07, 4.32, 54, 137.33),
('ebox', '', 0, 'C', 113, 60, 4.25, 0.98, 51.39, 4.97, 46.67, 255.26),
('ebox', '', 0, 'E', 13, 7, 2.14, 0.66, 25.71, 5, 42.86, 15),
('ebox', '', 0, 'D', 32, 13, -0.32, -0.07, -4.14, 4.62, 46.15, -4.14),
('ebox', '', 1, 'D', 32, 17, -0.52, -0.12, -7.58, 4.12, 41.18, -8.84),
('ebox', '', 1, 'G', 8, 4, 3.29, 0.96, 49.35, 4, 50, 13.16),
('ebox', '', 0, 'G', 8, 3, 2.31, 0.68, 27.72, 5, 33.33, 6.93),
('ebox', '', 0, 'H', 101, 53, 4.95, 1.18, 57.45, 5.17, 49.06, 262.36),
('ebox', '', 0, 'I', 75, 35, 0.83, 0.19, 10.19, 4.91, 48.57, 29.21),
('ebox', '', 0, 'J', 19, 11, 2, 0.71, 24.88, 4.82, 54.55, 21.98),
('ebox', '', 1, 'K', 23, 17, -1.78, -0.4, -25.16, 4.24, 35.29, -30.19),
('ebox', '', 1, 'M', 119, 74, 1, 0.22, 12.23, 4.89, 41.89, 73.79),
('ebox', '', 0, 'K', 23, 14, -1.47, -0.3, -19.95, 4.43, 42.86, -20.61),
('ebox', '', 0, 'L', 148, 75, 1.6, 0.48, 18.84, 5.11, 54.67, 120.26),
('ebox', '', 0, 'M', 119, 57, 0.45, 0.1, 5.15, 5.26, 38.6, 25.77),
('bbrev', '', 0, 'M', 1113, 115, -0.2, -0.05, -2.62, 4.47, 41.74, -22.43),
('dvgn', '', 0, 'M', 410, 80, -0.45, -0.14, -5.25, 5.14, 41.25, -35.96),
('bbrev', '', 0, 'L', 51, 12, 0.54, 0.23, 6.83, 4.75, 66.67, 6.49),
('bbdrev', '', 0, 'E', 71, 47, 2.23, 0.36, 15.31, 8.72, 59.57, 104.6),
('bbdrev', '', 0, '3', 151, 92, -0.56, -0.09, -4.82, 6.93, 42.39, -51.27),
('bbdrev', '', 0, '5', 25, 15, 4.71, 0.54, 29.02, 9.73, 80, 70.61),
('bbdrev', '', 0, 'A', 14, 12, -5.5, -0.85, -54.26, 6.08, 8.33, -66.01),
('bbdrev', '', 0, 'B', 288, 220, -1.57, -0.25, -14.29, 6.6, 33.64, -346.07),
('bbdrev', '', 0, 'C', 353, 225, -0.28, -0.05, -2.33, 7.21, 48.89, -63.1),
('bbdrev', '', 0, 'D', 192, 124, 1.59, 0.31, 11.81, 8.08, 57.26, 197.24),
('bbdrev', '', 0, 'F', 164, 108, -0.7, -0.13, -5.68, 7.37, 46.3, -75.4),
('bbdrev', '', 0, 'G', 45, 27, 2.79, 0.45, 32.76, 5.11, 62.96, 75.35),
('bbrev', '', 0, 'F', 5, 2, 8.65, 86450, 61.02, 8.5, 100, 17.29),
('bbrev', '', 0, 'C', 7, 1, 2.31, 23100, 27.72, 5, 100, 2.31),
('dvgn', '', 0, 'J', 366, 67, 0.22, 0.06, 2.66, 4.96, 50.75, 14.71),
('dvgn', '', 0, 'I', 339, 114, 0.84, 0.3, 9.87, 5.12, 55.26, 96.1),
('dvgn', '', 0, 'H', 136, 43, 6.64, 1.75, 61.83, 6.44, 65.12, 285.44),
('dvgn', '', 0, 'G', 251, 42, 0.2, 0.04, 2.41, 4.93, 54.76, 8.32),
('dvgn', '', 0, 'F', 16, 4, 0.58, 0.18, 6.99, 5, 50, 2.33),
('dvgn', '', 0, 'E', 102, 16, 2.75, 0.68, 34.33, 4.81, 68.75, 44.06),
('dvgn', '', 0, 'B', 12, 6, 24.22, 4.77, 155.7, 9.33, 83.33, 145.32),
('dvgn', '', 0, 'C', 89, 28, 3.37, 1.11, 34.7, 5.82, 60.71, 94.28),
('dvgn', '', 0, 'D', 203, 60, 1.51, 0.76, 18.05, 5.03, 60, 90.87),
('dbox', '', 0, 'N', 76, 11, -1.75, -0.68, -35.05, 3, 18.18, -19.28),
('bbrev', '', 0, 'D', 66, 23, -0.6, -0.17, -7.86, 4.57, 47.83, -13.76),
('bbrev', '', 0, 'E', 92, 19, 0.9, 0.26, 11.83, 4.58, 42.11, 17.16),
('lvstg', '1', 0, 'E', 57, 25, 1.96, 0.33, 26.22, 4.48, 52, 48.94),
('bbrev', '', 0, 'A', 17, 3, 0.45, 0.09, 5.4, 5, 66.67, 1.35),
('bbrev', '', 0, 'B', 3, 1, -5.55, -1, -166.5, 2, 0, -5.55),
('dbox', '', 0, 'M', 1227, 196, -0.22, -0.07, -4.13, 3.26, 40.31, -43.94),
('bbrev', '', 0, 'G', 227, 85, -0.64, -0.17, -8.34, 4.58, 37.65, -54.07),
('dvgn', '', 0, 'L', 334, 60, -0.57, -0.21, -6.81, 5, 36.67, -34.06),
('bbrev', '', 0, 'H', 16, 4, -1.15, -0.22, -16.27, 4.25, 50, -4.61),
('bbrev', '', 0, 'J', 289, 92, -0.6, -0.16, -7.62, 4.73, 38.04, -55.27),
('bbdrev', '', 0, 'L', 435, 300, -0.42, -0.08, -3.11, 8.05, 46.33, -125.22),
('dvgn', '', 0, 'A', 149, 62, -0.56, -0.08, -6.01, 5.55, 45.16, -34.45),
('bbrev', '', 0, 'I', 88, 29, -1.67, -0.39, -22.33, 4.48, 37.93, -48.38),
('dvgn', '', 0, 'K', 364, 88, 0.11, 0.03, 1.26, 5.4, 40.91, 10.01),
('bbrev', '', 0, 'K', 118, 28, 1.04, 0.25, 13.05, 4.79, 64.29, 29.14),
('bbdrev', '', 0, 'H', 547, 405, -0.91, -0.15, -7.83, 6.98, 39.01, -369.22),
('bbdrev', '', 0, 'I', 389, 232, 0.2, 0.03, 1.66, 7.2, 48.71, 46.26),
('bbdrev', '', 0, 'J', 155, 95, 2.48, 0.42, 17.81, 8.35, 64.21, 235.36),
('bbdrev', '', 0, 'K', 288, 191, -0.26, -0.05, -1.99, 7.81, 47.64, -49.4),
('dvgn_bear', '', 0, 'L', 570, 52, -1.42, -0.48, -28.32, 3, 28.85, -73.62),
('dvgn_bear', '', 0, 'M', 608, 71, -0.12, -0.04, -2.34, 3.17, 45.07, -8.76),
('dvgn_bear', '', 0, 'J', 293, 62, 1.17, 0.34, 22.73, 3.08, 56.45, 72.36),
('dvgn_bear', '', 0, 'K', 591, 60, -1.22, -0.38, -21.49, 3.4, 23.33, -73.08),
('dvgn_bear', '', 0, 'I', 565, 91, 0.59, 0.21, 11.08, 3.2, 48.35, 53.74),
('dvgn_bear', '', 0, 'G', 121, 30, 3.16, 0.82, 59.33, 3.2, 66.67, 94.93),
('dvgn_bear', '', 0, 'H', 702, 145, -2.42, -0.5, -47.06, 3.09, 30.34, -351.41),
('dvgn_bear', '', 0, 'E', 147, 31, 1.5, 0.45, 28.23, 3.19, 58.06, 46.58),
('dvgn_bear', '', 0, 'F', 282, 33, -2.14, -0.55, -34.24, 3.76, 18.18, -70.76),
('dvgn_bear', '', 0, 'D', 362, 58, -1.17, -0.42, -23.59, 2.97, 34.48, -67.61),
('dvgn_bear', '', 0, 'B', 278, 55, -3.8, -0.65, -76.9, 2.96, 21.82, -208.92),
('dvgn_bear', '', 0, 'C', 430, 90, -1.52, -0.38, -30.52, 2.99, 36.67, -136.84),
('dvgn_bear', '', 0, '5', 86, 22, 4.18, 1.13, 83.66, 3, 68.18, 92.03),
('dvgn_bear', '', 0, 'A', 93, 17, -1.08, -0.2, -16.5, 3.94, 35.29, -18.42),
('dvgn_bear', '', 0, '3', 176, 32, 2.52, 0.88, 42.13, 3.59, 65.63, 80.75);

-- --------------------------------------------------------

--
-- 替換檢視表以便查看 `v_bt_para`
--
CREATE TABLE IF NOT EXISTS `v_bt_para` (
`ID` bigint(20) unsigned
,`sys` varchar(10)
,`subsys` varchar(30)
,`num` int(11)
,`firststop_cri` varchar(10)
,`in_days` varchar(10)
,`buy_mode` varchar(10)
,`holddays_cri` varchar(10)
,`min_profit` varchar(10)
,`chg_cri` varchar(10)
,`stop_ma` varchar(10)
,`filt_ma` varchar(10)
,`p14` varchar(10)
,`p15` varchar(10)
,`p16` varchar(10)
,`p17` varchar(10)
,`p18` varchar(10)
,`p19` varchar(10)
,`p20` varchar(10)
,`p21` varchar(10)
,`p22` varchar(10)
,`p23` varchar(10)
,`p24` varchar(10)
,`p25` varchar(10)
);
-- --------------------------------------------------------

--
-- 替換檢視表以便查看 `v_bt_stat`
--
CREATE TABLE IF NOT EXISTS `v_bt_stat` (
`sys` varchar(10)
,`subsys` varchar(30)
,`參數編號` int(11)
,`總筆數` int(11)
,`交易筆數` int(11)
,`交易率%` decimal(16,2)
,`期望值` double
,`獲利` double
,`R期望值` double
,`勝率%` double
,`平均天數` double
,`季期望值` double
,`datecode` varchar(1)
,`begdate` varchar(10)
,`enddate` varchar(10)
,`rmk` varchar(50)
);
-- --------------------------------------------------------

--
-- 檢視表結構 `v_bt_para`
--
DROP TABLE IF EXISTS `v_bt_para`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_bt_para` AS select `bt_para`.`ID` AS `ID`,`bt_para`.`sys` AS `sys`,`bt_para`.`subsys` AS `subsys`,`bt_para`.`num` AS `num`,`bt_para`.`firststop_cri` AS `firststop_cri`,`bt_para`.`in_days` AS `in_days`,`bt_para`.`buy_mode` AS `buy_mode`,`bt_para`.`holddays_cri` AS `holddays_cri`,`bt_para`.`min_profit` AS `min_profit`,`bt_para`.`chg_cri` AS `chg_cri`,`bt_para`.`stop_ma` AS `stop_ma`,`bt_para`.`filt_ma` AS `filt_ma`,`bt_para`.`p14` AS `p14`,`bt_para`.`p15` AS `p15`,`bt_para`.`p16` AS `p16`,`bt_para`.`p17` AS `p17`,`bt_para`.`p18` AS `p18`,`bt_para`.`p19` AS `p19`,`bt_para`.`p20` AS `p20`,`bt_para`.`p21` AS `p21`,`bt_para`.`p22` AS `p22`,`bt_para`.`p23` AS `p23`,`bt_para`.`p24` AS `p24`,`bt_para`.`p25` AS `p25` from `bt_para`;

-- --------------------------------------------------------

--
-- 檢視表結構 `v_bt_stat`
--
DROP TABLE IF EXISTS `v_bt_stat`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_bt_stat` AS select `b`.`sys` AS `sys`,`b`.`subsys` AS `subsys`,`b`.`num` AS `參數編號`,`b`.`sel_recs` AS `總筆數`,`b`.`trade_recs` AS `交易筆數`,round(((`b`.`trade_recs` * 100) / `b`.`sel_recs`),2) AS `交易率%`,`b`.`exp` AS `期望值`,`b`.`net_profit` AS `獲利`,`b`.`exp_r` AS `R期望值`,`b`.`winning` AS `勝率%`,`b`.`avgdays` AS `平均天數`,`b`.`exp_quater` AS `季期望值`,`b`.`datecode` AS `datecode`,`p`.`begdate` AS `begdate`,`p`.`enddate` AS `enddate`,`p`.`rmk` AS `rmk` from (`bt_stat` `b` join `bt_period` `p` on((`b`.`datecode` = `p`.`datecode`))) order by `b`.`sys`,`b`.`subsys`,`b`.`datecode`,`b`.`num`;

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `bt_para`
--
ALTER TABLE `bt_para`
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `sys` (`sys`,`subsys`,`num`);

--
-- 資料表索引 `bt_paraname`
--
ALTER TABLE `bt_paraname`
 ADD PRIMARY KEY (`sys`,`subsys`);

--
-- 資料表索引 `bt_stat`
--
ALTER TABLE `bt_stat`
 ADD PRIMARY KEY (`sys`,`subsys`,`num`,`datecode`);

--
-- 在匯出的資料表使用 AUTO_INCREMENT
--

--
-- 使用資料表 AUTO_INCREMENT `bt_para`
--
ALTER TABLE `bt_para`
MODIFY `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=13;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
