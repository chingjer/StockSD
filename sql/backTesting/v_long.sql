CREATE OR REPLACE VIEW v_long AS
select l.stockid,l.stkname,h.price as `���I`,h.dte as `���I��`,l.price as `�C�I`,l.dte as `�C�I��`,
round((l.price-h.price)*100/h.price,2) as `���^�T`
from tmp_high h inner join stkid l on h.stockid = l.stockid 
where h.price <> 0 and l.price <> 0 order by `���^�T`