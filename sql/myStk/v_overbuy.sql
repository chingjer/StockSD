CREATE OR REPLACE VIEW v_overbuy AS
SELECT s.stockId, i.stkname, s.dte,s.price, 
CEIL(vol) AS `成交量`,t.t5 AS `法人5日買超`,round(s.updown,1) as `漲幅`,
b.GRACC AS `累計營收成長率`, CONCAT(GRYOY_1 , "/" , GRYOY_2 , "/" , GRYOY_3) AS `近3月營收成長率`,
CONCAT('[',t.t5b ,'/',t.t5s,',',t.t20b,'/',t.t20s,']') as `買賣力指標`,
CONCAT(QEG_1, "/" , QEG_2 , "/" , QEG_3 , "/" , QEG_4 , "/" , QEG_5 , "/" , QEG_6 , "/" , QEG_7 , "/" , QEG_8) AS `近8季EPS成長率`,
b.yr2,b.qt,b.grmonth
FROM stk s INNER JOIN stkid i ON s.stockId = i.stockid LEFT JOIN stkbasic b ON i.stockid = b.stockid
inner join tppii_sum t ON t.stockId = i.stockid
WHERE t.t5b >= 3 and (t.t20b/(t.t20s+0.01)) >= 3
AND VOL > 1000
AND s.dte=i.dte;
