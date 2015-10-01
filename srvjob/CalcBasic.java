
import java.sql.*;

public final class CalcBasic {

    /**
     * WAIT_RET_DAYS--譬如杯柄型的高點應該在至少十天以前，否則目前突破高點的價位 就被當成高點了 飆股的漲相其突破後整理的時間要三個月以上
     */
    final int WAIT_RET_DAYS = 60;
    final int CRI_PER = 12; // 低於此值則 nVal +1

    Boolean isAll; // true-全部資料重新產生，false-只針對今天的資料
    StkDb oStk;
    Statement stmt;
    ResultSet rslt;
    double currPrice;
    int nVal; //價值分數

    CalcBasic() throws SQLException {
        isAll = false;
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
    }

    /**
     * 計算兩年高點與低點
     */
    void calc_maxmin() throws SQLException {

        Statement stmt2;
        java.util.Date dCurrDate;
        String sql, begDate, endDate;

        dCurrDate = oStk.strToDate(oStk.currDate);
        begDate = oStk.getPrevStockDate(dCurrDate, 480 + WAIT_RET_DAYS);
        endDate = oStk.getPrevStockDate(dCurrDate, WAIT_RET_DAYS);
        System.out.println(String.format("***** calc yr max,min 2年前至3個月以前=%s～%s *****",
                begDate, endDate));
        sql = String.format("select stockid,max(p_high) as maxp ,min(p_low) as minp from stk "
                + "where dte between %s and %s group by stockid",
                oStk.padCh(begDate), oStk.padCh(endDate));
        stmt = oStk.conn.createStatement();
        stmt2 = oStk.conn.createStatement();
        rslt = stmt.executeQuery(sql);
        while (rslt.next()) {
            sql = String.format("update stkbasic set YRMAX=%f, YRMIN=%f where stockid = '%s'",
                    rslt.getDouble("maxp"), rslt.getDouble("minp"), rslt.getString("stockid"));
            stmt2.executeUpdate(sql);
        }
        rslt.close();
        stmt2.close();
    }

    /**
     * 計算股價現金比
     *
     * @return 股價現金比
     * @throws SQLException
     */
    double get_pricecash_r() throws SQLException {
        double n1, n2, n3, dVal;
        n1 = rslt.getDouble("outcap");
        if (rslt.wasNull()) {
            n3 = 0;
        } else {
            n1 = n1 * 100000000d / 10d;//流通股本
            dVal = rslt.getDouble("cashbeop"); //期末現金及約當現金(Cash Balances - End of Period)
            if (rslt.wasNull()) {
                n3 = 0;
            } else {
                if (n1 != 0 && dVal != 0) {
                    n2 = dVal * 1000000 / n1;//每股現金流量
                    n3 = currPrice / n2;
                } else {
                    n3 = 0;
                }
            }
        }

        return n3;
    }

    /**
     * 計算每股股價營收比(psr)
     *
     * @return psr
     */
    double get_psr() throws SQLException {
        double n1, n2, n3, dVal;
        n1 = rslt.getDouble("outcap") * 100000000d / 10d;//流通股本
        dVal = rslt.getDouble("rev12");//最近12個月總營收(仟)(Revenue)
        if (n1 != 0 && dVal != 0) {
            n2 = dVal * 1000 / n1; //每股營收
            n3 = currPrice / n2;
        } else {
            n3 = -1;
        }
        return n3;
    }

    /**
     * 計算股價淨值比(Price-Book Ratio)
     *
     * @return pbr
     */
    double get_pbr() throws SQLException {
        double n3, dVal;
        dVal = rslt.getDouble("nav");//每股淨值 
        if (dVal != 0) {
            n3 = currPrice / dVal;
        } else {
            n3 = -1;
        }
        return n3;
    }

    /**
     * 計算最近四季的EPS，用以估算本益比
     *
     * @return eps4
     */
    double get_eps4() throws SQLException {
        double n2;
        double nn[] = new double[4];
        int ix;
        n2 = 0;
        for (ix = 0; ix < 4; ix++) {
            nn[ix] = rslt.getDouble("qeps_" + (ix + 1));
            if (rslt.wasNull()) {
                n2 = -1;
                break;
            }
            n2 += nn[ix]; // eps4
        }
        return n2;
    }

