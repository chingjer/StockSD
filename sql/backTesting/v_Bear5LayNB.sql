CREATE OR REPLACE VIEW v_Bear5LayNB AS
SELECT s.stockId, s.dte,s.price,s.ma5
FROM stk s 
WHERE s.price>10 
AND vol > 1000 
AND s. ma240 > s.ma120 and s.ma120 > s.ma60 
AND s. ma60 > s.ma20
AND (s.price-s.ma5)*100/s.ma5 BETWEEN -2 AND 3
AND s.sc_ma60 < 0 
AND s.sc_ma20 < 0 
AND s.ud200='D'
AND kdk < kdd and sc_kdk < 0 and sc_kdd < 0
AND rsi6 < rsi12
