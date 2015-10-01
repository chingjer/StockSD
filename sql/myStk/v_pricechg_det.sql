CREATE OR REPLACE VIEW v_pricechg_det AS
select g.cl1, p.stockid, i.stkname, p.updown5, p.updown20,p.updown60 from grp g inner join pricechg p inner join stkid i 
on p.stockid = g.stockid and p.stockid = i.stockid 