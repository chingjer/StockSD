CREATE OR REPLACE VIEW v_ebox AS
SELECT x.stockid, i.stkname, x.dte, ROUND(s.updown,2) AS `���^`, x.price AS `����`, 
x.diff_bt AS `�Z���I�T��`, x.highprice AS `BT`, 
Round(x.highprice*1.05,2) AS `BT+5%`, Round(x.highprice*0.96,2) AS `���l`,
stkbasic.YRMAX AS `2�~��`, Round(stkbasic.YRDOWN,1) AS `�Z2�~��`,
x.highdte AS `BT��`, x.vol AS `����q`, x.va5 AS `5�駡�q`, Round(s.ptbrsi6,1) AS `%b(rsi6)`,
x.udrate AS `BT�e���T`, x.boxwidth AS `�c��`, x.hdays AS `�ZBT��`, x.ldays AS `�ZBB��`,
b.yr2, b.qt,b.grmonth
FROM BOX x INNER JOIN stkid i ON x.stockid = i.stockid LEFT JOIN stkbasic b ON i.stockid = b.stockid
INNER JOIN stk s ON x.stockid = s.stockId AND x.dte = s.dte 
INNER JOIN stkbasic ON i.stockid = stkbasic.stockid
WHERE x.dte = i.dte;

WHERE x.dte >= '2014-12-1'
WHERE x.dte = i.dte;