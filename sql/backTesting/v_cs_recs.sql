CREATE OR REPLACE VIEW v_cs_recs AS
SELECT candleStick.dte, candleStick.ktype, Count(*) AS cnt
FROM candleStick
GROUP BY candleStick.dte, candleStick.ktype;
