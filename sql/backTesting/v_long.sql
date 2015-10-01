CREATE OR REPLACE VIEW v_long AS
select l.stockid,l.stkname,h.price as `高點`,h.dte as `高點日`,l.price as `低點`,l.dte as `低點日`,
round((l.price-h.price)*100/h.price,2) as `漲跌幅`
from tmp_high h inner join stkid l on h.stockid = l.stockid 
where h.price <> 0 and l.price <> 0 order by `漲跌幅`