
import java.sql.*;

/**
 *  股票資料庫物件，繼承自MyDb
 * @author huangtm
 */
public class StkDb extends MyDb {

    String fldStockid, fldDte, fldPrice, fldHigh, fldLow, fldOpen;
    String tblStk, tblStkid;
    Statement stmt;
    // --- SYSPARM ---
    String currDate, hist_begdate;
    double cost_op, cost_tax, tot_money;
    String private_loc, public_loc;
    int tourdays;

    StkDb(String sType, String sDBName, String sUser, String sPwd) {
        super(sType, sDBName, sUser, sPwd);
    }
    /**
     * 物件初始化
     * @param sType "P":do getSysParm(()
     */
    public void init(String sType) throws SQLException
    {
        setNames();
        if (sType.contains("P"))
            getSysParm();
    }
/**
 * 定義資料表及欄位名稱	---
 */

    public void setNames() {
        fldStockid = "stockid";
        fldDte = "dte";
        fldPrice = "price";
        fldHigh = "p_high";
        fldLow = "p_low";
        fldOpen = "p_open";
        tblStk = "stk";
        tblStkid = "stkid";
    }
    /**
     * 取得SYSPARM中的欄位
     */
    public void getSysParm() {
        String sql, sdte;
        java.util.Date dteBeg = new java.util.Date(0);
        java.util.Date dte = new java.util.Date(0);
        ResultSet rs;

        sql = "select * from SYSPARM";
        sdte = "";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                currDate = rs.getString("currdate");
                hist_begdate = rs.getString("hist_begdate");
                cost_op = rs.getDouble("cost_op");
                cost_tax = rs.getDouble("cost_tax");
                private_loc = rs.getString("private_loc");
                public_loc = rs.getString("public_loc");
                tot_money = rs.getDouble("tot_money");
                tourdays = rs.getInt("tourdays");
            } else {
                System.err.println("沒有SYSPARM紀錄");
                System.exit(-1);
            }

        } catch (SQLException e) {
            System.err.println("in getSysParm -- SQL Error");
            System.err.println(e.getLocalizedMessage());
        }
    }


    /**
     * 取得幾天前股票日期，注意：當天算第1天
     * @param pDate
     * @param pDays
     * @return String of date format, like: 2015/01/28
     * @throws SQLException 
     */
    public String getPrevStockDate(java.util.Date pDate, int pDays) throws SQLException {
        int iDays, i;
        String sql, sdte;
        java.util.Date dteBeg = new java.util.Date(0);
        java.util.Date dte = new java.util.Date(0);
        Statement stmt;
        ResultSet rs;

        if (pDays < 20) {
            iDays = 40;
        } else {
            iDays = pDays * 2;
        }
        dteBeg = addToDate(pDate, -1 * iDays);

        sql = "select " + fldDte + " from " + tblStk
                + " where " + fldStockid + "= '1101' and " + fldDte + " BETWEEN "
                + padCh(dateToStr(dteBeg));
        sql += " and " + padCh(dateToStr(pDate))
                + " ORDER BY " + fldDte + " DESC";
        sdte = "";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            i = 1;

            while (rs.next()) {
                dte = rs.getDate(fldDte);
                if (i++ >= pDays) {
                    break;
                }
            }
            //sdte = sqldte.toString();
            sdte = dateToStr(dte);
        } catch (SQLException e) {
            System.err.println("in getPrevStockDate SQL Error");
            System.err.println(e.getLocalizedMessage());
        }

        return sdte;
    }
    /**
     * 取得幾天後股票日期，注意：當天算第1天
     * @param pDate
     * @param pDays
     * @return String of date format, like: 2015/01/28
     * @throws SQLException 
     */

    public String getNextStockDate(java.util.Date pDate, int pDays) throws SQLException {
        int iDays, i;
        String sql, sdte;
        java.util.Date dteEnd = new java.util.Date(0);
        java.util.Date dte = new java.util.Date(0);
        ResultSet rs;
        Statement stmt;

        if (pDays < 20) {
            iDays = 40;
        } else {
            iDays = pDays * 2;
        }
        dteEnd = addToDate(pDate, iDays);

        sql = "select " + fldDte + " from " + tblStk
                + " where " + fldStockid + "= '1101' and " + fldDte + " BETWEEN "
                + padCh(dateToStr(pDate));
        sql += " and " + padCh(dateToStr(dteEnd)) + " ORDER BY " + fldDte;
        sdte = "";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            i = 1;

            while (rs.next()) {
                dte = rs.getDate(fldDte);
                if (i++ >= pDays) {
                    break;
                }
            }
            sdte = dateToStr(dte);
        } catch (SQLException e) {
            System.err.println("in getNextStockDate SQL Error");
            System.err.println(e.getLocalizedMessage());
        }

        return sdte;
    }
    /**
     * 取得最後一天股票日期，注意：當天算第1天
     * 並將其放入 currDate 
     */

    public void getLastStockDate() {
        String sql, sdte;
        java.util.Date dteBeg = new java.util.Date(0);
        java.util.Date dte = new java.util.Date(0);
        ResultSet rs;

        dteBeg = addToDate((new java.util.Date()), -40);

        sql = "select " + fldDte + " from " + tblStk
                + " where " + fldStockid + "= '1101' and " + fldDte + " >= "
                + padCh(dateToStr(dteBeg));
        sql += " ORDER BY " + fldDte;
        sdte = "";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                dte = rs.getDate(fldDte);
                //System.out.println(sqldte);
            }
            sdte = dateToStr(dte);
        } catch (SQLException e) {
            System.err.println("in getLastStockDate SQL Error");
            System.err.println(e.getLocalizedMessage());
        }

        currDate = sdte;
    }

}