    /**
     * 計算現金殖利率(Cash Dividend Yield)
     *
     * @return cashdiv_yld
     */
    double get_cashdiv_yld(double eps4) throws SQLException {
        int yr1, yr2;
        String sYr1, sYr2;
        int diff;
        int i;
        String epsNm[] = new String[4];
        String cashNm[] = new String[4];
        boolean isNumber, isValid;
        double cashdiv_yld;

        double dVal1, dVal2, n1, n3;

        sYr1 = rslt.getString("yr");
        sYr2 = rslt.getString("yr2");
        if (sYr1 == null || sYr2 == null || "".equals(sYr1)
                || "".equals(sYr2)) {
            return 0d;
        }
        i = sYr2.indexOf(".");//*** EPS 的年度有時候如下：97.1~4Q
        if (i == -1) {
            yr2 = Integer.parseInt(sYr2);
            isNumber = true;
        } else {
            yr2 = Integer.parseInt(sYr2.substring(0, i));
            isNumber = false;
        }
        yr1 = Integer.parseInt(sYr1);
        diff = yr1 - yr2;//*** 有時候 yr1 (現金股利)是從98年度開始，而EPS的yr2是從96年度開始
        //System.out.printf("id=%s, yr1=%d,yr2=%d\r\n ",
        //        rslt.getString("stockid"),yr1,yr2);
        if (diff+4 > 6) {
            return 0;
        }
        for (i = 0; i < 4; i++) {
            if (!isNumber) {
                epsNm[i] = "EPS_" + (i + 2);
            } else {
                epsNm[i] = "EPS_" + (i + 1);
            }
            cashNm[i] = "cashdiv_" + (i + 1 + diff); //現金股利(Cash Dividend)
        }
        isValid = true;
        for (i = 0; i < 4; i++) {
            dVal1 = rslt.getDouble(cashNm[i]);
            if (rslt.wasNull()) {
                isValid = false;
                break;
            }
            dVal1 = rslt.getDouble(epsNm[i]);
            if (rslt.wasNull() || dVal1 == 0) {
                isValid = false;
                break;
            }
        }
        if (!isValid) {
            return 0;
        }
        n1 = 0;
        for (i = 0; i < 4; i++) {
            n1 += rslt.getDouble(cashNm[i]) / rslt.getDouble(epsNm[i]);
        }
        cashdiv_yld = 0;
        if (eps4 > 0.001 && currPrice != 0) {
            cashdiv_yld = eps4 * (n1 / 4) / currPrice * 100;//預估配發現金/股價
        }
        return cashdiv_yld;
    }

