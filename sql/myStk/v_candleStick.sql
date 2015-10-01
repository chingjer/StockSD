CREATE OR REPLACE VIEW v_candle AS
SELECT c.stockid, i.stkname, c.dte, s.price, c.ktype, 
ceil(s.vol) as `成交量` 
FROM candlestick c 
INNER JOIN stkid i ON c.stockid = i.stockid 
INNER JOIN stk s ON s.stockid = i.stockid and s.dte = i.dte 
where c.dte = i.dte and 
c.ktype in ("+單白兵","-單黑鴉","+槌子","-吊人","+晨星","+晨星十字","-夜星","+多頭母子","+多頭母子十字","-空頭母子","-空頭吞噬","+貫穿線","-烏雲罩頂")
