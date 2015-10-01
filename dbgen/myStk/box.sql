SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


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


ALTER TABLE `box`
 ADD PRIMARY KEY (`stockid`,`dte`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
