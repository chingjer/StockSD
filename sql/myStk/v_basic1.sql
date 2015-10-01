CREATE OR REPLACE VIEW v_basic1 AS
SELECT b.stockid, i.stkname AS `名稱`, i.dte as `最近日期`, i.price as `價格`,
CONCAT(QEG_1, "/" , QEG_2 , "/" , QEG_3 , "/" , QEG_4 , "/" , QEG_5 , "/" , QEG_6 , "/" , QEG_7 , "/" , QEG_8) AS `近8季EPS成長率`,
If(eps_2=0,0,Round((eps_1-eps_2)*100/Abs(eps_2),2)) AS `近1年EPS成長率`,
b.GRACC AS `累計營收年增率`, 
CONCAT(GRYOY_1 , "/" , GRYOY_2 , "/" , GRYOY_3) AS `近3月營收成長率`
FROM stkbasic AS b INNER JOIN stkid AS i ON b.stockid = i.stockid