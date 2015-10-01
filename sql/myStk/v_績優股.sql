CREATE OR REPLACE VIEW v_�Z�u�� AS
SELECT s.stockId, i.stkname, s.dte,s.price, 
CEIL(vol) AS `����q`,round(s.updown,1) as `���T`,
b.GRACC AS `�֭p�禬�����v`, CONCAT(GRYOY_1 , "/" , GRYOY_2 , "/" , GRYOY_3) AS `��3���禬�����v`,
CONCAT('[',t.t5b ,'/',t.t5s,',',t.t20b,'/',t.t20s,']') as `�T�j�k�H�R��O`,
CONCAT(QEG_1, "/" , QEG_2 , "/" , QEG_3 , "/" , QEG_4 , "/" , QEG_5 , "/" , QEG_6 , "/" , QEG_7 , "/" , QEG_8) AS `��8�uEPS�����v`
FROM stk s INNER JOIN stkid i ON s.stockId = i.stockid LEFT JOIN stkbasic b ON i.stockid = b.stockid
inner join tppii_sum t ON t.stockId = i.stockid
WHERE b.qt = "104.1Q" AND b.grmonth = "104/05"
AND VOL > 500
AND QEG_1 > 25 AND QEG_2 > 25 AND GRACC > 30
AND UD200="U" AND sc_ma60 > 0
AND s.dte=i.dte
ORDER BY s.price;
