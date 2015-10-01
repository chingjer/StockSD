CREATE OR REPLACE VIEW v_Bear5Lay AS
SELECT s.stockId, i.stkname, s.dte,s.price, 
round(s.ma5 * 0.98,2) as `�i��`, 
round(s.ma5 * 1.02,2) as `�X��`,
Round((s.price-s.ma5)*100/s.ma5,2) AS `BIAS5%`, 
CEIL(vol) AS `����q`,
b.GRACC AS `�֭p�禬�����v`, CONCAT(GRYOY_1 , "/" , GRYOY_2 , "/" , GRYOY_3) AS `��3���禬�����v`,
CONCAT(QEG_1, "/" , QEG_2 , "/" , QEG_3 , "/" , QEG_4 , "/" , QEG_5 , "/" , QEG_6 , "/" , QEG_7 , "/" , QEG_8) AS `��8�uEPS�����v`,
b.yr2, b.qt,b.grmonth
FROM stk s INNER JOIN stkid i ON s.stockId = i.stockid LEFT JOIN stkbasic b ON i.stockid = b.stockid
WHERE s.price BETWEEN 10 AND 60 
AND vol > 1000 
AND s. ma240 > s.ma120 and s.ma120 > s.ma60 
AND s. ma60 > s.ma20
AND (s.price-s.ma5)*100/s.ma5 BETWEEN -2 AND 3
AND s.sc_ma60 < 0 
AND s.sc_ma20 < 0 
AND s.ud200='D'
AND kdk < kdd and sc_kdk < 0 and sc_kdd < 0
AND rsi6 < rsi12
AND GRACC < 0
AND QEG_1 < 0
AND s.dte=i.dte;