    void calc_stockValueIndex() throws SQLException {
        ResultSet rs2;
        Statement stmt2;
        String sql, sFldVal1, sFldVal2;
        double dVal, dVal2;
        double eps4;
        int yr1; //基本資料起始年度(現金股利等)
        int yr2; //財務比率起始年度(eps,roe,流動比率等)

        System.out.println("***** 計算pbr,per,psr等　*****");
        stmt2 = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);//stmt2可以updateRow()
        rslt = stmt.executeQuery("select a.*,b.price from stkbasic a,stkid b where a.stockid = b.stockid");
        while (rslt.next()) {
            nVal = 0;
            currPrice = rslt.getDouble("price");
            sql = String.format("select * from stkbasic where stockid ='%s'", rslt.getString("stockid"));
            rs2 = stmt2.executeQuery(sql);
            if (!rs2.next()){
                System.out.println("not found "+rslt.getString("stockid"));
                continue;
            }

            dVal = get_pricecash_r();
            if (dVal <= 10 && dVal > 0) {
                nVal++;
            }
            rs2.updateDouble("pricecash_r", dVal);//股價現金比

            dVal = get_psr();
            if (dVal <= 1.0 && dVal > 0d) {
                nVal++;
            }
            rs2.updateDouble("psr", dVal);//每股股價營收比

            dVal = get_pbr();
            if (dVal <= 1.0d && dVal > 0d) {
                nVal++;
            }
            rs2.updateDouble("pbr", dVal);//股價淨值比(Price-Book Ratio)

            dVal = get_eps4();
            dVal2 = dVal <= 0 ? -1 : currPrice / dVal;
            if (dVal2 <= 12 && dVal2 > 0) {
                nVal++;
            }
            eps4 = dVal;
            rs2.updateDouble("eps4", eps4);
            rs2.updateDouble("per", dVal2);//本益比
            rs2.updateDouble("val", nVal);//價值分數

            dVal = rslt.getDouble("YRMAX");
            if (dVal != 0) {
                dVal2 = (currPrice - dVal) / dVal * 100;
            } else {
                dVal2 = -100;
            }
            rs2.updateDouble("yrdown", dVal2);//距2年高點(%)
            dVal = get_cashdiv_yld(eps4);
            rs2.updateDouble("cashdiv_yld", dVal);//現金殖利率
            rs2.updateRow();

        }//while

    }

    /**
     * 一年變動一次的部分
     */
    void calc_YearOnce() throws SQLException {
        final int ROE_POS = 8;
        final int QEG_POS = 13;
        String aFld[] = {"long_inv_1", "fix_ass_1", "long_inv_5", "fix_ass_5",
            "nprofita_1", "nprofita_2", "nprofita_3", "nprofita_4",
            "roe_1", "roe_2", "roe_3", "roe_4", "roe_5",
            "qeg_1", "qeg_2", "qeg_3", "qeg_4", "qeg_5", "qeg_6", "qeg_7", "qeg_8"};
        double nn[] = new double[aFld.length], n1, n2, n3, n4;
        ResultSet rs2;
        Statement stmt2;
        String sql;
        boolean isValid = true;
        int i, i2;
        
        System.out.println("***** calc_YearOnce()　*****");

        stmt.executeUpdate("update stkbasic set reinv_rate4=NULL,roe5=NULL,roe_sc=NULL,qeg_sc=NULL");
        stmt2 = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);//stmt2可以updateRow()
        rs2 = stmt2.executeQuery("select * from stkbasic");
        while (rs2.next()) {
            for (i = 0; i < aFld.length; i++) {
                nn[i] = rs2.getDouble(aFld[i]);
                if (rs2.wasNull()) {
                    isValid = false;
                    break;
                }
            }//for
            if (!isValid) {
                continue;
            }
            /* ----- 計算盈再率
             n1 = rs("長期投資_1") + rs("固定資產_1") - rs("長期投資_5") - rs("固定資產_5")
             n2 = rs("稅後淨利_1") + rs("稅後淨利_2") + rs("稅後淨利_3") + rs("稅後淨利_4")
             */
            n1 = 0;
            n2 = 0;
            for (i = 0; i < 4; i++) {
                n1 += nn[i];
            }
            for (i = 4; i < 8; i++) {
                n2 += nn[i];
            }
            if (n2 != 0) {
                n3 = Double.parseDouble(String.format("%.1f", n1 / n2 * 100));
            } else {
                n3 = 999d;
            }
            rs2.updateDouble("reinv_rate4", n3);//四年盈再率

            // -----計算五年ROE平均
            n1 = 0;
            for (i = 1; i < 5; i++) {
                n1 += rs2.getDouble("roe_" + i);
            }
            rs2.updateDouble("roe5",
                    Double.parseDouble(String.format("%.1f", n1 / 5)));

            // ----- 計算ROE_SC,ROE的品質，roe_1是最近一年 
            i2 = 0;
            for (i = 1; i < 5; i++) {
                if (nn[i + ROE_POS] == 0) { //下一個ROE,=( i+ ROE_POS-1 +1)
                    n4 = 0;
                } else {
                    n4 = (nn[i + ROE_POS - 1] - nn[i + ROE_POS]) / nn[i + ROE_POS];
                }
                if (n4 > 0) {//成長+2分
                    i2 += 2;
                } else if (n4 >= -0.05) {//衰退5%以內仍+1分
                    i2 += 1;
                }
            }
            rs2.updateInt("roe5", i2);//roe品質

            // ----- 計算qeg_sc 獲利成長品質            
            i2 = 0;
            for (i = 1; i < 9; i++) {
                n4 = nn[i + QEG_POS - 1];
                if (n4 >= 10) {
                    i2 += 1;
                } else if (n4 < 0) {
                    i2 -= 1;
                }
            }
            rs2.updateInt("qeg_sc", i2);

            rs2.updateRow();

        }//while

    }
    /**
     * 備份今日stk到stkBackup
     */
    void backupTodayStk() throws SQLException {

        String sql;
        int cnt;
        stmt = oStk.conn.createStatement();
        sql = String.format("delete from stkbackup where dte = %s",
                oStk.padCh(oStk.currDate));
        System.out.println(sql);
        stmt.executeUpdate(sql);
        sql = String.format("insert into stkbackup select * from stk where dte = %s",
                oStk.padCh(oStk.currDate));
        System.out.println(sql);
        cnt = stmt.executeUpdate(sql);
        stmt.close();
        System.out.printf("***** 備份(%s)stk到stkbackup 共 %d筆*****\n",
                oStk.currDate, cnt);        
    }

    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** CalcBasic() *****");
        try {
            CalcBasic oo = new CalcBasic();
            if (args.length > 0 && args[0].equals("Y")) {
                oo.isAll = true;
            }
            oo.calc_maxmin();
            oo.calc_stockValueIndex();
            oo.calc_YearOnce();
            oo.backupTodayStk();
            System.out.println("\r\n***** CalcBasic() Done! *****");
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

}//CalcBasic
