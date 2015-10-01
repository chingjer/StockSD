
import java.sql.*;

/**
 * ******************************************************************
 * 計算 KD9
********************************************************************
 */
public final class CalcMacd {

    Boolean isAll; // true-全部資料重新產生，false-只針對今天的資料
    StkDb oStk;
    Statement stmt;

    CalcMacd() throws SQLException {
        isAll = false;
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
    }

    void calc_all_macd()  throws SQLException  {
        calc_macd1();
    }

    Double getPt(ResultSet rs) throws SQLException {
        Double p_high, p_low, price, pt;
        p_high = rs.getDouble("p_high");
        p_low = rs.getDouble("p_low");
        price = rs.getDouble("price");
        pt = (p_high + p_low + price * 2) / 4;
        return pt;
    }

    @SuppressWarnings("UnusedAssignment")
    void calc_macd1() throws SQLException {
        ResultSet rs;
        int i, idxLast = 0, days;
        String tmpId, sWhere, sFromDate, sql;
        Boolean noEnoughRecords = true, isNullPrev;
        java.util.Date dCurrDate; // 資料庫中的最後一天
        Double dec = 0d;

        int nShort, nLong, nMacd; //--MACD(12,26,9)
        Double ent12_1 = 0d, ent12 = 0d, alpha12 = 0d;
        Double ent26_1 = 0d, ent26 = 0d, alpha26 = 0d;
        Double macd9_1 = 0d, macd9 = 0d, alpha9 = 0d;
        int idxMacd = 0;
        Double totMacd = 0d, Dif = 0d;
        Double prevEnt12 = 0d, prevEnt26 = 0d, pmacd = 0d;
        Double p_high, p_low, price, pt;

        nShort = 12;
        nLong = 26;
        nMacd = 9;
        alpha12 = 2.0d / (nShort + 1);
        alpha26 = 2.0d / (nLong + 1);
        alpha9 = 2.0d / (nMacd + 1);
        tmpId = "";
        dCurrDate = oStk.strToDate(oStk.currDate);
        sFromDate = oStk.getPrevStockDate(dCurrDate, nLong + 1 + oStk.tourdays);
        if (isAll) {
            sWhere = "";
        } else {
            sWhere = String.format(" WHERE dte > %s and dte <= %s",
                    oStk.padCh(sFromDate), oStk.padCh(oStk.currDate));
        }
        sql = "select stockid,dte,p_high,p_low,price,macd,ent12,ent26,dif,osc "
                + " from stk " + sWhere + " ORDER BY stockid,dte";
        stmt = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            if (!tmpId.equals(rs.getString("stockid"))) {
                idxMacd = 0;
                ent12_1 = 0d;
                ent26_1 = 0d;
                totMacd = 0d;
                tmpId = rs.getString("stockid");
                noEnoughRecords = false;
                for (i = 0; i <= nShort + 1; i++) {
                    pt = getPt(rs);
                    ent26_1 += pt;
                    rs.next();
                    if (rs.isAfterLast() || !(rs.getString("stockid")).equals(tmpId)) {
                        noEnoughRecords = true;
                        break;
                    }
                } // for
                if (!noEnoughRecords) {
                    isNullPrev = false;
                    for (i = nShort + 2; i <= nLong - 1; i++) {
                        pt = getPt(rs);
                        ent26_1 += pt;
                        ent12_1 += pt;

                        prevEnt12 = rs.getDouble("ent12");
                        prevEnt26 = rs.getDouble("ent26");
                        pmacd = rs.getDouble("macd");
                        rs.next();
                        if (rs.isAfterLast() || !(rs.getString("stockid")).equals(tmpId)) {
                            noEnoughRecords = true;
                            break;
                        }
                    } // for
                    if (!noEnoughRecords) {
                        if (isAll || prevEnt12 == 0d) {
                            ent12_1 = ent12_1 / nShort;
                            ent26_1 = ent26_1 / nLong;
                        } else {
                            ent12_1 = prevEnt12;
                            ent26_1 = prevEnt26;
                            macd9_1 = pmacd;
                        }
                    } // if
                } // if
            } //if (!tmpId.equals

            if (!noEnoughRecords) {
                pt = getPt(rs);
                ent12 = ent12_1 + alpha12 * (pt - ent12_1);
                ent26 = ent26_1 + alpha26 * (pt - ent26_1);
                Dif = ent12 - ent26;
                if (isAll) {
                    if (idxMacd < nMacd) {
                        totMacd = totMacd + Dif;
                        macd9_1 = 0d;
                    } else if (idxMacd == nMacd) {
                        macd9_1 = totMacd / nMacd;
                    }
                    idxMacd = idxMacd + 1;
                }
                macd9 = macd9_1 + alpha9 * (Dif - macd9_1);
                dec = rs.getDouble("ent12");
                if (isAll || dec == 0d) {
                    rs.updateDouble("dif", Dif);
                    rs.updateDouble("macd", macd9);
                    rs.updateDouble("osc", Dif - macd9);
                    rs.updateDouble("ent12", ent12);
                    rs.updateDouble("ent26", ent26);
                    rs.updateRow();
                    macd9_1 = macd9;
                    ent12_1 = ent12;
                    ent26_1 = ent26;
                } else {
                    macd9_1 = rs.getDouble("macd");
                    ent12_1 = rs.getDouble("ent12");
                    ent26_1 = rs.getDouble("ent26");
                }
            } //if (!noEnoughRecords)
        } // while
        rs.close();
        System.out.println("Calc MACD Done!");
    }

    /**
     * 執行方式: java CalcMacd [isAll]
     *
     * @param args [] Y:產生全部，注意：將非常耗時
     */
    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** CalcMacd() *****");
        try {
            CalcMacd oo = new CalcMacd();
            if (args.length > 0 && args[0].equals("Y")) {
                oo.isAll = true;
            }
            oo.calc_all_macd();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.err.println("================================");
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    } //main

}
