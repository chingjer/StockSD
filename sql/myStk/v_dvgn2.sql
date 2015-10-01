CREATE OR REPLACE VIEW v_dvgn2 AS
SELECT d.stockid AS `�N��`, i.stkname AS `�W��`,  d.dte AS `���`,
"�}�L" AS `���I`,
Round(d.p_stop,2) AS `���l��`,
round(s.percentb,2) AS `%b` ,
CONCAT(round(s.MFI,2),"(", s.sc_mfi, ")") AS `MFI`, round(s.vol,0) AS `����q`,
CONCAT(Round(s.KDK,1) , "(" , s.sc_kdk , ")") AS `KD_K`, 
CONCAT(Round(s.KDD,1) , "(" , s.sc_kdd , ")") AS `KD_D`
FROM divergence d inner join stkid i ON d.stockid = i.stockId and d.dte = i.dte 
INNER JOIN stk s ON d.stockid = s.stockId and d.dte = s.dte 
WHERE s.percentb > 0.7
AND s.rsi6 < s.rsi12

