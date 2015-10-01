
import java.sql.*;

/**
 * <h1>計算價格波動率突破交易系統的測試資料</h1>
 * usage: java GenSqueeze dbname [startDate, endDate]<br>
 * 當沒有參數時則為stk的最後一天
* dbname : like 127.0.0.1/mystk
  *
 * @author huangtm
 */
public final class GenSqueeze {

    final double SQUEEZE_CLEAR_CRI = 3.0;//當bandwidth >最小帶寬*3時清除擠壓狀態
    final int SQUEEZE_DAYS = 120; // 計算幾天以內的擠壓情形(原180)
    final int SQUEEZE_DAYS_BEFORE = 20; // 計算發生擠壓之前n天的壓縮天數 (原40)
    final double MIN_CHG_UP = 2.0; // 當日最小涨服，價格波動率突破交易系統是用4%

    StkDb oStk;
    private Statement stmt, stmt2;
    ResultSet rs;
    String startDate, endDate;
    String tblName = "squeeze";

    String lastId;
    java.util.Date lowDate, highDate, boxDate;
    double lowP, highP, lowVa10;
    int Hdays;

    GenSqueeze(String args[]) throws SQLException {
        String dbName;
        if (args.length < 1) {
            System.err.println("第1個參數必須是資料庫名稱(like:127.0.0.1/mystk)");
            System.exit(-1);
        }
        dbName = args[0];
        oStk = new StkDb(DbConfig.DB_TYPE, dbName, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
        if (args.length < 3) {
            startDate = oStk.currDate;
            endDate = oStk.currDate;
        } else {
            startDate = args[1];
            endDate = args[2];
        }
    }

    void InsertRec_2(double minbw, java.util.Date minbw_dte, int sqdays) throws SQLException {
        String sql2;
        stmt2 = oStk.conn.createStatement();

        MyFunc.genSql_d(minbw, "minbw", "'");
        MyFunc.genSql(oStk.dateToStrCh(minbw_dte), "minbw_dte", "");
        MyFunc.genSql_d(sqdays, "sqdays", "");

        MyFunc.genSqlEnd();
        sql2 = MyFunc.getInsertSql(tblName);
        //System.out.println(sql2);
        if (stmt2.executeUpdate(sql2) == 0) {
            System.out.println("InsertRec() SQL INSERT ERROR!" + sql2);
            System.exit(-1);
        }

    }

    /**
     * 將目前紀錄先暫存至未完成的 SQL，將來如果squeeze成立， 再經由InsertRec_2補足其他部分
     *
     * @param rs
     * @throws SQLException
     */
    void InsertRec_1(ResultSet rs) throws SQLException {

        String sql2;
        MyFunc.initSql();
        MyFunc.genSql(rs.getString("stockid"), "stockid", "'");
        MyFunc.genSql(oStk.dateToStrCh(rs.getDate("dte")), "dte", "");
        MyFunc.genSql_d(rs.getDouble("bandwidth"), "bandwidth", "");
        MyFunc.genSql_d(rs.getDouble("vol"), "vol", "");
        MyFunc.genSql(rs.getString("va5"), "va5", "");
        MyFunc.genSql(rs.getString("va10"), "va10", "");
        MyFunc.genSql_d(rs.getDouble("updown"), "updown", "");
        MyFunc.genSql_d(rs.getDouble("price"), "price", "");
        MyFunc.genSql_d(rs.getDouble("ub"), "ub", "");
        MyFunc.genSql_d(rs.getDouble("percentb"), "percentb", "");
        MyFunc.genSql(rs.getString("sc_osc"), "sc_osc", "");
        MyFunc.genSql_d(rs.getDouble("mfi"), "mfi", "");
        MyFunc.genSql_d(rs.getDouble("ptbrsi12"), "ptbrsi12", "");

    }

    /**
     * 建立某一天的squeeze資料(價格波動率突破)
     *
     * @throws SQLException
     */
    void genSqueeze_1(java.util.Date dTheDate) throws SQLException {
        String sDteEnd, sDteBeg;
        double minbw;
        String sql, theId;
        Statement stmtId;
        ResultSet rsId;
        int days, days_before;
        java.util.Date minbw_dte = null;
        boolean isSqueeze;

        stmtId = oStk.conn.createStatement();
        sql = String.format("select stockid from v_squeeze_gen  where dte = %s and updown >=%.2f",
                oStk.dateToStrCh(dTheDate), MIN_CHG_UP);
        stmt = oStk.conn.createStatement();
        sDteEnd = oStk.dateToStr(dTheDate);
        sDteBeg = oStk.getPrevStockDate(dTheDate, SQUEEZE_DAYS);
        rsId = stmtId.executeQuery(sql);
        while (rsId.next()) {
            theId = rsId.getString("stockid");
            // 讀取該股票在期間內的最小帶寬
            sql = String.format("SELECT stockid, Min(bandwidth) AS MIN_WIDTH FROM stk "
                    + "WHERE stockid ='%s' and dte BETWEEN %s AND %s GROUP BY stockid",
                    theId, oStk.padCh(sDteBeg), oStk.padCh(sDteEnd));
            rs = stmt.executeQuery(sql);
            if (!rs.next()) {
                System.err.println("不可能發生!");
                System.exit(-1);
            }
            minbw = rs.getDouble("MIN_WIDTH");
            rs.close();
            // Notice: order is DESC
            sql = String.format("select * from stk where stockid='%s' "
                    + "and dte BETWEEN %s AND %s order by dte DESC",
                    theId, oStk.padCh(sDteBeg), oStk.padCh(sDteEnd));
            rs = stmt.executeQuery(sql);
            days = 0;
            days_before = 0;
            isSqueeze = false;
            while (rs.next()) {
                days++;
                if (days == 1) { //dTheDate那天
                    InsertRec_1(rs); //還需補足 minbw, minbw_dte
                } 
                if (rs.getDouble("bandwidth") > minbw * SQUEEZE_CLEAR_CRI) {
                    // 如果帶寬已經放大三倍(從最小帶寬)則擠壓現象已經消失
                    break;
                }
                if (isSqueeze) {
                    days_before++;//擠壓日以前
                    if (days_before > SQUEEZE_DAYS_BEFORE) {
                        break;
                    }
                } else if (minbw == rs.getDouble("bandwidth")) {
                    isSqueeze = true;
                    minbw_dte = rs.getDate("dte");
                }
            }//while rs.next
            if (isSqueeze) {
                InsertRec_2(minbw, minbw_dte, days);
            }
            rs.close();
        } // while rsId.next
    }

    /**
     * squeeze資料必須逐日產生，首先清除期間內原有BOX資料， 然後依日期逐日呼叫gebox_1()
     *
     * @throws SQLException
     */
    void doGen() throws SQLException {
        ResultSet rsD;
        Statement stmtD;
        String sqlD;
        int i;
        java.util.Date theDte;
        System.out.printf("***** GenSqueeze (%s - %s) *****\r\n", startDate, endDate);
        stmtD = oStk.conn.createStatement();
        sqlD = String.format("delete from %s where dte between %s and %s",
                tblName, oStk.padCh(startDate), oStk.padCh(endDate));
        stmtD.executeUpdate(sqlD);
        sqlD = String.format("select dte from stk where stockid='1101' and dte between %s and %s order by dte",
                oStk.padCh(startDate), oStk.padCh(endDate));
        rsD = stmtD.executeQuery(sqlD);
        while (rsD.next()) {
            theDte = rsD.getDate("dte");
            System.out.printf("=== %s ===\r\n", oStk.dateToStr(theDte));
            genSqueeze_1(theDte);
        }
        System.out.printf("***** GenSqueeze Complete *****\r\n", startDate, endDate);

    }

    public static void main(String[] args) {
        System.setErr(System.out);
        try {
            GenSqueeze oo = new GenSqueeze(args);
            oo.doGen();
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
