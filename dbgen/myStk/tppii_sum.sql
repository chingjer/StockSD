SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


DROP TABLE IF EXISTS `tppii_sum`;
CREATE TABLE IF NOT EXISTS `tppii_sum` (
  `stockid` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `dte` date DEFAULT NULL,
  `t1` int(11) DEFAULT '0',
  `t5` int(11) DEFAULT '0',
  `t20` int(11) DEFAULT '0',
  `t5b` int(11) DEFAULT '0',
  `t5s` int(11) DEFAULT '0',
  `t20b` int(11) DEFAULT '0',
  `t20s` int(11) DEFAULT '0',
  `a1` int(11) DEFAULT '0',
  `a5` int(11) DEFAULT '0',
  `a20` int(11) DEFAULT '0',
  `a5b` int(11) DEFAULT '0',
  `a5s` int(11) DEFAULT '0',
  `a20b` int(11) DEFAULT '0',
  `a20s` int(11) DEFAULT '0',
  `b1` int(11) DEFAULT '0',
  `b5` int(11) DEFAULT '0',
  `b20` int(11) DEFAULT '0',
  `b5b` int(11) DEFAULT '0',
  `b5s` int(11) DEFAULT '0',
  `b20b` int(11) DEFAULT '0',
  `b20s` int(11) DEFAULT '0',
  `c1` int(11) DEFAULT '0',
  `c5` int(11) DEFAULT '0',
  `c20` int(11) DEFAULT '0',
  `c5b` int(11) DEFAULT '0',
  `c5s` int(11) DEFAULT '0',
  `c20b` int(11) DEFAULT '0',
  `c20s` int(11) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


ALTER TABLE `tppii_sum`
 ADD PRIMARY KEY (`stockid`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
