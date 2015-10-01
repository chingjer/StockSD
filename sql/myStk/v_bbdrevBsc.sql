CREATE OR REPLACE VIEW v_bbdrev AS
select s.stockid, i.stkname as `股名`,s.dte, s.price as `收盤` ,round(s.percentb,2) as `%b`,
round(s.price * 1.03,2) as `停損`,
round(s.rsi6,2) as `RSI6`, round(s.rsi12,2) as `RSI12`, 
concat(round(s.kdk,2),'(',s.sc_kdk,')',round(s.kdd,2),'(',s.sc_kdd,')') as 'KD9',
CONCAT(QEG_1, "/" , QEG_2 , "/" , QEG_3 , "/" , QEG_4 , "/" , QEG_5 , "/" , QEG_6 , "/" , QEG_7 , "/" , QEG_8) AS `近8季EPS成長率`,
If(eps_2=0,0,Round((eps_1-eps_2)*100/Abs(eps_2),2)) AS `近1年EPS成長率`,
b.GRACC AS `累計營收年增率`, 
CONCAT(GRYOY_1 , "/" , GRYOY_2 , "/" , GRYOY_3) AS `近3月營收成長率`, i.price as `近日收盤`
from stk s inner join stkid i on s.stockid = i.stockid 
inner join stkbasic b on s.stockid = b.stockid  
where s.percentb > 0.9 and s.ma20 > 5 and s.vol > 300 and s.rsi6 < s.rsi12