CREATE OR REPLACE VIEW v_lvstg_stat AS
SELECT s.paranum AS `參數`, CEIL(Avg(sel_recs)) AS `平均總筆數` ,
CEIL(Avg(trade_recs)) AS `平均交易筆數`, Round(Avg(exp),2) AS `平均期望值`,
Round(Avg(avgdays),2) AS `持股天數`, Round(Avg(exp_quater),2) AS `平均季期望值`,
Round(Avg(net_profit),2) AS `平均總獲利`, Round(Avg(exp_R),2) AS `平均R期望值`, 
Avg(s.winning) AS `平均勝率`, s.datecode
FROM lvstg_stat s
GROUP BY s.paranum, s.datecode
ORDER BY s.datecode, s.paranum;

