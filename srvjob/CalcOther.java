
import java.sql.*;

public final class CalcOther {

    Boolean isAll; // true-全部資料重新產生，false-只針對今天的資料
    StkDb oStk;
    Statement stmt;

    CalcOther() throws SQLException {
        isAll = false;
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
    }

    /**
     * 計算漲跌幅與ma200的方向
     *
     * @throws SQLException
     */
    void calc_updown() throws SQLException {
        ResultSet rs;
        String tmpId, sWhere, sFromDate, sql, Trend, TrendVal;
        double LastPrice, UpDown, CurrPrice, LastMA200;
        java.util.Date dCurrDate;
        boolean isUpd;

        tmpId = "";
        LastPrice = 0d;
        LastMA200 = 0d;
        dCurrDate = oStk.strToDate(oStk.currDate);
        sFromDate = oStk.getPrevStockDate(dCurrDate, 2 + oStk.tourdays);
        if (isAll) {
            sWhere = "";
        } else {
            sWhere = String.format(" WHERE dte > %s and dte <= %s",
                    oStk.padCh(sFromDate), oStk.padCh(oStk.currDate));
        }
        sql = "select stockid,dte,price,updown,ma200,ud200 "
                + " from stk " + sWhere + " ORDER BY stockid,dte";
        stmt = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            if (!tmpId.equals(rs.getString("stockid"))) {
                tmpId = rs.getString("stockid");
                LastPrice = 0d;
                LastMA200 = 0d;
            } // if
            if (LastPrice != 0d) {
                isUpd = false;
                CurrPrice = rs.getDouble("price");
                UpDown = (CurrPrice - LastPrice) / LastPrice * 100;
                if (isAll || UpDown != rs.getDouble("updown")) {
                    rs.updateDouble("updown", UpDown);
                    isUpd = true;
                }
                if (rs.getDouble("MA200") > LastMA200) {
                    Trend = "U";
                } else {
                    Trend = "D";
                }
                TrendVal = rs.getString("UD200");
                if (isAll || rs.wasNull() || !TrendVal.equals(Trend)) {
                    rs.updateString("ud200", Trend);
                    isUpd = true;
                }
                if (isUpd) {
                    rs.updateRow();
                }
            }
            LastPrice = rs.getDouble("price");
            LastMA200 = rs.getDouble("MA200");
        } // while
        rs.close();
        System.out.println("Calc upDown,ud200 Done!");

    }

    /**
     * 計算5,20,60的漲跌幅，以作為強弱勢類股的統計
     *
     * @throws SQLException
     */
    void calc_pricechg() throws SQLException {

        ResultSet rs;
        Statement stmt;
        String lastId, sql, currId;
        double nn[] = new double[4]; // nn[0]是今日收盤，nn[1]是5日漲幅，nn[2]20日,nn[3]60日
        double nTmp;
        java.util.Date dCurr;
        String sDte[] = new String[4];
        boolean isIns;
        int i, iDays;

        lastId = "";
        sDte[0] = oStk.currDate;
        dCurr = oStk.strToDate(sDte[0]);
        sDte[1] = oStk.getPrevStockDate(dCurr, 6);
        sDte[2] = oStk.getPrevStockDate(dCurr, 21);
        sDte[3] = oStk.getPrevStockDate(dCurr, 61);
        for (i = 0; i < nn.length; i++) {
            nn[i] = -100d;
        }
        iDays = 0;
        sql = String.format("select stockid,dte,price from stk where dte in (%s,%s,%s,%s) "
                + "order by stockid,dte DESC", oStk.padCh(sDte[0]), oStk.padCh(sDte[1]),
                oStk.padCh(sDte[2]), oStk.padCh(sDte[3]));
        
        System.out.println(sql);

        stmt = oStk.conn.createStatement();
        rs = stmt.executeQuery(sql);
        lastId = "XXXX";
        while (rs.next()) {
            currId = rs.getString("stockid");
            if (!lastId.equals(currId)) {
                if (!"XXXX".equals(lastId)) {
                    if (iDays == 4) {
                        doUpdatePriceChg(lastId, nn);
                    }
                }
                lastId = currId;
                for (i = 0; i < nn.length; i++) {
                    nn[i] = -100d;
                }
                iDays = 0;
            }
            nTmp = rs.getDouble("price");
            if (currId.equals("5490")){
                System.out.printf("\ndate=%s,price=%.2f,",rs.getDate("dte"),nTmp);                
            }
            if (iDays == 0) {
                nn[0] = nTmp;
            } else {
                if (nn[0] > 0 && nTmp > 0) {
                    nn[iDays] = (nn[0]-nTmp) / nTmp;
                }
                if (currId.equals("5490")){
                    System.out.printf("chg=%.2f",nn[iDays]*100);                
                }
            }

            iDays += 1;
        }//while
        if (iDays == 4) {
            doUpdatePriceChg(lastId, nn);
        }
        rs.close();
        /* delete dte is not same oStk.currDate
         可能是該股已經下市等等
         */
        sql = String.format("delete from pricechg where not (dte = %s)", oStk.padCh(oStk.currDate));
        stmt.executeUpdate(sql);

        System.out.println("Calc prichchg Done!");

    }

    void doUpdatePriceChg(String lastId, double nn[]) throws SQLException {
        ResultSet rs;
        Statement stmtUpdate;
        boolean isInsert = false;
        double chg;
        int ix;

        if (nn[0] <= 0) {//currdate price
            return;
        }
        for (ix = 1; ix < nn.length; ix++) {// price Change
            if (nn[ix] == -100d) {
                return;
            }
        }
        stmtUpdate = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        String sql;
        sql = String.format("select * from pricechg where stockid='%s'", lastId);
        rs = stmtUpdate.executeQuery(sql);
        if (!rs.next()) {
            isInsert = true;
            rs.moveToInsertRow();
            rs.updateString("stockid", lastId);
        }
        rs.updateDate("dte", Date.valueOf(oStk.currDate));
        for (ix = 1; ix < nn.length; ix++) {
            nn[ix] = Double.parseDouble(String.format("%.2f", nn[ix] * 100));
            rs.updateDouble(ix + 2, nn[ix]);
        }
        if (isInsert) {
            rs.insertRow();
        } else {
            rs.updateRow();
        }

    }

    /**
     * 計算各種均線與指標的方向<br>
     * +1...+10表示連續向上天數，最大為10<br>
     * -1...-10表示連續向上天數，最大為-10<br>
     * 如果與昨天價格相同則為0
     *
     * @throws SQLException
     */
    void calc_sc() throws SQLException {
        calc_sc1("ma10");
        calc_sc1("ma60");
        calc_sc1("ma20");
        calc_sc1("ma120");
        calc_sc1("osc");
        calc_sc1("mfi");
        calc_sc1("kdk");
        calc_sc1("kdd");
    }

    @SuppressWarnings("UnusedAssignment")
    void calc_sc1(String Fld) throws SQLException {
        ResultSet rs;
        int i, idxLast = 0;
        double tot = 0d, ma = 0d;
        String tmpId, scFld, sWhere, sFromDate, sql;
        boolean noEnoughRecords = true, isSkip;
        java.util.Date dCurrDate; // 資料庫中的最後一天
        double prevVal = 0d, currVal = 0d;
        int Sc = 0, prevSc = 0;

        scFld = "sc_" + Fld;
        tmpId = "";
        dCurrDate = oStk.strToDate(oStk.currDate);
        sFromDate = oStk.getPrevStockDate(dCurrDate, 2 + oStk.tourdays);
        if (isAll) {
            sWhere = "";
        } else {
            sWhere = String.format(" WHERE dte > %s and dte <= %s",
                    oStk.padCh(sFromDate), oStk.padCh(oStk.currDate));
        }
        sql = "select stockid,dte," + Fld + "," + scFld
                + " from stk " + sWhere + " ORDER BY stockid,dte";
        stmt = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            if (!tmpId.equals(rs.getString("stockid"))) {
                Sc = rs.getInt(scFld);
                tmpId = rs.getString("stockid");
                prevVal = rs.getDouble(Fld);
            } // if
            else {

                currVal = rs.getDouble(Fld);
                if (currVal > prevVal) {
                    if (Sc < 0) {
                        Sc = 1;
                    } else if (Sc < 10) // 最多10
                    {
                        Sc = Sc + 1;
                    }
                } else if (currVal < prevVal) {
                    if (Sc > 0) {
                        Sc = -1;
                    } else if (Sc > -10) // 最小-10
                    {
                        Sc = Sc - 1;
                    }
                } else if (currVal == prevVal) {
                    Sc = 0;
                }

            }
            if (isAll || Sc != rs.getInt(scFld)) {
                rs.updateInt(scFld, Sc);
                rs.updateRow();
            }
            prevVal = rs.getDouble(Fld);
        } // while
        rs.close();
        System.out.println("Calc " + scFld + " Done!");
    } //calc_sc1

    /**
     * 如果本日是180天內的最小帶寬，將日期與帶寬紀錄在stkid
     *
     * @throws SQLException
     */
    void upd_minbw() throws SQLException {
        ResultSet rs;
        String sFromDate, sql, sql2;
        java.util.Date dCurrDate;

        dCurrDate = oStk.strToDate(oStk.currDate);
        sFromDate = oStk.getPrevStockDate(dCurrDate, 181);
        sql2 = "SELECT stockid, Min(bandwidth) AS MIN_WIDTH FROM stk "
                + "WHERE dte > '" + sFromDate + "' GROUP BY stockid";
        sql = "SELECT a.stockId, a.dte, a.bandwidth "
                + "FROM (" + sql2 + ") m INNER JOIN stk a ON (m.stockid = a.stockId) "
                + "AND (m.MIN_WIDTH = a.bandwidth) where a.dte = "
                + oStk.padCh(oStk.currDate);
        stmt = oStk.conn.createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            sql = "update stkid set minbw_dte = " + oStk.dateToStrCh(rs.getDate("dte"))
                    + ", minbw=" + rs.getDouble("bandwidth")
                    + " where stockid='" + rs.getString("stockid") + "'";
            if (oStk.stmt.executeUpdate(sql) == 0) {
                System.out.println(sql + " *** ERROR ***");
            }
        } // while
        rs.close();
        System.out.println("upd_minbw() Done!");
    } //upd_minbw()

    /**
     * 清除stkid內過期的最小帶寬資訊,當本日帶寬大於最小帶寬3倍時就清除之
     *
     * @throws SQLException
     */
    void upd_stkid_minbw() throws SQLException {
        ResultSet rs;
        String sFromDate, sql, sql2;
        java.util.Date dCurrDate;

        dCurrDate = oStk.strToDate(oStk.currDate);
        sql = "SELECT stkid.stockid, stkid.stkname, stkid.minbw_dte, stkid.minbw, stk.bandwidth "
                + "FROM stkid INNER JOIN stk ON stkid.stockid = stk.stockId "
                + "WHERE stkid.minbw_dte is not null "
                + "and stk.bandwidth > stkid.minbw*3 AND stk.dte=" + oStk.padCh(oStk.currDate);
        //System.out.println(sql);
        stmt = oStk.conn.createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            sql = "update stkid set minbw_dte = NULL,minbw = 0,sqdays = 0 where stockid ='"
                    + rs.getString("stockid") + "'";
            if (oStk.stmt.executeUpdate(sql) == 0) {
                System.out.println(sql + " *** ERROR ***");
            }
        }
        rs.close();
        System.out.println("upd_stkid_minbw() Done!");
    }

    /**
     * 計算目前已經是壓縮第幾天<br>
     * 帶寬不大於最小帶寬50%者視為壓縮， 計算最小帶寬日前後40天之壓縮天數
     *
     * @throws SQLException
     */
    void calc_sqdays() throws SQLException {
        ResultSet rs, rs2;
        Statement stmt, stmt2, stmt3;
        String sdate1, sdate2, sql, sql2, sql3, stockid;
        double minbw;
        java.util.Date minbw_date;

        sql = "select stockid,minbw,minbw_dte from stkid where minbw_dte is not null and minbw_dte != 0";
        stmt = oStk.conn.createStatement();
        stmt2 = oStk.conn.createStatement();
        stmt3 = oStk.conn.createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            minbw_date = rs.getDate("minbw_dte");
            stockid = rs.getString("stockid");
            sdate1 = oStk.getPrevStockDate(minbw_date, 40);
            sdate2 = oStk.getNextStockDate(minbw_date, 40);
            minbw = rs.getDouble("minbw");
            if (minbw != 0d) {
                sql2 = String.format("select count(*) as CNT from stk where stockid ='%s'"
                        + " and dte between %s and %s and (bandwidth - %f) / %f < 0.5",
                        stockid, oStk.padCh(sdate1), oStk.padCh(sdate2), minbw, minbw);
                //System.out.println(sql);

                rs2 = stmt2.executeQuery(sql2);
                if (rs2.next()) {
                    sql3 = "update stkid set sqdays = " + rs2.getInt("cnt")
                            + " where stockid = '" + stockid + "'";
                    if (stmt3.executeUpdate(sql3) == 0) {
                        System.out.println(sql3 + " *** ERROR ***");
                    }
                }
                rs2.close();
            }
        }
        rs.close();
        System.out.println("calc_sqdays() Done!");
    }

    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** CalcOther() *****");
        try {
            CalcOther oo = new CalcOther();
            if (args.length > 0 && args[0].equals("Y")) {
                oo.isAll = true;
            }
            oo.calc_updown();
            oo.calc_sc();
            //oo.upd_minbw();//取消，直接由genSqueeze.java產生
            //oo.upd_stkid_minbw();//取消，直接由genSqueeze.java產生
            //oo.calc_sqdays();//取消，直接由genSqueeze.java產生
            oo.calc_pricechg();
        } catch (SQLException e) {
            System.err.println("*** SQL Error ***");
            System.err.println(e);
            System.err.println(e.getLocalizedMessage());
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            System.err.println(e);
            System.err.println(e.getLocalizedMessage());
            System.err.println(e.getMessage());
            e.printStackTrace();

            System.exit(-1);
        }

    } // main

}//CalcOther
