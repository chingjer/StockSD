
import java.sql.*;

/**
 * 計算各種均線
 *
 * @author huangtm
 */
public final class CalcMa {

    Boolean isAll; // true-全部資料重新產生，false-只針對今天的資料
    StkDb oStk;
    Statement stmt;

    CalcMa() throws SQLException {
        isAll = false;
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
    }

    void calc_all_ma() throws SQLException {
        calc_days_ma("ma", "price", 5);
        calc_days_ma("ma", "price", 10);
        calc_days_ma("ma", "price", 20);
        calc_days_ma("ma", "price", 60);
        calc_days_ma("ma", "price", 120);
        calc_days_ma("ma", "price", 240);
        calc_days_ma("ma", "price", 200);

        calc_days_ma("va", "vol", 5);
        calc_days_ma("va", "vol", 10);
        calc_days_ma("va", "vol", 20);
    } // calc_all_ma

    /**
     * 計算一個均線
     *
     * @param Leading Leading+days=要計算的欄位名稱，如ma10,ma20...
     * @param sumFld 計算什麼欄位的均線，如price，指價格的均線：vol，指成交量的均線
     * @param days 天數
     */
    void calc_days_ma(String Leading, String sumFld, int days) throws SQLException {
        //--- 計算移動平均，(簡單)n日移動平均 MA_t = (P_t + P_[t-1] + P_[t-2] +…+ P_[t-n+1] ) / n
        //--- 使用Queue的技術來紀錄週期內每一天的價格
        ResultSet rs;
        int i, idxLast = 0;
        Double tot = 0d, ma = 0d;
        String tmpStockId, fldName, sWhere, sFromDate, sql;
        Double[] Que = new Double[days];
        Boolean noEnoughRecords = true;
        java.util.Date dCurrDate; // 資料庫中的最後一天
        Double oldValue;
        fldName = Leading + Integer.toString(days).trim();
        tmpStockId = "";
        dCurrDate = oStk.strToDate(oStk.currDate);
        sFromDate = oStk.getPrevStockDate(dCurrDate, days + 1 + oStk.tourdays);
        if (isAll) {
            sWhere = "";
        } else {
            sWhere = String.format(" WHERE dte > %s and dte <= %s",
                    oStk.padCh(sFromDate), oStk.padCh(oStk.currDate));
        }
        sql = "select stockid,dte,price,vol," + fldName
                + " from stk " + sWhere + " ORDER BY stockid,dte";
        //System.out.println(sql);
        stmt = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            if (!tmpStockId.equals(rs.getString("stockid"))) {
                for (i = 0; i < Que.length; i++) {
                    Que[i] = 0d;
                }
                tot = 0d;
                idxLast = 0;
                tmpStockId = rs.getString("stockid");
                noEnoughRecords = false;
                /**
                 * 代號變換時先讀進並處理前days-1天的資料 之後就每次只讀進一筆資料放到Que
                 */
                for (i = 0; i <= days - 2; i++) {
                    Que[idxLast] = rs.getDouble(sumFld);
                    tot = tot + Que[idxLast];
                    rs.next();
                    if (rs.isAfterLast() || !(rs.getString("stockid")).equals(tmpStockId)) {
                        noEnoughRecords = true;
                        break;
                    }
                    idxLast++;
                } // for
            } // if
            if (!noEnoughRecords) {
                tot = tot + rs.getDouble(sumFld);
                ma = tot / days;
                oldValue = rs.getDouble(fldName);
                if (isAll || oldValue == 0d) {
                    rs.updateDouble(fldName, ma);
                    rs.updateRow();
                }
                Que[idxLast] = rs.getDouble(sumFld);
                idxLast = (idxLast + 1) % days;
                tot = tot - Que[idxLast];
            }
        } // while
        rs.close();
        System.out.println("Calc " + fldName + " Done!");
    }

    /**
     * 執行方式: java CalcMa [isAll]
     *
     * @param args [] Y:產生全部，注意：將非常耗時
     */
    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** CalcMa() *****");
        try {
            CalcMa oMain = new CalcMa();
            if (args.length > 0 && args[0].equals("Y")) {
                oMain.isAll = true;
            }
            oMain.calc_all_ma();
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
