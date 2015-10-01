
import java.sql.*;

/**
 * <h1>計算各種BOX，EBOX, DBOX, HBox</h1>
 * <ol>
 * <li>EBOX -- 交易系統ebox, 突破廂型
 * <li>DBOX -- 交易系統dbox, 跌破廂型
 * </ol><br>
 * usage: java GenBox dbname [startDate endDate]<br>
 * 當沒有參數時則為stk的最後一天
 * dbname : like 127.0.0.1/mystk
 *
 * @author huangtm
 */
public final class GenBox {

    final int EBOX = 0;
    final int DBOX = 1;
    final double BT_DIST = 6.0d; // 收盤距箱頂%
    final double BB_DIST = 4.0d; // 收盤距箱底%
    final double BOXWIDTH_MAX = 40.0d; // 最大箱型高度
    final int HDAYS_MIN = 14; //高點至當日至少n天
    final int LDAYS_MIN = 5; //高點後低點至當日的天數至少n天

    StkDb oStk;
    Statement stmt, stmt2;
    ResultSet rs;
    String startDate, endDate;
    String tblName[] = {"box", "dboxdata"};

    String lastId;
    java.util.Date lowDate, highDate, boxDate;
    double lowP, highP, lowVa10;
    int Hdays;

    GenBox(String args[]) throws SQLException {
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

    void Init_WhenGrpChg() throws SQLException {
        lastId = rs.getString("stockid");
        lowDate = rs.getDate("dte");
        highDate = rs.getDate("dte");
        lowP = rs.getDouble("p_low");
        highP = rs.getDouble("p_high");
        lowVa10 = rs.getDouble("va10");
        Hdays = 0;
    }

    void InsertRec(int nBoxType) throws SQLException {

        String sql2;
        ResultSet rs2;
        boolean isOk;
        double bot, p_low, currPrice, vol, va5, udRate, boxWidth;
        double Ldays; //高點後低點至當日的天數
        java.util.Date bottomDate = null;
        double diff_bb, diff_bt;


        sql2 = String.format(
                "select p_low, dte,price  from stk "
                + "where stockid='%s' and dte between %s and %s",
                lastId, oStk.dateToStrCh(highDate), oStk.dateToStrCh(boxDate));

        rs2 = stmt2.executeQuery(sql2);
        if (!rs2.next()) {
            return;
        }
        bot = 9999;
        Ldays = 0;
        while (rs2.next()) {
            p_low = rs2.getDouble("p_low");
            if (p_low < bot) {
                bot = p_low;
                bottomDate = rs2.getDate("dte");
                Ldays = 0;
            } else {
                Ldays++;
            }
        }
        if (bot == 9999 || bot == 0) {
            return;
        }
        udRate = Double.parseDouble(String.format("%.2f",
                (highP - lowP) / lowP * 100));
        MyFunc.initSql();
        MyFunc.genSql(lastId, "stockid", "'");
        MyFunc.genSql(oStk.dateToStrCh(boxDate), "dte", "");
        MyFunc.genSql(oStk.dateToStrCh(lowDate), "low1dte", ""); //起漲日期
        MyFunc.genSql_d(lowP, "low1price", "");//起漲價格
        MyFunc.genSql_d(lowVa10, "low1Va10", "");//起漲日10日均量
        MyFunc.genSql(oStk.dateToStrCh(highDate), "highdte", ""); //最高價日期
        MyFunc.genSql_d(highP, "highprice", "");//最高價
        MyFunc.genSql_d(udRate, "udrate", "");//漲幅
        MyFunc.genSql_d(bot, "low2price", "");//最低價
        MyFunc.genSql(oStk.dateToStrCh(bottomDate), "low2dte", ""); //最低價日期
        boxWidth = -1.0 * Double.parseDouble(String.format("%.2f", (bot - highP) / highP * 100));
        MyFunc.genSql_d(boxWidth, "boxwidth", "");//波幅
        if (boxWidth == 0 || boxWidth > BOXWIDTH_MAX
                || Ldays < LDAYS_MIN || (Hdays + 1) < HDAYS_MIN) {
            return; // Hdays只計算到前一天，所以要+1
        }
        sql2 = "select price,va5,vol from stk where stockid='" + lastId + "' and dte = "
                + oStk.dateToStrCh(boxDate);
        rs2 = stmt2.executeQuery(sql2);
        if (!rs2.next()) {
            return;
        }
        currPrice = rs2.getDouble("price");
        vol = rs2.getDouble("vol");
        va5 = rs2.getDouble("va5");
        
        MyFunc.genSql_d(currPrice, "price", "");
        MyFunc.genSql_d(vol, "vol", "");
        MyFunc.genSql_d(va5, "va5", "");
        diff_bt = Double.parseDouble(String.format("%.2f",
                (highP - currPrice) * 100 / currPrice));
        MyFunc.genSql_d(diff_bt, "diff_bt", "");//距高點幅度
        diff_bb = Double.parseDouble(String.format("%.2f",
                (currPrice - bot) * 100 / currPrice));
        MyFunc.genSql_d(diff_bb, "diff_bb", "");//距低點幅度
        MyFunc.genSql_d(Hdays, "hdays", "");//距高點天數
        MyFunc.genSql_d(Ldays, "ldays", "");//距高點前低點1天數
        isOk = false;
        if (nBoxType == DBOX && diff_bb >= -1*BB_DIST && diff_bb <= BB_DIST) {
            isOk = true;
        } else if (nBoxType == EBOX && diff_bt >= -1*BT_DIST && diff_bt <= BT_DIST) {
            isOk = true;
        }
        if (isOk) {
            MyFunc.genSqlEnd();
            sql2 = MyFunc.getInsertSql(tblName[nBoxType]);
            //System.out.println(sql3);
            if (stmt2.executeUpdate(sql2) == 0) {
                System.out.println("InsertRec() SQL INSERT ERROR!" + sql2);
                System.exit(-1);
            }
        }

    } // highP != 0

    /**
     * 建立某一天的EBOX資料(向上突破廂型)，為 ebox交易系統所使用
     *
     * @throws SQLException
     */
    void genBox_1(int nBoxType, java.util.Date dTheDate) throws SQLException {
        String sDteEnd, sDteBeg;
        String sql = null, sql2 = null, theId = null;
        int days[] = {120, 100};
        boolean more;
        Statement stmtId;
        ResultSet rsId;

        stmtId = oStk.conn.createStatement();
        if (nBoxType == EBOX) {
            sql2 = "select stockid from stk where dte = " + oStk.dateToStrCh(dTheDate)
                    + " and vol > va5 * 3 and price > ma20 and ma20 > ma60 "
                    + "and ma60 > ma120 and ma120 > ma240 and "
                    + "sc_ma20 > 0 and sc_ma60 > 0 and sc_ma120 > 0 and ud200 = 'U' and ptbrsi6 > 0.8";
        } else if (nBoxType == DBOX) {
            sql2 = "select stockid from stk where dte = " + oStk.dateToStrCh(dTheDate)
                    + " and ma5 < ma10 and ma10 < ma20 "
                    + "and sc_ma10 < 0 and sc_ma20 < 0 "
                    + "and kdk < kdd and sc_kdk < 0 and sc_kdd < 0 "
                    + "and rsi6 < rsi12 and sc_osc < 0 "
                    + "and price < ma5";
        } else {
            System.exit(-1);
        }
        stmt = oStk.conn.createStatement();
        stmt2 = oStk.conn.createStatement();
        sDteEnd = oStk.getPrevStockDate(dTheDate, 2);
        sDteBeg = oStk.getPrevStockDate(dTheDate, days[nBoxType]);
        rsId = stmtId.executeQuery(sql2);
        while (rsId.next()) {
            theId = rsId.getString("stockid");
            //System.out.println(theId);
            sql = String.format("SELECT * from stk where dte between %s and %s "
                    + "and stockid ='%s' order by dte",
                    oStk.padCh(sDteBeg), oStk.padCh(sDteEnd), theId);
            rs = stmt.executeQuery(sql);
            if (!rs.next()) {
                continue;
            }
            Init_WhenGrpChg();
            more = true;
            boxDate = dTheDate;
            while (true) {
                if (!more || (!rs.getString("stockid").equals(lastId))) {
                    if (highP != 0 && lowP != 0) {
                        InsertRec(nBoxType);
                        if (more) {
                            Init_WhenGrpChg();
                        }
                    }
                }
                if (!more) {
                    break;
                }
                if (rs.getDouble("p_low") < lowP) {
                    lowP = rs.getDouble("p_low");
                    lowDate = rs.getDate("dte");
                    lowVa10 = rs.getDouble("va10");
                    highP = 0d;
                } else if (rs.getDouble("p_high") > highP) {
                    highP = rs.getDouble("p_high");
                    highDate = rs.getDate("dte");
                    Hdays = 0;
                }
                Hdays = Hdays + 1;
                more = rs.next();
            } // while true
            rs.close();
        }//while rsId
    }

    /**
     * BOX資料必須逐日產生，首先清除期間內原有BOX資料， 然後依日期逐日呼叫gebox_1()
     *
     * @throws SQLException
     */
    void doGen() throws SQLException {
        ResultSet rsD;
        Statement stmtD;
        String sqlD;
        int i;
        java.util.Date theDte;
        System.out.printf("***** GenBox (%s - %s) *****\r\n", startDate, endDate);
        stmtD = oStk.conn.createStatement();
        for (i = 0; i < tblName.length; i++) {
            sqlD = String.format("delete from %s where dte between %s and %s",
                    tblName[i], oStk.padCh(startDate), oStk.padCh(endDate));
            stmtD.executeUpdate(sqlD);
        }
        sqlD = String.format("select dte from stk where stockid='1101' and dte between %s and %s order by dte",
                oStk.padCh(startDate), oStk.padCh(endDate));
        rsD = stmtD.executeQuery(sqlD);
        while (rsD.next()) {
            theDte = rsD.getDate("dte");
            System.out.printf("=== %s ===\r\n", oStk.dateToStr(theDte));
            genBox_1(EBOX, theDte);
            genBox_1(DBOX, theDte);
        }
        System.out.printf("***** GenBox Complete *****(%s~%s)\r\n", startDate, endDate);

    }

    public static void main(String[] args) {
        System.setErr(System.out);
        try {
            GenBox oo = new GenBox(args);
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
