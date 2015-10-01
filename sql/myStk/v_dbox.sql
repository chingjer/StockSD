CREATE OR REPLACE VIEW v_dbox AS
SELECT d.stockid, i.stkname, d.dte,Round(low2price*0.98,2) AS `空點`, 
d.low2price AS `箱底`, d.boxwidth AS `高度`, d.diff_bb AS `距箱底`, 
d.hdays AS `距BT天`, d.ldays AS `距BB天`, d.va5 AS `5日均量`, 
b.GRACC AS `累計營收成長率`, CONCAT(GRYOY_1 , "/" , GRYOY_2 , "/" , GRYOY_3) AS `近3月營收成長率`,
CONCAT(QEG_1, "/" , QEG_2 , "/" , QEG_3 , "/" , QEG_4 , "/" , QEG_5 , "/" , QEG_6 , "/" , QEG_7 , "/" , QEG_8) AS `近8季EPS成長率`,
b.yr2, b.qt,b.grmonth
FROM dBoxdata d INNER JOIN stkid i ON d.stockid = i.stockid LEFT JOIN stkbasic b ON i.stockid = b.stockid
WHERE d.va5>500 AND d.dte=i.dte ORDER BY d.stockid;
