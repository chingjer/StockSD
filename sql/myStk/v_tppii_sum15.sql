CREATE OR REPLACE VIEW v_tppii_sum15 AS
SELECT t.stockid, i.stkname,i.price as `收盤`,
t20b as `20日買超天數`,
t20s as `20日賣超天數`,
t5b as `5日買超天數`,
t5s as `5日賣超天數`,
t5 as `5日買超`,
t20 as `20日買超`
FROM `tppii_sum` t inner join stkid i on t.stockid = i.stockid WHERE t20b >= 15