CREATE OR REPLACE VIEW v_BearCandidate AS
SELECT s.stockId, i.stkname, s.dte,s.price, round(s.p_low * 0.98,2) as `進場`, 
round(s.p_low * 1.01,2) as `出場`,
Round(ma200,2) AS `200日線`,
Round((s.price-s.ma200)*100/s.ma200,2) AS `乖離率%`, 
CONCAT(CEIL(vol),IF(VOL>VA10*4,'＊','') ) AS `成交量`, 
b.GRACC AS `累計營收成長率`, CONCAT(GRYOY_1 , "/" , GRYOY_2 , "/" , GRYOY_3) AS `近3月營收成長率`,
CONCAT(QEG_1, "/" , QEG_2 , "/" , QEG_3 , "/" , QEG_4 , "/" , QEG_5 , "/" , QEG_6 , "/" , QEG_7 , "/" , QEG_8) AS `近8季EPS成長率`,
b.yr2, b.qt,b.grmonth
FROM stk s INNER JOIN stkid i ON s.stockId = i.stockid LEFT JOIN stkbasic b ON i.stockid = b.stockid
WHERE s.price>10 AND s.ma200 > s.price 
AND vol > 2000 
AND s.sc_ma60 < 0 
AND s.sc_ma20 < 0 
AND s.ud200='D'
AND s.vol 
AND s.dte=i.dte;
