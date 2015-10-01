CREATE OR REPLACE VIEW v_newhigh AS
SELECT b.stockid, i.stkname, i.dte, i.price, Round(YRMAX,2) AS `近年高點`, b.YRMAX_DTE AS `新高日`, 
Round(YRMIN,2) AS `低點`, b.CHGSCOPE AS `範圍(%)`, Round(YRDOWN,1) AS `距離高點`, 
CONCAT(QEG_1, "/" , QEG_2 , "/" , QEG_3 , "/" , QEG_4 , "/" , QEG_5 , "/" , QEG_6 , "/" , QEG_7 , "/" , QEG_8) AS `近8季EPS成長率`,
If(eps_2=0,0,Round((eps_1-eps_2)*100/Abs(eps_2),2)) AS `近1年EPS成長率`,
b.GRACC AS `累計營收年增率`, 
CONCAT(GRYOY_1 , "/" , GRYOY_2 , "/" , GRYOY_3) AS `近3月營收成長率`,
b.yr2, b.qt,b.grmonth
FROM stkbasic AS b INNER JOIN stkid AS i ON b.stockid = i.stockid
WHERE i.price>5 AND YRDOWN Between -10 And 10
AND b.GRACC>10 AND b.QEG_1>15 AND b.QEG_2>15  AND b.PER<60
ORDER BY b.QEG_1 DESC;
