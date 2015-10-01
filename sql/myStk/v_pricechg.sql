CREATE OR REPLACE VIEW v_pricechg AS
SELECT g.CL1,round(Avg(updown5),2) AS `5�麦�T(%)`,
round(Avg(updown20),2) AS `20�麦�T(%)`,round(Avg(updown60),2) AS `60�麦�T(%)`
FROM pricechg c INNER JOIN grp g ON c.stockId=g.stockid 
GROUP BY g.CL1 


