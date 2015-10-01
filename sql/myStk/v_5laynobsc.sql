CREATE OR REPLACE VIEW v_5laynobsc AS 
select s.stockid AS stockId,i.stkname AS stkname,s.dte AS dte,
s.price AS price,
round(s.ma5 * 0.98,2) AS `進場`,
round(s.ma5 * 1.02,2) AS `出場`,
Round((s.price-s.ma5)*100/s.ma5,2) AS `BIAS5%`,
ceiling(s.vol) AS `成交量` 
from stk s inner join stkid i on s.stockid = i.stockid 
WHERE s.price between 10 and 60 
AND s.vol > 1000 
AND s. ma240 > s.ma120 and s.ma120 > s.ma60 
AND s. ma60 > s.ma20
AND (s.price-s.ma5)*100/s.ma5 BETWEEN -2 AND 3
AND s.sc_ma60 < 0 
AND s.sc_ma20 < 0 
AND s.ud200='D'
AND s.kdk < s.kdd and s.sc_kdk < 0 and s.sc_kdd < 0 and s.rsi6 < s.rsi12
and s.dte = i.dte;

