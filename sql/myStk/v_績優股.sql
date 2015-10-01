CREATE OR REPLACE VIEW v_績優股 AS
SELECT s.stockId, i.stkname, s.dte,s.price, 
CEIL(vol) AS `成交量`,round(s.updown,1) as `漲幅`,
b.GRACC AS `累計營收成長率`, CONCAT(GRYOY_1 , "/" , GRYOY_2 , "/" , GRYOY_3) AS `近3月營收成長率`,
CONCAT('[',t.t5b ,'/',t.t5s,',',t.t20b,'/',t.t20s,']') as `三大法人買賣力`,
CONCAT(QEG_1, "/" , QEG_2 , "/" , QEG_3 , "/" , QEG_4 , "/" , QEG_5 , "/" , QEG_6 , "/" , QEG_7 , "/" , QEG_8) AS `近8季EPS成長率`
FROM stk s INNER JOIN stkid i ON s.stockId = i.stockid LEFT JOIN stkbasic b ON i.stockid = b.stockid
inner join tppii_sum t ON t.stockId = i.stockid
WHERE b.qt = "104.1Q" AND b.grmonth = "104/05"
AND VOL > 500
AND QEG_1 > 25 AND QEG_2 > 25 AND GRACC > 30
AND UD200="U" AND sc_ma60 > 0
AND s.dte=i.dte
ORDER BY s.price;
