SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


DROP TABLE IF EXISTS `grp`;
CREATE TABLE IF NOT EXISTS `grp` (
`id` bigint(20) unsigned NOT NULL,
  `cl1` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `stockid` varchar(10) COLLATE utf8_bin DEFAULT NULL
) ENGINE=MyISAM AUTO_INCREMENT=983 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `grp` (`id`, `cl1`, `stockid`) VALUES
(1, '生技', '1701'),
(2, '生技', '1707'),
(3, '生技', '1716'),
(4, '生技', '1720'),
(5, '生技', '1729'),
(6, '生技', '1731'),
(7, '生技', '1733'),
(8, '生技', '1734'),
(9, '生技', '1736'),
(10, '生技', '3164'),
(11, '生技', '4104'),
(12, '生技', '4106'),
(13, '生技', '4108'),
(14, '生技', '4119'),
(15, '生技', '4133'),
(16, '生技', '1565'),
(17, '生技', '1752'),
(18, '生技', '1777'),
(19, '生技', '1781'),
(20, '生技', '1784'),
(21, '生技', '1788'),
(22, '生技', '1795'),
(23, '生技', '1799'),
(24, '生技', '1813'),
(25, '生技', '3118'),
(26, '生技', '3205'),
(27, '生技', '3218'),
(28, '生技', '3266'),
(29, '生技', '4102'),
(30, '生技', '4103'),
(31, '生技', '4105'),
(32, '生技', '4107'),
(33, '生技', '4109'),
(34, '生技', '4111'),
(35, '生技', '4114'),
(36, '生技', '4120'),
(37, '生技', '4121'),
(38, '生技', '4123'),
(39, '生技', '4126'),
(40, '生技', '4127'),
(41, '生技', '4128'),
(42, '生技', '4129'),
(43, '生技', '4131'),
(44, '觀光', '2701'),
(45, '觀光', '2702'),
(46, '觀光', '2704'),
(47, '觀光', '2705'),
(48, '觀光', '2706'),
(49, '觀光', '2707'),
(50, '觀光', '8940'),
(51, '觀光', '5701'),
(52, '觀光', '5703'),
(53, '觀光', '5704'),
(54, '觀光', '5706'),
(55, '0050', '0050'),
(56, '太陽能', '6244'),
(57, '太陽能', '3452'),
(58, '太陽能', '5483'),
(59, '太陽能', '6182'),
(60, '太陽能', '3514'),
(61, '太陽能', '3532'),
(62, '太陽能', '3016'),
(63, '太陽能', '2434'),
(64, '太陽能', '6125'),
(65, '太陽能', '3043'),
(66, '太陽能', '1711'),
(67, '太陽能', '3561'),
(68, '太陽能', '2342'),
(69, '太陽能', '2367'),
(70, '太陽能', '2323'),
(71, '太陽能', '5425'),
(72, '太陽能', '2426'),
(73, '太陽能', '2491'),
(74, '太陽能', '2481'),
(75, '太陽能', '5326'),
(76, '農金', '1905'),
(77, '農金', '1708'),
(78, '農金', '1219'),
(79, '農金', '1314'),
(80, '農金', '1709'),
(81, '農金', '1710'),
(82, '農金', '1712'),
(83, '農金', '1727'),
(84, '農金', '1907'),
(85, '農金', '4702'),
(86, '農金', '4721'),
(87, '農金', '6508'),
(88, '農金', '1722'),
(89, 'IC設計', '2454'),
(90, 'IC設計', '2458'),
(91, 'IC設計', '3006'),
(92, 'IC設計', '3014'),
(93, 'IC設計', '3056'),
(94, 'IC設計', '3227'),
(95, 'IC設計', '3228'),
(96, 'IC設計', '3438'),
(97, 'IC設計', '3443'),
(98, 'IC設計', '6186'),
(99, 'IC設計', '6195'),
(100, 'IC設計', '6198'),
(101, 'IC設計', '6202'),
(102, 'IC設計', '6229'),
(103, 'IC設計', '6231'),
(104, 'IC設計', '6233'),
(105, 'IC設計', '6237'),
(106, 'IC設計', '6243'),
(107, 'IC設計', '6286'),
(108, 'IC設計', '8081'),
(109, 'IC設計', '8299'),
(110, 'IC設計', '2379'),
(111, 'IC設計', '2363'),
(112, 'IC設計', '2436'),
(113, 'IC設計', '5302'),
(114, 'IC設計', '5314'),
(115, 'IC設計', '2388'),
(116, 'IC設計', '5471'),
(117, 'IC設計', '2401'),
(118, 'IC設計', '5351'),
(119, 'IC設計', '5468'),
(120, 'IC設計', '5487'),
(121, 'IC設計', '6103'),
(122, 'IC設計', '6104'),
(123, 'IC設計', '6129'),
(124, 'IC設計', '6138'),
(125, 'IC設計', '3034'),
(126, 'IC設計', '3035'),
(127, 'IC設計', '3041'),
(128, '網通設備', '2354'),
(129, '網通設備', '6142'),
(130, '網通設備', '2314'),
(131, '網通設備', '3062'),
(132, '網通設備', '2444'),
(133, '網通設備', '2332'),
(134, '網通設備', '6152'),
(135, '網通設備', '2391'),
(136, '網通設備', '2485'),
(137, '網通設備', '6285'),
(138, '網通設備', '3466'),
(139, '網通設備', '9105'),
(140, '網通設備', '6220'),
(141, '網通設備', '6277'),
(142, '網通設備', '6163'),
(143, '網通設備', '3047'),
(144, '中概', '2327'),
(145, '中概', '1507'),
(146, '中概', '2477'),
(147, '中概', '1473'),
(148, '中概', '1313'),
(149, '中概', '1525'),
(150, '中概', '1905'),
(151, '中概', '2316'),
(152, '中概', '1402'),
(153, '中概', '2105'),
(154, '中概', '1532'),
(155, '中概', '2317'),
(156, '中概', '2375'),
(157, '中概', '3008'),
(158, '中概', '2430'),
(159, '中概', '1101'),
(160, '中概', '2102'),
(161, '中概', '2367'),
(162, '中概', '2106'),
(163, '中概', '2101'),
(164, '中概', '9910'),
(165, '中概', '1907'),
(166, '中概', '1717'),
(167, '中概', '2301'),
(168, '中概', '8008'),
(169, '中概', '1516'),
(170, '中概', '9904'),
(171, '中概', '3037'),
(172, '中概', '9914'),
(173, '中概', '5469'),
(174, '中概', '1301'),
(175, '中概', '9945'),
(176, '中概', '9911'),
(177, '中概', '1531'),
(178, '中概', '1210'),
(179, '中概', '1319'),
(180, '中概', '2911'),
(181, '中概', '2012'),
(182, '中概', '2204'),
(183, '中概', '2227'),
(184, '中概', '5905'),
(185, '中概', '5609'),
(186, '中概', '1802'),
(187, '中概', '9921'),
(188, '中概', '2454'),
(189, '中概', '1303'),
(190, '中概', '9919'),
(191, '中概', '1216'),
(192, '中概', '2362'),
(193, '中概', '2328'),
(194, '中概', '2461'),
(195, '中概', '2347'),
(196, '中概', '1201'),
(197, '中概', '2352'),
(198, '中概', '2201'),
(199, '中概', '1714'),
(200, '中概', '1103'),
(201, '中概', '1605'),
(202, '中概', '2324'),
(203, '中概', '3702'),
(204, '中概', '3044'),
(205, '中概', '1702'),
(206, '中概', '9939'),
(207, '中概', '2474'),
(208, '中概', '2354'),
(209, '中概', '2382'),
(210, '中概', '3164'),
(211, '中概', '1718'),
(212, '中概', '2475'),
(213, '中概', '2308'),
(214, '水資源', '1722'),
(215, '水資源', '5434'),
(216, '水資源', '9930'),
(217, '水資源', '1108'),
(218, '水資源', '1216'),
(219, '水資源', '1218'),
(220, '水資源', '1737'),
(221, '水資源', '1535'),
(222, '水資源', '9919'),
(223, '水資源', '8925'),
(224, '水資源', '8936'),
(225, '水資源', '6508'),
(226, '水資源', '8383'),
(227, '搏奕', '3229'),
(228, '搏奕', '8076'),
(229, '搏奕', '6105'),
(230, '搏奕', '6206'),
(231, '搏奕', '8050'),
(232, '搏奕', '2463'),
(233, '搏奕', '2397'),
(234, '搏奕', '2395'),
(235, '搏奕', '3022'),
(236, '搏奕', '3416'),
(237, '搏奕', '3088'),
(238, '搏奕', '5205'),
(239, '搏奕', '3064'),
(240, '金融', '0055'),
(241, '物聯網', '3535'),
(242, '物聯網', '2414'),
(243, '物聯網', '3169'),
(244, '物聯網', '1907'),
(245, '物聯網', '8034'),
(246, '物聯網', '2482'),
(247, '物聯網', '3419'),
(248, '物聯網', '3559'),
(249, '物聯網', '8114'),
(250, '物聯網', '2308'),
(251, '物聯網', '6202'),
(252, '物聯網', '5353'),
(253, '物聯網', '2321'),
(254, '物聯網', '2461'),
(255, '物聯網', '3652'),
(256, '物聯網', '5490'),
(257, '物聯網', '3611'),
(258, '物聯網', '6233'),
(259, '智慧電網', '2444'),
(260, '智慧電網', '3152'),
(261, '智慧電網', '2413'),
(262, '智慧電網', '5388'),
(263, '智慧電網', '6282'),
(264, '智慧電網', '2420'),
(265, '智慧電網', '3296'),
(266, '智慧電網', '3380'),
(267, '智慧電網', '3027'),
(268, '智慧電網', '1503'),
(269, '智慧電網', '2308'),
(270, '智慧電網', '4906'),
(271, '智慧電網', '2395'),
(272, '智慧電網', '2371'),
(273, '智慧電網', '2391'),
(274, '智慧電網', '2419'),
(275, '智慧電網', '1514'),
(276, '智慧電網', '8040'),
(277, '智慧電網', '3047'),
(278, '智慧電網', '3094'),
(279, '智慧電網', '1513'),
(280, '智慧電網', '6233'),
(281, '智慧電網', '3025'),
(282, '智慧電網', '1519'),
(283, '智慧電網', '3577'),
(284, '雲端運算', '2496'),
(285, '雲端運算', '2317'),
(286, '雲端運算', '2465'),
(287, '雲端運算', '3126'),
(288, '雲端運算', '3231'),
(289, '雲端運算', '2353'),
(290, '雲端運算', '3324'),
(291, '雲端運算', '6230'),
(292, '雲端運算', '2357'),
(293, '雲端運算', '2345'),
(294, '雲端運算', '3062'),
(295, '雲端運算', '6282'),
(296, '雲端運算', '4908'),
(297, '雲端運算', '6277'),
(298, '雲端運算', '3291'),
(299, '雲端運算', '3058'),
(300, '雲端運算', '2420'),
(301, '雲端運算', '6214'),
(302, '雲端運算', '2412'),
(303, '雲端運算', '4904'),
(304, '雲端運算', '3045'),
(305, '雲端運算', '3380'),
(306, '雲端運算', '2354'),
(307, '雲端運算', '2421'),
(308, '雲端運算', '3017'),
(309, '雲端運算', '2377'),
(310, '雲端運算', '8234'),
(311, '雲端運算', '6179'),
(312, '雲端運算', '2324'),
(313, '雲端運算', '6263'),
(314, '雲端運算', '2332'),
(315, '雲端運算', '2308'),
(316, '雲端運算', '4906'),
(317, '雲端運算', '3664'),
(318, '雲端運算', '2495'),
(319, '雲端運算', '3483'),
(320, '雲端運算', '2376'),
(321, '雲端運算', '2356'),
(322, '雲端運算', '3015'),
(323, '雲端運算', '2382'),
(324, '雲端運算', '3450'),
(325, '雲端運算', '2391'),
(326, '雲端運算', '2315'),
(327, '雲端運算', '2059'),
(328, '雲端運算', '3057'),
(329, '雲端運算', '6245'),
(330, '雲端運算', '3029'),
(331, '雲端運算', '2498'),
(332, '雲端運算', '4903'),
(333, '雲端運算', '2480'),
(334, '雲端運算', '2453'),
(335, '雲端運算', '3060'),
(336, '電動車', '2201'),
(337, '電動車', '3211'),
(338, '電動車', '2360'),
(339, '電動車', '6121'),
(340, '電動車', '2104'),
(341, '電動車', '3291'),
(342, '電動車', '1503'),
(343, '電動車', '1504'),
(344, '電動車', '2308'),
(345, '電動車', '6170'),
(346, '電動車', '1729'),
(347, '電動車', '6279'),
(348, '資產', '1722'),
(349, '資產', '1902'),
(350, '資產', '2201'),
(351, '資產', '1419'),
(352, '資產', '2102'),
(353, '資產', '2206'),
(354, '資產', '1614'),
(355, '資產', '1437'),
(356, '資產', '1402'),
(357, '資產', '2101'),
(358, '資產', '1903'),
(359, '資產', '2107'),
(360, '資產', '1314'),
(361, '資產', '2913'),
(362, '資產', '9902'),
(363, '資產', '1532'),
(364, '資產', '2705'),
(365, '資產', '1503'),
(366, '資產', '1504'),
(367, '資產', '1713'),
(368, '資產', '2901'),
(369, '資產', '1712'),
(370, '資產', '1702'),
(371, '資產', '2371'),
(372, '資產', '1441'),
(373, '資產', '2035'),
(374, '資產', '2008'),
(375, 'LED', '3383'),
(376, 'LED', '2426'),
(377, 'LED', '6213'),
(378, 'LED', '2317'),
(379, 'LED', '3339'),
(380, 'LED', '8107'),
(381, 'LED', '6261'),
(382, 'LED', '6255'),
(383, 'LED', '3026'),
(384, 'LED', '2409'),
(385, 'LED', '3066'),
(386, 'LED', '6230'),
(387, 'LED', '3527'),
(388, 'LED', '3061'),
(389, 'LED', '2393'),
(390, 'LED', '6271'),
(391, 'LED', '6176'),
(392, 'LED', '5483'),
(393, 'LED', '6224'),
(394, 'LED', '3080'),
(395, 'LED', '8121'),
(396, 'LED', '5371'),
(397, 'LED', '3531'),
(398, 'LED', '3288'),
(399, 'LED', '5383'),
(400, 'LED', '6164'),
(401, 'LED', '2455'),
(402, 'LED', '3579'),
(403, 'LED', '2301'),
(404, 'LED', '2354'),
(405, 'LED', '2486'),
(406, 'LED', '3017'),
(407, 'LED', '3031'),
(408, 'LED', '2340'),
(409, 'LED', '6182'),
(410, 'LED', '5355'),
(411, 'LED', '6127'),
(412, 'LED', '2499'),
(413, 'LED', '2308'),
(414, 'LED', '2489'),
(415, 'LED', '6226'),
(416, 'LED', '3016'),
(417, 'LED', '6289'),
(418, 'LED', '3406'),
(419, 'LED', '2371'),
(420, 'LED', '6168'),
(421, 'LED', '3512'),
(422, 'LED', '2448'),
(423, 'LED', '3653'),
(424, 'LED', '8111'),
(425, 'LED', '2351'),
(426, 'ECFA', '6603'),
(427, 'ECFA', '1525'),
(428, 'ECFA', '1512'),
(429, 'ECFA', '4510'),
(430, 'ECFA', '1507'),
(431, 'ECFA', '1473'),
(432, 'ECFA', '4533'),
(433, 'ECFA', '1528'),
(434, 'ECFA', '1313'),
(435, 'ECFA', '2105'),
(436, 'ECFA', '2201'),
(437, 'ECFA', '1440'),
(438, 'ECFA', '1419'),
(439, 'ECFA', '2102'),
(440, 'ECFA', '1531'),
(441, 'ECFA', '2206'),
(442, 'ECFA', '1304'),
(443, 'ECFA', '1717'),
(444, 'ECFA', '2887'),
(445, 'ECFA', '1402'),
(446, 'ECFA', '2101'),
(447, 'ECFA', '2106'),
(448, 'ECFA', '2006'),
(449, 'ECFA', '9921'),
(450, 'ECFA', '2615'),
(451, 'ECFA', '2103'),
(452, 'ECFA', '1314'),
(453, 'ECFA', '1409'),
(454, 'ECFA', '1533'),
(455, 'ECFA', '1301'),
(456, 'ECFA', '1305'),
(457, 'ECFA', '1101'),
(458, 'ECFA', '1541'),
(459, 'ECFA', '1417'),
(460, 'ECFA', '1303'),
(461, 'ECFA', '5609'),
(462, 'ECFA', '2881'),
(463, 'ECFA', '1523'),
(464, 'ECFA', '2880'),
(465, 'ECFA', '2204'),
(466, 'ECFA', '1522'),
(467, 'ECFA', '2015'),
(468, 'ECFA', '2002'),
(469, 'ECFA', '1310'),
(470, 'ECFA', '1319'),
(471, 'ECFA', '2029'),
(472, 'ECFA', '1521'),
(473, 'ECFA', '9914'),
(474, 'ECFA', '1309'),
(475, 'ECFA', '2891'),
(476, 'ECFA', '2883'),
(477, 'ECFA', '1447'),
(478, 'ECFA', '1503'),
(479, 'ECFA', '2022'),
(480, 'ECFA', '5608'),
(481, 'ECFA', '4526'),
(482, 'ECFA', '1102'),
(483, 'ECFA', '1560'),
(484, 'ECFA', '1583'),
(485, 'ECFA', '2885'),
(486, 'ECFA', '1477'),
(487, 'ECFA', '1527'),
(488, 'ECFA', '6505'),
(489, 'ECFA', '2612'),
(490, 'ECFA', '1326'),
(491, 'ECFA', '1312'),
(492, 'ECFA', '1605'),
(493, 'ECFA', '2049'),
(494, 'ECFA', '2882'),
(495, 'ECFA', '2601'),
(496, 'ECFA', '2823'),
(497, 'ECFA', '2617'),
(498, 'ECFA', '2023'),
(499, 'ECFA', '1530'),
(500, 'ECFA', '1524'),
(501, 'ECFA', '2892'),
(502, 'ECFA', '1517'),
(503, 'ECFA', '1455'),
(504, 'ECFA', '1466'),
(505, '中國內需', '9945'),
(506, '中國內需', '9921'),
(507, '中國內需', '2347'),
(508, '中國內需', '2903'),
(509, '中國內需', '2362'),
(510, '中國內需', '2915'),
(511, '中國內需', '3702'),
(512, '中國內需', '2911'),
(513, 'Kinect', '3630'),
(514, 'Kinect', '2317'),
(515, 'Kinect', '3008'),
(516, 'Kinect', '5469'),
(517, 'Kinect', '2421'),
(518, 'Kinect', '8046'),
(519, 'Kinect', '3501'),
(520, 'Kinect', '2308'),
(521, 'Kinect', '2385'),
(522, 'Kinect', '6115'),
(523, 'Kinect', '2382'),
(524, 'Kinect', '3450'),
(525, 'Kinect', '8008'),
(526, 'Kinect', '2363'),
(527, 'Kinect', '2392'),
(528, 'Kinect', '3071'),
(529, 'Kinect', '6194'),
(530, 'Kinect', '3653'),
(531, 'Kinect', '3011'),
(532, 'iPhone', '2474'),
(533, 'iPhone', '3008'),
(534, 'iPhone', '8046'),
(535, 'iPhone', '2392'),
(536, 'iPhone', '3095'),
(537, 'iPhone', '2402'),
(538, 'iPhone', '2313'),
(539, 'iPhone', '2384'),
(540, 'iPhone', '2327'),
(541, 'iPhone', '3037'),
(542, 'iPhone', '6269'),
(543, 'iPhone', '2317'),
(544, 'iPhone', '8039'),
(545, 'iPhone', '2354'),
(546, 'iPhone', '3042'),
(547, '觸控面板', '3623'),
(548, '觸控面板', '2384'),
(549, '觸控面板', '3584'),
(550, '觸控面板', '4729'),
(551, '觸控面板', '8240'),
(552, '觸控面板', '3049'),
(553, '觸控面板', '3629'),
(554, '觸控面板', '3622'),
(555, '觸控面板', '3615'),
(556, '觸控面板', '3038'),
(557, '觸控面板', '3557'),
(558, '觸控面板', '8105'),
(559, '觸控面板', '1324'),
(560, '觸控面板', '3523'),
(561, '觸控面板', '3545'),
(562, '觸控面板', '6167'),
(563, '觸控面板', '2403'),
(564, '觸控面板', '6125'),
(565, '觸控面板', '8084'),
(566, '觸控面板', '6243'),
(567, '觸控面板', '2363'),
(568, '觸控面板', '3227'),
(569, '觸控面板', '3014'),
(570, '觸控面板', '3556'),
(571, '觸控面板', '5471'),
(572, '觸控面板', '6298'),
(573, '觸控面板', '5487'),
(574, '觸控面板', '2382'),
(575, '觸控面板', '2436'),
(576, '觸控面板', '2458'),
(577, '觸控面板', '3416'),
(578, '觸控面板', '6202'),
(579, '觸控面板', '6116'),
(580, '觸控面板', '3028'),
(581, '觸控面板', '3481'),
(582, '觸控面板', '3607'),
(583, '觸控面板', '6153'),
(584, '觸控面板', '2409'),
(585, '觸控面板', '8266'),
(586, '觸控面板', '6269'),
(587, '觸控面板', '8077'),
(588, '智慧型手機', '3556'),
(589, '智慧型手機', '2384'),
(590, '智慧型手機', '6269'),
(591, '智慧型手機', '6153'),
(592, '智慧型手機', '3622'),
(593, '智慧型手機', '2357'),
(594, '智慧型手機', '5211'),
(595, '智慧型手機', '3152'),
(596, '智慧型手機', '2402'),
(597, '智慧型手機', '3037'),
(598, '智慧型手機', '2458'),
(599, '智慧型手機', '2301'),
(600, '智慧型手機', '8078'),
(601, '智慧型手機', '2474'),
(602, '智慧型手機', '3545'),
(603, '智慧型手機', '3511'),
(604, '智慧型手機', '3042'),
(605, '智慧型手機', '2313'),
(606, '智慧型手機', '2376'),
(607, '智慧型手機', '3095'),
(608, '智慧型手機', '2353'),
(609, '智慧型手機', '3311'),
(610, '智慧型手機', '2498'),
(611, '智慧型手機', '3034'),
(612, '智慧型手機', '2388'),
(613, '智慧型手機', '2392'),
(614, '智慧型手機', '2354'),
(615, '智慧型手機', '8101'),
(616, '智慧型手機', '2439'),
(617, '智慧型手機', '2317'),
(618, '智慧型手機', '3008'),
(619, '智慧型手機', '2454'),
(620, '智慧型手機', '3584'),
(621, '智慧型手機', '8105'),
(622, '智慧型手機', '8086'),
(623, '智慧型手機', '2367'),
(624, 'HDI', '3037'),
(625, 'HDI', '2313'),
(626, 'HDI', '2367'),
(627, 'HDI', '3044'),
(628, 'HDI', '2316'),
(629, 'HDI', '6141'),
(630, 'HDI', '8046'),
(631, 'HDI', '5469'),
(632, 'HDI', '2368'),
(633, 'HDI', '6251'),
(634, '中國內需', '1702'),
(635, '風力發電', '1504'),
(636, '風力發電', '1513'),
(637, '風力發電', '1514'),
(638, '風力發電', '1519'),
(639, '風力發電', '4733'),
(640, '太陽能', '1704'),
(641, 'Ultrabook', '2458'),
(642, 'Ultrabook', '2379'),
(643, 'Ultrabook', '6230'),
(644, 'Ultrabook', '1569'),
(645, 'Ultrabook', '3376'),
(646, 'Ultrabook', '3526'),
(647, 'Ultrabook', '3324'),
(648, 'Ultrabook', '6231'),
(649, '中國內需', '1103'),
(650, '中國內需', '9151'),
(651, '中國內需', '1218'),
(652, '中國內需', '1216'),
(653, '中國內需', '9939'),
(654, '中國內需', '6192'),
(655, '中國內需', '9905'),
(656, '中國內需', '9907'),
(657, '中國內需', '9911'),
(658, '中國內需', '9934'),
(659, '中國內需', '2607'),
(660, '中國內需', '1507'),
(661, '中國內需', '9914'),
(662, '中國內需', '4532'),
(663, '中國內需', '2204'),
(664, '中國內需', '1802'),
(665, '中國內需', '9904'),
(666, '中國內需', '2105'),
(667, '中國內需', '2601'),
(668, '中國內需', '1319'),
(669, '中國內需', '1101'),
(670, '中國內需', '1102'),
(671, '中國內需', '5312'),
(672, '中國內需', '9941'),
(673, '中國內需', '5903'),
(674, '中國內需', '2450'),
(675, 'LED', '3698'),
(676, '生技', '6130'),
(677, '4G光纖', '5349'),
(678, '4G光纖', '3363'),
(679, '4G光纖', '3450'),
(680, '4G光纖', '3419'),
(681, '4G光纖', '4909'),
(682, '4G光纖', '4979'),
(683, '4G光纖', '4966'),
(684, '4G光纖', '2314'),
(685, '4G光纖', '3491'),
(686, '4G光纖', '2334'),
(687, '4G光纖', '4908'),
(688, '4G光纖', '8155'),
(689, '4G光纖', '3062'),
(690, '4G光纖', '4906'),
(691, '4G光纖', '2345'),
(692, '4G光纖', '2332'),
(693, '生技', '4152'),
(694, '生技', '4162'),
(695, '生技', '4164'),
(696, '生技', '8406'),
(697, '生技', '3176'),
(698, '生技', '1789'),
(699, '生技', '4743'),
(700, 'NFC', '3529'),
(701, 'NFC', '2436'),
(702, 'NFC', '6285'),
(703, 'NFC', '2498'),
(704, 'NFC', '8101'),
(705, 'NFC', '8078'),
(706, 'NFC', '6146'),
(707, 'NFC', '3697'),
(708, 'NFC', '6155'),
(709, 'NFC', '2412'),
(710, 'NFC', '3045'),
(711, 'NFC', '4904'),
(712, 'NFC', '6160'),
(713, 'NFC', '2414'),
(714, 'NFC', '3652'),
(715, 'NFC', '2482'),
(716, 'NFC', '5490'),
(717, 'NFC', '3094'),
(718, 'NFC', '3535'),
(719, 'NFC', '6202'),
(720, 'NFC', '3169'),
(721, 'NFC', '3419'),
(722, 'NFC', '6233'),
(723, 'NFC', '3611'),
(724, '汽車零組件', '1319'),
(725, '汽車零組件', '1339'),
(726, '汽車零組件', '1338'),
(727, '汽車零組件', '1524'),
(728, '汽車零組件', '1512'),
(729, '汽車零組件', '1506'),
(730, '汽車零組件', '1536'),
(731, '汽車零組件', '1521'),
(732, '汽車零組件', '1522'),
(733, '汽車零組件', '6605'),
(734, '汽車零組件', '1525'),
(735, '汽車零組件', '4523'),
(736, '汽車零組件', '3552'),
(737, '汽車零組件', '6279'),
(738, '汽車零組件', '2231'),
(739, '汽車零組件', '8255'),
(740, '汽車零組件', '2355'),
(741, '汽車零組件', '4535'),
(742, '汽車零組件', '1586'),
(743, '汽車零組件', '2233'),
(744, '汽車零組件', '9942'),
(745, '汽車零組件', '2497'),
(746, '汽車零組件', '1533'),
(747, '汽車零組件', '5007'),
(748, '環保', '1535'),
(749, '環保', '8383'),
(750, '環保', '8435'),
(751, '環保', '8925'),
(752, '環保', '911610'),
(753, '環保', '1504'),
(754, '環保', '4733'),
(755, '環保', '1589'),
(756, '環保', '6192'),
(757, '環保', '6282'),
(758, '環保', '8996'),
(759, '環保', '3631'),
(760, '環保', '6803'),
(761, '環保', '8390'),
(762, '環保', '8422'),
(763, '環保', '9927'),
(764, '環保', '1337'),
(765, '環保', '8423'),
(766, '環保', '1723'),
(767, '環保', '1729'),
(768, '環保', '2308'),
(769, '環保', '2233'),
(770, '環保', '6121'),
(771, '環保', '6170'),
(772, '4G光纖', '3163'),
(773, '4G光纖', '3234'),
(774, '4G光纖', '4984'),
(775, '4G光纖', '3596'),
(776, '4G光纖', '8011'),
(777, '工具機', '1566'),
(778, '工具機', '1597'),
(779, '工具機', '1590'),
(780, '工具機', '2049'),
(781, '工具機', '1530'),
(782, '工具機', '1540'),
(783, '工具機', '1583'),
(784, '工具機', '4510'),
(785, '工具機', '4513'),
(786, '工具機', '1528'),
(787, '工具機', '4526'),
(788, '工具機', '4533'),
(789, '工具機', '6603'),
(790, '工具機', '6609'),
(791, '工具機', '1515'),
(792, '工具機', '1527'),
(793, '工具機', '1539'),
(794, '工具機', '1541'),
(795, '工具機', '1570'),
(796, '機場捷運', '5607'),
(797, '機場捷運', '1904'),
(798, '機場捷運', '2520'),
(799, '機場捷運', '1711'),
(800, '機場捷運', '1463'),
(801, '機場捷運', '1234'),
(802, '機場捷運', '2102'),
(803, '機場捷運', '1504'),
(804, '機場捷運', '1409'),
(805, '機場捷運', '1416'),
(806, '機場捷運', '2607'),
(807, '機場捷運', '1410'),
(808, '機場捷運', '1321'),
(809, '機場捷運', '2107'),
(810, '機場捷運', '2538'),
(811, '機場捷運', '1903'),
(812, '機場捷運', '2545'),
(813, '機場捷運', '2901'),
(814, '機場捷運', '1203'),
(815, '機場捷運', '1201'),
(816, '機場捷運', '1614'),
(817, '機場捷運', '1437'),
(818, '機場捷運', '2882'),
(819, 'DRAM', '8271'),
(820, 'DRAM', '6145'),
(821, 'DRAM', '2451'),
(822, 'DRAM', '4973'),
(823, 'DRAM', '3260'),
(824, 'DRAM', '8088'),
(825, 'DRAM', '2425'),
(826, 'DRAM', '3252'),
(827, 'DRAM', '8277'),
(828, 'DRAM', '2348'),
(829, 'DRAM', '2344'),
(830, 'DRAM', '2408'),
(831, 'DRAM', '3474'),
(832, 'iPhone', '4938'),
(833, '智慧型手機', '4938'),
(834, '4G光纖', '6274'),
(835, '3D列印', '8416'),
(836, '3D列印', '3504'),
(837, '3D列印', '2388'),
(838, '3D列印', '3005'),
(839, '3D列印', '4722'),
(840, '3D列印', '8925'),
(841, '3D列印', '3663'),
(842, '3D列印', '3570'),
(843, '3D列印', '1597'),
(844, '3D列印', '3022'),
(845, '3D列印', '2312'),
(846, '3D列印', '5371'),
(847, '電子商務', '2608'),
(848, '電子商務', '2636'),
(849, '電子商務', '5609'),
(850, '電子商務', '4965'),
(851, '電子商務', '8044'),
(852, '電子商務', '6245'),
(853, '電子商務', '6183'),
(854, '電子商務', '1626'),
(855, '電子商務', '2719'),
(856, '電子商務', '2912'),
(857, '電子商務', '3130'),
(858, '電子商務', '3687'),
(859, '電子商務', '5278'),
(860, '電子商務', '5706'),
(861, '工業電腦', '2395'),
(862, '工業電腦', '2397'),
(863, '工業電腦', '3022'),
(864, '工業電腦', '3088'),
(865, '工業電腦', '3416'),
(866, '工業電腦', '3479'),
(867, '工業電腦', '3521'),
(868, '工業電腦', '3577'),
(869, '工業電腦', '3652'),
(870, '工業電腦', '6105'),
(871, '工業電腦', '6166'),
(872, '工業電腦', '6206'),
(873, '工業電腦', '6245'),
(874, '工業電腦', '8050'),
(875, '工業電腦', '8076'),
(876, '工業電腦', '8114'),
(877, '工業電腦', '8234'),
(878, '環保', '1539'),
(879, '環保', '1513'),
(880, '風力發電', '8435'),
(881, '風力發電', '1589'),
(882, '越南概念', '1108'),
(883, '越南概念', '1301'),
(884, '越南概念', '1303'),
(885, '越南概念', '1315'),
(886, '越南概念', '1326'),
(887, '越南概念', '1434'),
(888, '越南概念', '1503'),
(889, '越南概念', '1537'),
(890, '越南概念', '1609'),
(891, '越南概念', '1616'),
(892, '越南概念', '1203'),
(893, '越南概念', '1210'),
(894, '越南概念', '1216'),
(895, '越南概念', '1440'),
(896, '越南概念', '1463'),
(897, '越南概念', '1476'),
(898, '越南概念', '1477'),
(899, '越南概念', '4432'),
(900, '越南概念', '4401'),
(901, '越南概念', '9944'),
(902, '越南概念', '1902'),
(903, '越南概念', '2002'),
(904, '越南概念', '5016'),
(905, '越南概念', '2106'),
(906, '越南概念', '2206'),
(907, '越南概念', '2317'),
(908, '越南概念', '2324'),
(909, '越南概念', '2353'),
(910, '越南概念', '2384'),
(911, '越南概念', '3481'),
(912, '越南概念', '3622'),
(913, '越南概念', '911868'),
(914, '越南概念', '9904'),
(915, '越南概念', '9910'),
(916, '越南概念', '1307'),
(917, '越南概念', '9938'),
(918, '越南概念', '9905'),
(919, '越南概念', '9939'),
(920, 'NFC', '3068'),
(921, '企業換機潮', '6231'),
(922, '企業換機潮', '2385'),
(923, '企業換機潮', '5215'),
(924, '企業換機潮', '2387'),
(925, '企業換機潮', '8163'),
(926, '企業換機潮', '6230'),
(927, '企業換機潮', '3338'),
(928, '企業換機潮', '3653'),
(929, '企業換機潮', '3017'),
(930, '企業換機潮', '6124'),
(931, '企業換機潮', '3324'),
(932, '企業換機潮', '3483'),
(933, '企業換機潮', '3512'),
(934, '企業換機潮', '3231'),
(935, '企業換機潮', '3548'),
(936, '企業換機潮', '3211'),
(937, '企業換機潮', '3376'),
(938, '企業換機潮', '2356'),
(939, '企業換機潮', '2382'),
(940, '企業換機潮', '3526'),
(941, '企業換機潮', '3605'),
(942, '企業換機潮', '2324'),
(943, '企業換機潮', '2353'),
(944, '企業換機潮', '3694'),
(945, '物聯網', '6263'),
(946, '物聯網', '3094'),
(947, '物聯網', '4906'),
(948, '物聯網', '3062'),
(949, '物聯網', '6142'),
(950, '物聯網', '5388'),
(951, '物聯網', '6160'),
(952, '物聯網', '2357'),
(953, '物聯網', '2356'),
(954, '物聯網', '2377'),
(955, '物聯網', '2395'),
(956, '物聯網', '2412'),
(957, '物聯網', '3045'),
(958, '物聯網', '4904'),
(959, '物聯網', '3035'),
(960, '物聯網', '6411'),
(961, '物聯網', '6166'),
(962, '物聯網', '5272'),
(963, '物聯網', '5261'),
(964, 'ApplePay', '3048'),
(965, 'ApplePay', '8114'),
(966, 'ApplePay', '5490'),
(967, 'ApplePay', '2436'),
(968, 'ApplePay', '6285'),
(969, 'ApplePay', '6206'),
(970, 'ApplePay', '2454'),
(971, 'ApplePay', '2379'),
(972, 'ApplePay', '3529'),
(973, '電子商務', '1909'),
(974, '無線充電', '4952'),
(975, '無線充電', '6243'),
(976, '無線充電', '6138'),
(977, '無線充電', '6286'),
(978, '無線充電', '4919'),
(979, '無線充電', '6202'),
(980, '無線充電', '2431'),
(981, '無線充電', '8271'),
(982, '無線充電', '3260');


ALTER TABLE `grp`
 ADD UNIQUE KEY `id` (`id`);


ALTER TABLE `grp`
MODIFY `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=983;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
