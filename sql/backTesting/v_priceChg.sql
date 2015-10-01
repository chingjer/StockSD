CREATE OR REPLACE VIEW v_priceChg AS
select h.price as `高點`,l.price as `低點`,
round((l.price-h.price)*100/h.price,2) as `漲跌幅`
from tmp_high h inner join tmp_low l on h.stockid = l.stockid
where h.price <> 0 and l.price <> 0
