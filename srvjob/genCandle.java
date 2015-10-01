
import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * <h1>產生一段期間內的CandleStick資料</h1>
 * Usage: java genCandle dbname beg_dte end_dte<br>
 * beg_dte: 開始日期(如：2011/1/12)<br>
 * end_die 截止日期
 *
 * @author huangtm
 */
class genCandle {

    final int MAX_DAYS = CandleStick.MAX_NUM;
    StkDb oStk;
    Connection conn;
    BufferedWriter f_out;
    StringBuilder sql_b1 = new StringBuilder(300);
    StringBuilder sql_b2 = new StringBuilder(300);
    String file_trace = "candleStick.txt";
    String tbl_candle = "candleStick";

    String beg_dte = null;
    String end_dte = null;

    String sNow;
    String dbName;
    /**
     * 4天的資料，acs{0]為當天的資料, acs[1]為前一天 acs[n][POPEN]為開盤, acs[n][PCLOSE]為收盤
     */
    double acs[][] = new double[MAX_DAYS][5];
    CandleStick cdl = new CandleStick();

    genCandle(String args[]) {
        if (args.length < 1) {
            System.err.println("第1個參數必須是資料庫名稱(like:127.0.0.1/mystk)");
            System.exit(-1);
        }
        dbName = args[0];

        if (args.length < 3) {
            System.out.println("Usgae: java genCandle dbname begdte enddte");
            System.exit(-1);
        }
        beg_dte = args[1];
        end_dte = args[2];

    }

    protected stkRecord stk_rec = new stkRecord();

    /**
     * rs_stk 盤後資料檔目前紀錄值
     */
    class stkRecord {

        String stockId;
        java.util.Date dte;
        double price, p_high, p_low, p_open, updown, vol, sc_ma10;

        boolean moveNext(ResultSet rsStk) throws SQLException {
            boolean more;
            more = rsStk.next();
            if (more) {
                setValues(rsStk);
            }
            return more;
        }

        void setValues(ResultSet rsStk) throws SQLException {
            //rsStk.refreshRow();
            stockId = rsStk.getString("stockid");
            dte = rsStk.getDate("dte");
            price = rsStk.getDouble("price");
            p_high = rsStk.getDouble("p_high");
            p_low = rsStk.getDouble("p_low");
            p_open = rsStk.getDouble("p_open");
            updown = rsStk.getDouble("updown");
            vol = rsStk.getDouble("vol");
            sc_ma10 = rsStk.getDouble("sc_ma10");
        }
    }

    public void Start() throws SQLException, IOException {
        oStk = new StkDb(DbConfig.DB_TYPE, dbName, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
        conn = oStk.conn;
        f_out = new BufferedWriter(new FileWriter(file_trace));
        sNow = oStk.dateTimeToStr(new java.util.Date());
        debugPrint("作業開始 " + sNow, true);
        gen();
        sNow = oStk.dateTimeToStr(new java.util.Date());
        debugPrint("作業完畢！" + sNow + ", 測試報表為" + file_trace, true);
        f_out.close();
    }

    public void initStr(StringBuilder sb1) {
        if (sb1.length() != 0) {
            sb1.delete(0, sb1.length());
        }
    }

    void debugPrint(String s) throws IOException {
        debugPrint(s, false);
    }

    /**
     * 集中寫檔動作，以免 throws IOException滿天飛 isDisplay = true時，同時 System.out.println
     */
    void debugPrint(String s, boolean isDisplay) throws IOException {
        f_out.write(s);
        f_out.newLine();
        f_out.flush();
        if (isDisplay) {
            System.out.println(s);
        }
    }

    public void gen() throws SQLException, IOException {
        ResultSet rs_stk, rs_id;
        Statement stmt_id, stmt, stmtUpdate;
        String sDteCurr, s1, sql, stockId;
        int cnt;
        String kType, lastId, beg_dte2;
        java.util.Date dte;
        double acs_1[];
        double list1Ele[];
        ArrayList<double[]> list1 = new ArrayList<>();
        stmt_id = conn.createStatement();
        stmt = conn.createStatement();
        stmtUpdate = conn.createStatement();

        initStr(sql_b1);
        sql_b1.append(String.format(
                "delete from %s where dte between %s and %s",
                tbl_candle, oStk.padCh(beg_dte), oStk.padCh(end_dte)));
        stmt.executeUpdate(sql_b1.toString());
        dte = oStk.strToDate(beg_dte);
        beg_dte2 = oStk.getPrevStockDate(dte, MAX_DAYS);

        sql = "select stockid from stkid order by stockid";
        rs_id = stmt_id.executeQuery(sql);
        while (rs_id.next()) {
            stockId = rs_id.getString("stockid");
            initStr(sql_b1);
            sql_b1.append(String.format(
                    "select stockid,dte,p_open,p_high,p_low,price,updown, vol, sc_ma10 from stk "
                    + "where stockid ='%s' and dte between %s and %s "
                    + "order by dte", stockId, oStk.padCh(beg_dte2), oStk.padCh(end_dte)));
            //debugPrint(sql_b1.toString(), true);
            rs_stk = stmt.executeQuery(sql_b1.toString());
            lastId = "";
            list1.clear();
            //sNow = oStk.dateTimeToStr(new java.util.Date());
            //debugPrint(stockId + " --- Start at: " + sNow, true);
            while (stk_rec.moveNext(rs_stk)) {
                /*
                 if (!stk_rec.stockId.equals(lastId)) {
                 list1.clear();
                 lastId = stk_rec.stockId;
                 sNow = oStk.dateTimeToStr(new java.util.Date());
                 debugPrint(lastId + " --- Start at: " + sNow, true);
                 }
                 */
                acs_1 = new double[5];
                acs_1[CandleStick.POPEN] = stk_rec.p_open;
                acs_1[CandleStick.PHIGH] = stk_rec.p_high;
                acs_1[CandleStick.PLOW] = stk_rec.p_low;
                acs_1[CandleStick.PPRICE] = stk_rec.price;
                acs_1[CandleStick.SC_MA10] = stk_rec.sc_ma10;
                list1.add(acs_1);
                if (list1.size() < MAX_DAYS) {
                    continue;
                }
                dte = stk_rec.dte;
                sDteCurr = oStk.dateToStr(dte);
                for (int i = 0; i < MAX_DAYS; i++) {
                    list1Ele = list1.get(i);
                    System.arraycopy(list1Ele, 0, acs[MAX_DAYS - 1 - i], 0, list1Ele.length);
                }
                if (Math.abs(stk_rec.updown) > 7.1 || stk_rec.vol < 300 || stk_rec.price < 5) {
                    kType = "";
                } else {
                    kType = cdl.getKtype(acs);
                    System.gc();
                }
                if (!kType.equals("")) {
                    debugPrint(String.format("%s %s kType is %s", stk_rec.stockId, sDteCurr, kType));
                    initStr(sql_b2);
                    sql_b2.append(String.format(
                            "insert into %s (stockid,dte, ktype) values('%s',%s,'%s')",
                            tbl_candle, stk_rec.stockId, oStk.padCh(sDteCurr), kType));
                    if (stmtUpdate.executeUpdate(sql_b2.toString()) == 0) {
                        debugPrint("Insert 0 rec\r\n" + sql_b2.toString(), true);
                    }
                }
                list1.remove(0);
                System.gc();
            } //while stk_rec
        }//while rs_id

    }

    public static void main(String[] args) {
        try {
            genCandle oo = new genCandle(args);
            oo.Start();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.err.println("================================");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("IOException ");
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
