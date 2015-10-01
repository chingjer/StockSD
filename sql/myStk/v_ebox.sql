CREATE OR REPLACE VIEW v_ebox AS
SELECT x.stockid, i.stkname, x.dte, ROUND(s.updown,2) AS `漲跌`, x.price AS `價格`, 
x.diff_bt AS `距高點幅度`, x.highprice AS `BT`, 
Round(x.highprice*1.05,2) AS `BT+5%`, Round(x.highprice*0.96,2) AS `停損`,
stkbasic.YRMAX AS `2年高`, Round(stkbasic.YRDOWN,1) AS `距2年高`,
x.highdte AS `BT日`, x.vol AS `成交量`, x.va5 AS `5日均量`, Round(s.ptbrsi6,1) AS `%b(rsi6)`,
x.udrate AS `BT前漲幅`, x.boxwidth AS `箱高`, x.hdays AS `距BT天`, x.ldays AS `距BB天`,
b.yr2, b.qt,b.grmonth
FROM BOX x INNER JOIN stkid i ON x.stockid = i.stockid LEFT JOIN stkbasic b ON i.stockid = b.stockid
INNER JOIN stk s ON x.stockid = s.stockId AND x.dte = s.dte 
INNER JOIN stkbasic ON i.stockid = stkbasic.stockid
WHERE x.dte = i.dte;

WHERE x.dte >= '2014-12-1'
WHERE x.dte = i.dte;