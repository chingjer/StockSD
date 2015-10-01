CREATE OR REPLACE VIEW v_bbrev AS
select s.stockid, i.stkname as `股名`,s.dte,s.price as `收盤`, round(s.price * 0.97,2) as `停損`,
round(s.percentb,2) as `%b`,round(s.rsi6,2) as `RSI6`, round(s.rsi12,2) as `RSI12`, 
concat(round(s.kdk,2),'(',s.sc_kdk,')',round(s.kdd,2),'(',s.sc_kdd,')') as 'KD9' 
from stk s inner join stkid i on s.stockid = i.stockid and s.dte = i.dte 
where s.percentb < 0.1 and s.ma20 > 5 and s.vol > 300 and s.rsi6 > s.rsi12