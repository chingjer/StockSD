CREATE OR REPLACE VIEW v_basic2 AS
SELECT b.stockid, 
CONCAT('[',s.t5b ,'/',s.t5s,',',s.t20b,'/',s.t20s,']') as `法人idx` ,
t5,t20,
CONCAT('[',s.b5b ,'/',s.b5s,',',s.b20b,'/',s.b20s,']') as `投idx` ,
b5,b20,
CONCAT('[',s.a5b ,'/',s.a5s,',',s.a20b,'/',s.a20s,']') as `外idx` ,
a5,a20,
CONCAT('[',s.c5b ,'/',s.c5s,',',s.c20b,'/',s.c20s,']') as `自idx` ,
c5,c20
FROM stkbasic AS b INNER JOIN tppii_sum AS s ON b.stockid = s.stockid