SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

CREATE DATABASE IF NOT EXISTS `backtesting` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `backtesting`;

DROP TABLE IF EXISTS `box`;
CREATE TABLE IF NOT EXISTS `box` (
  `stockid` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `dte` date NOT NULL DEFAULT '0000-00-00',
  `low1dte` date DEFAULT NULL,
  `low1price` double DEFAULT '0',
  `highdte` date DEFAULT NULL,
  `highprice` double DEFAULT '0',
  `udrate` double DEFAULT '0',
  `low2dte` date DEFAULT NULL,
  `low2price` double DEFAULT '0',
  `boxwidth` double DEFAULT '0',
  `low1va10` double DEFAULT '0',
  `price` double DEFAULT '0',
  `diff_bt` double DEFAULT '0',
  `diff_bb` double DEFAULT '0',
  `hdays` int(11) DEFAULT '0',
  `ldays` int(11) DEFAULT '0',
  `vol` int(11) DEFAULT '0',
  `va5` int(11) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `candlestick`;
CREATE TABLE IF NOT EXISTS `candlestick` (
  `stockid` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `dte` date DEFAULT NULL,
  `ktype` varchar(30) COLLATE utf8_bin DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `dbox`;
CREATE TABLE IF NOT EXISTS `dbox` (
  `stockid` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `dte` date NOT NULL DEFAULT '0000-00-00',
  `low1dte` date DEFAULT NULL,
  `low1price` double DEFAULT '0',
  `highdte` date DEFAULT NULL,
  `highprice` double DEFAULT '0',
  `udrate` double DEFAULT '0',
  `low2dte` date DEFAULT NULL,
  `low2price` double DEFAULT '0',
  `boxwidth` double DEFAULT '0',
  `low1va10` double DEFAULT '0',
  `price` double DEFAULT '0',
  `diff_bt` double DEFAULT '0',
  `diff_bb` double DEFAULT '0',
  `hdays` int(11) DEFAULT '0',
  `ldays` int(11) DEFAULT '0',
  `vol` int(11) DEFAULT '0',
  `va5` int(11) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `lvstg`;
CREATE TABLE IF NOT EXISTS `lvstg` (
  `stockid` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `dte` date DEFAULT NULL,
  `price` double DEFAULT NULL,
  `datecode` varchar(1) COLLATE utf8_bin DEFAULT NULL,
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

DROP TABLE IF EXISTS `lvstg_stat`;
CREATE TABLE IF NOT EXISTS `lvstg_stat` (
  `paranum` int(11) DEFAULT '0',
  `trade_recs` int(11) DEFAULT '0',
  `exp` double DEFAULT '0',
  `net_profit` double DEFAULT '0',
  `exp_r` double DEFAULT '0',
  `winning` double DEFAULT '0',
  `avgdays` double DEFAULT '0',
  `exp_quater` double DEFAULT '0',
  `begdate` date DEFAULT NULL,
  `datecode` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `sel_recs` int(11) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `stk`;
CREATE TABLE IF NOT EXISTS `stk` (
  `stockid` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `dte` date NOT NULL DEFAULT '0000-00-00',
  `p_open` double DEFAULT NULL,
  `p_high` double DEFAULT NULL,
  `p_low` double DEFAULT NULL,
  `price` double DEFAULT NULL,
  `vol` double DEFAULT NULL,
  `updown` double DEFAULT NULL,
  `ma5` double DEFAULT NULL,
  `ma10` double DEFAULT NULL,
  `ma20` double DEFAULT NULL,
  `ma60` double DEFAULT NULL,
  `ma120` double DEFAULT NULL,
  `ma240` double DEFAULT NULL,
  `ma200` double DEFAULT NULL,
  `va5` int(11) DEFAULT NULL,
  `va10` int(11) DEFAULT NULL,
  `va20` int(11) DEFAULT NULL,
  `stddev20` double DEFAULT NULL,
  `ub` double DEFAULT NULL,
  `lb` double DEFAULT NULL,
  `percentb` double DEFAULT NULL,
  `bandwidth` double DEFAULT NULL,
  `kdk` double DEFAULT NULL,
  `kdd` double DEFAULT NULL,
  `sc_kdk` int(11) DEFAULT NULL,
  `sc_kdd` int(11) DEFAULT NULL,
  `dif` double DEFAULT NULL,
  `macd` double DEFAULT NULL,
  `osc` double DEFAULT NULL,
  `sc_osc` int(11) DEFAULT NULL,
  `ent12` double DEFAULT NULL,
  `ent26` double DEFAULT NULL,
  `mfi` double DEFAULT NULL,
  `ptbmfi` double DEFAULT NULL,
  `sc_mfi` int(11) DEFAULT NULL,
  `sc_ma10` int(11) DEFAULT NULL,
  `sc_ma20` int(11) DEFAULT NULL,
  `sc_ma60` int(11) DEFAULT NULL,
  `sc_ma120` int(11) DEFAULT NULL,
  `ud200` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `rsi6` double DEFAULT NULL,
  `ptbrsi6` double DEFAULT NULL,
  `rsi12` double DEFAULT NULL,
  `ptbrsi12` double DEFAULT NULL,
  `emau6` double DEFAULT NULL,
  `emad6` double DEFAULT NULL,
  `emau12` double DEFAULT NULL,
  `emad12` double DEFAULT NULL,
  `di_p` double DEFAULT NULL,
  `di_m` double DEFAULT NULL,
  `adx` double DEFAULT NULL,
  `tr` double DEFAULT NULL,
  `dm_p` double DEFAULT NULL,
  `dm_m` double DEFAULT NULL,
  `sc_adx` int(11) DEFAULT NULL,
  `ptbadx` double DEFAULT NULL,
  `dif_adx` double DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `stkbasic`;
CREATE TABLE IF NOT EXISTS `stkbasic` (
  `stockid` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `qt` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `cashbeop` int(11) DEFAULT '0',
  `cashchg_1` int(11) DEFAULT '0',
  `cashchg_2` int(11) DEFAULT '0',
  `cashchg_3` int(11) DEFAULT '0',
  `cashchg_4` int(11) DEFAULT '0',
  `outcap` double DEFAULT '0',
  `yr` int(11) DEFAULT '0',
  `cashdiv_1` double DEFAULT '0',
  `cashdiv_2` double DEFAULT '0',
  `cashdiv_3` double DEFAULT '0',
  `cashdiv_4` double DEFAULT '0',
  `cashdiv_5` double DEFAULT '0',
  `cashdiv_6` double DEFAULT '0',
  `debt_ratio` double DEFAULT '0',
  `nav` double DEFAULT '0',
  `yr2` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `eps_1` double DEFAULT '0',
  `eps_2` double DEFAULT '0',
  `eps_3` double DEFAULT '0',
  `eps_4` double DEFAULT '0',
  `eps_5` double DEFAULT '0',
  `roe_1` double DEFAULT '0',
  `roe_2` double DEFAULT '0',
  `roe_3` double DEFAULT '0',
  `roe_4` double DEFAULT '0',
  `roe_5` double DEFAULT '0',
  `cur_r` double DEFAULT '0',
  `quick_r` double DEFAULT '0',
  `pricecash_r` double DEFAULT '0',
  `cashdiv_yld` double DEFAULT '0',
  `qeps_1` double DEFAULT '0',
  `qeps_2` double DEFAULT '0',
  `qeps_3` double DEFAULT '0',
  `qeps_4` double DEFAULT '0',
  `qeps_5` double DEFAULT '0',
  `eps4` double DEFAULT '0',
  `pbr` double DEFAULT '0',
  `per` double DEFAULT '0',
  `rev12` int(11) DEFAULT '0',
  `psr` double DEFAULT '0',
  `val` int(11) DEFAULT '0',
  `yrmax` double DEFAULT '0',
  `yrmin` double DEFAULT '0',
  `yrdown` double DEFAULT '0',
  `long_inv_1` int(11) DEFAULT '0',
  `long_inv_5` int(11) DEFAULT '0',
  `fix_ass_1` int(11) DEFAULT '0',
  `fix_ass_5` int(11) DEFAULT '0',
  `nprofita_1` int(11) DEFAULT '0',
  `nprofita_2` int(11) DEFAULT '0',
  `nprofita_3` int(11) DEFAULT '0',
  `nprofita_4` int(11) DEFAULT '0',
  `reinv_rate4` double DEFAULT '0',
  `roe5` double DEFAULT '0',
  `roe_sc` int(11) DEFAULT '0',
  `grmonth` varchar(7) COLLATE utf8_bin DEFAULT NULL,
  `gryoy_1` double DEFAULT '0',
  `gryoy_2` double DEFAULT '0',
  `gryoy_3` double DEFAULT '0',
  `gracc` double DEFAULT '0',
  `qeg_1` double DEFAULT '0',
  `qeg_2` double DEFAULT '0',
  `qeg_3` double DEFAULT '0',
  `qeg_4` double DEFAULT '0',
  `qeg_5` double DEFAULT '0',
  `qeg_6` double DEFAULT '0',
  `qeg_7` double DEFAULT '0',
  `qeg_8` double DEFAULT '0',
  `qeg_sc` int(11) DEFAULT '0',
  `yrmax_dte` date DEFAULT NULL,
  `befup` double DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `stkid`;
CREATE TABLE IF NOT EXISTS `stkid` (
  `stockid` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `stkname` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `cl` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `minbw_dte` date DEFAULT NULL,
  `minbw` double DEFAULT '0',
  `sqdays` int(11) DEFAULT '0',
  `price` double DEFAULT '0',
  `dte` date DEFAULT NULL,
  `rmk` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `flgbasic` varchar(1) COLLATE utf8_bin DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `stop_date`;
CREATE TABLE IF NOT EXISTS `stop_date` (
  `is_bear` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `dte` date DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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

DROP TABLE IF EXISTS `test_period`;
CREATE TABLE IF NOT EXISTS `test_period` (
  `datecode` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `begdate` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `enddate` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `enabled` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `rmk` varchar(50) COLLATE utf8_bin DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
DROP VIEW IF EXISTS `v_lvstg`;
CREATE TABLE IF NOT EXISTS `v_lvstg` (
`stockId` varchar(10)
,`dte` date
,`price` double
,`ptbrsi6` double
,`updown` double
,`vol` double
,`KDK` double
,`sc_osc` int(11)
,`MFI` double
,`va5` int(11)
);DROP VIEW IF EXISTS `v_lvstg_stat`;
CREATE TABLE IF NOT EXISTS `v_lvstg_stat` (
`參數` int(11)
,`平均總筆數` bigint(13)
,`平均交易筆數` bigint(13)
,`平均期望值` double(19,2)
,`持股天數` double(19,2)
,`平均季期望值` double(19,2)
,`平均總獲利` double(19,2)
,`平均R期望值` double(19,2)
,`平均勝率` double
,`datecode` varchar(1)
);DROP TABLE IF EXISTS `v_lvstg`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_lvstg` AS select `s`.`stockid` AS `stockId`,`s`.`dte` AS `dte`,`s`.`price` AS `price`,`s`.`ptbrsi6` AS `ptbrsi6`,`s`.`updown` AS `updown`,`s`.`vol` AS `vol`,`s`.`kdk` AS `KDK`,`s`.`sc_osc` AS `sc_osc`,`s`.`mfi` AS `MFI`,`s`.`va5` AS `va5` from `stk` `s` where ((`s`.`price` > 5) and (`s`.`updown` between 6.5 and 7.1) and (`s`.`vol` > `s`.`va5`) and (`s`.`vol` > 100) and (`s`.`kdk` > 60) and (`s`.`sc_osc` > 0) and (`s`.`mfi` >= 50) and (`s`.`sc_kdk` > 0) and (`s`.`kdd` < `s`.`kdk`) and (`s`.`sc_kdd` > 0));
DROP TABLE IF EXISTS `v_lvstg_stat`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_lvstg_stat` AS select `s`.`paranum` AS `參數`,ceiling(avg(`s`.`sel_recs`)) AS `平均總筆數`,ceiling(avg(`s`.`trade_recs`)) AS `平均交易筆數`,round(avg(`s`.`exp`),2) AS `平均期望值`,round(avg(`s`.`avgdays`),2) AS `持股天數`,round(avg(`s`.`exp_quater`),2) AS `平均季期望值`,round(avg(`s`.`net_profit`),2) AS `平均總獲利`,round(avg(`s`.`exp_r`),2) AS `平均R期望值`,avg(`s`.`winning`) AS `平均勝率`,`s`.`datecode` AS `datecode` from `lvstg_stat` `s` group by `s`.`paranum`,`s`.`datecode` order by `s`.`datecode`,`s`.`paranum`;


ALTER TABLE `box`
 ADD PRIMARY KEY (`stockid`,`dte`);

ALTER TABLE `candlestick`
 ADD KEY `stockid` (`stockid`);

ALTER TABLE `dbox`
 ADD PRIMARY KEY (`stockid`,`dte`);

ALTER TABLE `stk`
 ADD PRIMARY KEY (`stockid`,`dte`);

ALTER TABLE `stkbasic`
 ADD PRIMARY KEY (`stockid`);

ALTER TABLE `stkid`
 ADD PRIMARY KEY (`stockid`);

ALTER TABLE `sysparm`
 ADD PRIMARY KEY (`ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
