
import java.sql.*;

/**
 * 計算常態化指標 %b, stddev, bandWidth等包寧傑帶狀參數
 *
 * @author huangtm
 */
public final class CalcPtb {

    Boolean isAll; // true-全部資料重新產生，false-只針對今天的資料
    StkDb oStk;
    Statement stmt;

    CalcPtb() throws SQLException {
        isAll = false;
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
    }

    void calc_all_ptb() throws SQLException {
        CalcPtb1("price", 20, "stddev20", "percentb", "bandwidth", 2.0d);
        CalcPtb1("mfi", 50, "", "ptbmfi", "", 2.1d);
        CalcPtb1("rsi12", 50, "", "ptbrsi12", "", 2.1d);
        CalcPtb1("rsi6", 20, "", "ptbrsi6", "", 2.0d);
        //genSqueezeData(); //取消，直接由genSqueeze.java產生
    } // calc_all_ma
    

    void genSqueezeData() throws SQLException {
        String sql;
        stmt = oStk.conn.createStatement();
        sql = String.format("delete from squeeze where dte=%s",
                oStk.padCh(oStk.currDate));
        stmt.executeUpdate(sql);
        sql = "insert into squeeze select * from v_squeeze_ins";
        stmt.executeUpdate(sql);
        System.out.println("***** insert squeeze(價格波動率突破表) Done! *****");      
    }

    /**
     * 計算包寧傑指標
     *
     * @param sumFld 要統計的欄位，如收盤價就是Price,成交量就是 vol或mfi,rsi等
     * @param days 期數
     * @param stdFld 標準差，存起來將來可以計算 UB,LB與BandWidth
     * @param ptbFld %b欄位，如 ptb_ma20, ptb_va20
     * @param bwFld bandWidth帶寬欄位，如果是 ""則不存入帶寬
     * @param stdWidth 幾個標準差，一般是2個標準差
     */
    @SuppressWarnings("UnusedAssignment")
    void CalcPtb1(String sumFld, int days, String stdFld, String ptbFld, String bwFld, Double stdWidth)
            throws SQLException {
        ResultSet rs;
        int i, idxLast = 0;
        Double tot = 0d, ma = 0d;
        String tmpId, sWhere, sFromDate, sql;
        Double[] Que = new Double[days];
        Boolean noEnoughRecords = true;
        java.util.Date dCurrDate; // 資料庫中的最後一天
        Double dec = 0d;
        Double nvar, nstdDev, ub, lb, bandWidth, ptb, fldValue;

        tmpId = "";
        dCurrDate = oStk.strToDate(oStk.currDate);
        sFromDate = oStk.getPrevStockDate(dCurrDate, days + 1 + oStk.tourdays);
        if (isAll) {
            sWhere = "";
        } else {
            sWhere = String.format(" WHERE dte > %s and dte <= %s",
                    oStk.padCh(sFromDate), oStk.padCh(oStk.currDate));
        }
        sql = "select stockid,dte," + sumFld + "," + ptbFld;
        if (!stdFld.equals("")) {
            sql = sql + "," + stdFld;
        }
        if (!bwFld.equals("")) {
            sql = sql + "," + bwFld;
        }
        sql = sql + " from stk " + sWhere + " ORDER BY stockid,dte";
        stmt = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            if (!tmpId.equals(rs.getString("stockid"))) {
                for (i = 0; i < Que.length; i++) {
                    Que[i] = 0d;
                }
                tot = 0d;
                idxLast = 0;
                tmpId = rs.getString("stockid");
                noEnoughRecords = false;
                for (i = 0; i <= days - 2; i++) {
                    Que[idxLast] = rs.getDouble(sumFld);
                    if (!rs.wasNull()) {
                        tot = tot + Que[idxLast];
                    }
                    rs.next();
                    if (rs.isAfterLast() || !(rs.getString("stockid")).equals(tmpId)) {
                        noEnoughRecords = true;
                        break;
                    }
                    idxLast = idxLast + 1;
                } // for
            } // if
            if (!noEnoughRecords) {
                fldValue = rs.getDouble(sumFld);
                if (!rs.wasNull()) {
                    tot = tot + fldValue;
                    ma = tot / days;
                    Que[idxLast] = fldValue;
                } else {
                    fldValue = 0d;
                    tot = tot + fldValue;
                    ma = tot / days;
                    Que[idxLast] = fldValue;
                }
                nvar = 0d;
                for (i = 0; i < days; i++) {
                    nvar += Math.pow((Que[i] - ma), 2); // ^2
                }
                nstdDev = Math.sqrt(nvar / days); //*** 除數是N不是N-1
                ub = ma + nstdDev * stdWidth;
                lb = ma - nstdDev * stdWidth;
                if (ma != 0D) {
                    bandWidth = (ub - lb) / ma; //帶寬
                } else {
                    bandWidth = 0D;
                }
                if ((ub - lb) != 0D) {
                    ptb = (fldValue - lb) / (ub - lb);
                    dec = rs.getDouble(ptbFld);
                    if (isAll || dec == 0d) {
                        if (!stdFld.equals("")) {
                            rs.updateDouble(stdFld, nstdDev);
                        }
                        rs.updateDouble(ptbFld, ptb);
                        if (!bwFld.equals("")) {
                            rs.updateDouble(bwFld, bandWidth);
                        }
                        rs.updateRow();
                    }
                }
                i = (idxLast + 1) % days; //最後一個的下一個就是第一個
                tot = tot - Que[i];
                idxLast = (idxLast + 1) % days;
            }
        } // while
        rs.close();
        System.out.println("Calc " + ptbFld + " Done!");
    }

    /**
     * 執行方式: java CalcPtb [isAll]
     *
     * @param args [] Y:產生全部，注意：將非常耗時
     */
    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** CalcPtb() *****");
        try {
            CalcPtb oo = new CalcPtb();
            if (args.length > 0 && args[0].equals("Y")) {
                oo.isAll = true;
            }
            oo.calc_all_ptb();
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
