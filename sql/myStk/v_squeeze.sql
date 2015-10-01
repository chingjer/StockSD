CREATE OR REPLACE VIEW v_squeeze AS
SELECT  s.stockId, i.stkname, s.dte,
Round(s.price*1.04,2) AS `轉多`, Round(s.price*0.98,2) AS `停損`, 
s.price AS `價格`, s.minbw_dte AS `擠壓日期`, Round(s.minbw,2) AS `最小帶寬`, s.sqdays AS `壓縮天數`, 
ROUND(s.updown,2) AS `漲跌%`, Round(s.percentb,2) AS `%b`, s.sc_osc, 
Round(s.MFI,2) AS mfi14, Round(s.ptbrsi12,2) AS `%b(s.rsi12)`, round(s.vol,0) AS `成交量`, s.va5 AS `5日均量`
FROM squeeze s INNER JOIN stkid i ON s.stockId = i.stockid
WHERE s.dte=i.dte AND s.UPDOWN >=4;
