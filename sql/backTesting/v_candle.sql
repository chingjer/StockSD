CREATE OR REPLACE VIEW v_candle AS
SELECT candlestick.stockid, candlestick.dte, candlestick.ktype, 
stk.p_open, stk.p_high, stk.p_low, stk.price, stk.KDD, stk.ptbmfi, stk.ptbrsi6, stk.percentb, stk.bandwidth,
stk.vol,stk.va10,stk.mfi, stk.rsi6, stk.rsi12
FROM candlestick INNER JOIN stk ON (candlestick.dte = stk.dte) AND (candlestick.stockid = stk.stockId);
