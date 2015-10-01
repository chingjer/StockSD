-- phpMyAdmin SQL Dump
-- version 2.10.3
-- http://www.phpmyadmin.net
-- 
-- 主機: localhost
-- 建立日期: Dec 03, 2014, 02:45 AM
-- 伺服器版本: 5.0.51
-- PHP 版本: 5.2.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

-- 
-- 資料庫: `mystk`
-- 

-- --------------------------------------------------------

-- 
-- 資料表格式： `stk`
-- 

DROP TABLE IF EXISTS `stk`;
CREATE TABLE IF NOT EXISTS `stk` (
  `stockid` varchar(10) collate utf8_bin NOT NULL default '',
  `dte` date NOT NULL default '0000-00-00',
  `p_open` double default NULL,
  `p_high` double default NULL,
  `p_low` double default NULL,
  `price` double default NULL,
  `vol` double default NULL,
  `updown` double default NULL,
  `ma5` double default NULL,
  `ma10` double default NULL,
  `ma20` double default NULL,
  `ma60` double default NULL,
  `ma120` double default NULL,
  `ma240` double default NULL,
  `ma200` double default NULL,
  `va5` int(11) default NULL,
  `va10` int(11) default NULL,
  `va20` int(11) default NULL,
  `stddev20` double default NULL,
  `ub` double default NULL,
  `lb` double default NULL,
  `percentb` double default NULL,
  `bandwidth` double default NULL,
  `kdk` double default NULL,
  `kdd` double default NULL,
  `sc_kdk` int(11) default NULL,
  `sc_kdd` int(11) default NULL,
  `dif` double default NULL,
  `macd` double default NULL,
  `osc` double default NULL,
  `sc_osc` int(11) default NULL,
  `ent12` double default NULL,
  `ent26` double default NULL,
  `mfi` double default NULL,
  `ptbmfi` double default NULL,
  `sc_mfi` int(11) default NULL,
  `sc_ma10` int(11) default NULL,
  `sc_ma20` int(11) default NULL,
  `sc_ma60` int(11) default NULL,
  `sc_ma120` int(11) default NULL,
  `ud200` varchar(1) collate utf8_bin default NULL,
  `rsi6` double default NULL,
  `ptbrsi6` double default NULL,
  `rsi12` double default NULL,
  `ptbrsi12` double default NULL,
  `emau6` double default NULL,
  `emad6` double default NULL,
  `emau12` double default NULL,
  `emad12` double default NULL,
  `di_p` double default NULL,
  `di_m` double default NULL,
  `adx` double default NULL,
  `tr` double default NULL,
  `dm_p` double default NULL,
  `dm_m` double default NULL,
  `sc_adx` int(11) default NULL,
  `ptbadx` double default NULL,
  `dif_adx` double default NULL,
  PRIMARY KEY  (`stockid`,`dte`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- 
-- 列出以下資料庫的數據： `stk`
-- 

