CREATE OR REPLACE VIEW v_strong AS
SELECT s.stockid AS `代號`, i.stkname AS `名稱`,  s.dte AS `日期`,
Round(s.price*1.03,2) AS `轉多價`,
s.price AS `停損`, 
round(vol,0) AS `成交量`,round(va5,0) AS `5日均量`, 
CONCAT(Round(s.KDK,1) , "(" , s.sc_kdk , ")") AS `KD_K`, 
CONCAT(Round(s.KDD,1) , "(" , s.sc_kdd , ")") AS `KD_D`,
CONCAT(round(s.MFI,2),"(", s.sc_mfi, ")") AS `MFI`, 
s.sc_osc AS `sc_OSC`,
round(s.ptbrsi6,2) AS `%b(rsi6)` ,
round(s.percentb,2) AS `%b` 
FROM stkid i INNER JOIN stk s ON i.stockid = s.stockId 
WHERE s.price>5 
AND s.updown >= 6.5 
AND s.vol > s.va5*2 
AND s.KDK>75 
AND s.sc_kdk>0 
AND s.sc_kdd>0 
AND s.KDD< s.kdk  
AND s.sc_osc > 3 
AND s.MFI>= 80 
AND s.vol > 500 
AND s.ptbrsi6 >= 1 
AND s.dte = i.dte;

