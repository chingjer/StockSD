CREATE OR REPLACE VIEW v_squeeze AS
SELECT  s.stockId, i.stkname, s.dte,
Round(s.price*1.04,2) AS `��h`, Round(s.price*0.98,2) AS `���l`, 
s.price AS `����`, s.minbw_dte AS `�������`, Round(s.minbw,2) AS `�̤p�a�e`, s.sqdays AS `���Y�Ѽ�`, 
ROUND(s.updown,2) AS `���^%`, Round(s.percentb,2) AS `%b`, s.sc_osc, 
Round(s.MFI,2) AS mfi14, Round(s.ptbrsi12,2) AS `%b(s.rsi12)`, round(s.vol,0) AS `����q`, s.va5 AS `5�駡�q`
FROM squeeze s INNER JOIN stkid i ON s.stockId = i.stockid
WHERE s.dte=i.dte AND s.UPDOWN >=4;
