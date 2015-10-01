
import java.sql.*;
import java.io.*;

/**
 * <h1>產生一天的CandleStick資料</h1>
 * Usage: java genCandleDay dbname [dte]<br>
 * dte: 沒有的時候則為SYSPARM中的currDate
 * @author huangtm
 */
class genCandleDay {

    StkDb oStk;
    Connection conn;
    BufferedWriter f_out;
    StringBuilder sql_b1 = new StringBuilder(300);
    StringBuilder sql_b2 = new StringBuilder(300);
    String file_trace = "candleStick.txt";
    String tbl_candle = "candleStick";
    String beg_dte, end_dte;
    /**
     * 4天的資料，acs{0]為當天的資料, acs[1]為前一天 acs[n][POPEN]為開盤, acs[n][PCLOSE]為收盤
     */
    double acs[][] = new double[CandleStick.MAX_NUM][5];

    CandleStick cdl = new CandleStick();
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

    public void Start(String[] args) throws SQLException, IOException {
        String dbName;
        if (args.length < 1) {
            System.err.println("第1個參數必須是資料庫名稱(like:127.0.0.1/mystk)");
            System.exit(-1);
        }
        dbName = args[0];
        String sNow = "";
        oStk = new StkDb(DbConfig.DB_TYPE, dbName, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
        conn = oStk.conn;
        f_out = new BufferedWriter(new FileWriter(file_trace));
        if (args.length > 1) {
            sNow = args[1];
        } else {
            sNow = oStk.currDate;
            //sNow = StockDB.dateTimeToStr(new java.util.Date(),"/");
        }
        debugPrint("作業開始，日期為：" + sNow, true);
        //beg_dte = "2010/5/6";	end_dte = "2010/5/6";
        beg_dte = sNow;
        end_dte = sNow;
        gen();

        System.out.println("作業完畢！測試報表為" + file_trace);
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
        ResultSet rs_stk, rs2;
        Statement stmt, stmt2, stmtUpdate;
        String stockId, sDteCurr, sDtePrev;
        int cnt;
        String kType;
        java.util.Date dte;

        stmt = conn.createStatement();
        stmt2 = conn.createStatement();
        stmtUpdate = conn.createStatement();

        initStr(sql_b1);
        sql_b1.append(String.format(
                "delete from %s where dte between %s and %s",
                tbl_candle, oStk.padCh(beg_dte), oStk.padCh(end_dte)));
        stmt.executeUpdate(sql_b1.toString());

        initStr(sql_b1);
        sql_b1.append(String.format(
                "select stockid, dte from stk where dte between %s and %s "
                + "order by stockid,dte", oStk.padCh(beg_dte), oStk.padCh(end_dte)));
        //debugPrint(sql_b1.toString(), true);
        rs_stk = stmt.executeQuery(sql_b1.toString());
        while (rs_stk.next()) {
            stockId = rs_stk.getString("stockid");
            dte = rs_stk.getDate("dte");
            for (int i = 0; i < CandleStick.MAX_NUM; i++) {
                for (int j = 0; j <= 3; j++) {
                    acs[i][j] = -1d;
                }
            }
            sDteCurr = oStk.dateToStr(dte);
            sDtePrev = oStk.getPrevStockDate(dte, CandleStick.MAX_NUM);
            initStr(sql_b2);
            sql_b2.append(String.format(
                    "select stockid,dte,p_open,p_high,p_low,price,updown,vol,sc_ma10 from stk "
                    + "where stockid='%s' and dte between %s and %s order by dte desc",
                    stockId, oStk.padCh(sDtePrev), oStk.padCh(sDteCurr)));
            //debugPrint(sql_b2.toString(), true);
            rs2 = stmt2.executeQuery(sql_b2.toString());
            cnt = 0;
            while (stk_rec.moveNext(rs2)) {
                acs[cnt][CandleStick.POPEN] = stk_rec.p_open;
                acs[cnt][CandleStick.PHIGH] = stk_rec.p_high;
                acs[cnt][CandleStick.PLOW] = stk_rec.p_low;
                acs[cnt][CandleStick.PPRICE] = stk_rec.price;
                acs[cnt][CandleStick.SC_MA10] = stk_rec.sc_ma10;
                cnt++;
            }
            if (cnt != CandleStick.MAX_NUM) {
                //debugPrint(stockId+" not have 4 days!", true);
                continue;
            }
            //--- 根據acs[][] 開始判斷 K線型態 --------
            if (Math.abs(stk_rec.updown) > 7.1 || stk_rec.vol < 300 || stk_rec.price < 5) {
                kType = "";
            } else {
                kType = cdl.getKtype(acs);
            }
            if (!kType.equals("")) {
                debugPrint(String.format("%s %s kType is %s", stockId, sDteCurr, kType), true);
                initStr(sql_b2);
                sql_b2.append(String.format(
                        "insert into %s (stockid,dte, ktype) values('%s',%s,'%s')",
                        tbl_candle, stockId, oStk.padCh(sDteCurr), kType));
                if (stmtUpdate.executeUpdate(sql_b2.toString()) == 0) {
                    debugPrint("Insert 0 rec\r\n" + sql_b2.toString(), true);
                }
            }

            rs2.close();
            rs2 = null;
            System.gc();
        } //while
    }

    public static void main(String[] args) {
        try {
            genCandleDay obj = new genCandleDay();
            obj.Start(args);
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
