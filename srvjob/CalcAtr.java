
import java.sql.*;

/**
 * <h2>計算ATR (Average True Range)</h2>
 * H=高點，L=低點，PDC=昨日收盤，ATR=MAX(H-L, H-PDC, PDC-L)<br>
 * N=ATR= 20天TR平均值=179.65。(※這是暴跌大波動的時段)<br>
 * ATR簡易算法= (19*PDN+TR)/20 (PDN=作日N值，TR=今日真實區間)<p>
 *
 * @since 2015/9/13
 * @author huangtm
 */
public final class CalcAtr {

    Boolean isAll; // true-全部資料重新產生，false-只針對今天的資料
    StkDb oStk;
    Statement stmt;

    CalcAtr() throws SQLException {
        isAll = false;
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
    }

    void calc_all_atr() throws SQLException {
        CalcAtr1(20);
    }

    double max(double... n) {
        int i = 0;
        double max = n[i];

        while (++i < n.length) {
            if (n[i] > max) {
                max = n[i];
            }
        }

        return max;
    }

    double getTr(double pdc, ResultSet rs) throws SQLException {
        double p_high, p_low, H_L, H_PDC, PDC_L, trueRange = -1;
        p_high = rs.getDouble("p_high");
        p_low = rs.getDouble("p_low");
        H_L = p_high - p_low;
        H_PDC = Math.abs(p_high - pdc);
        PDC_L = Math.abs(pdc - p_low);
        trueRange = max(H_L, H_PDC, PDC_L);
        return trueRange;
    }

    @SuppressWarnings("UnusedAssignment")
    void CalcAtr1(int days)
            throws SQLException {
        ResultSet rs;
        int i, idxLast = 0;
        Double tot = 0d, ma = 0d;
        String tmpId, sWhere, sFromDate, sql;
        Double[] QueTR = new Double[days];
        Boolean noEnoughRecords = true;
        java.util.Date dCurrDate; // 資料庫中的最後一天
        Double tmpVal = 0d;
        double pdc = 0d;//前日收盤

        tmpId = "";
        dCurrDate = oStk.strToDate(oStk.currDate);
        // days+1 指必須讀取前一天的pdc(收盤價)
        sFromDate = oStk.getPrevStockDate(dCurrDate, days + 1 + oStk.tourdays);
        if (isAll) {
            sWhere = "";
        } else {
            // 多讀取一天以取得前日收盤pdc
            sWhere = String.format(" WHERE dte >= %s and dte <= %s",
                    oStk.padCh(sFromDate), oStk.padCh(oStk.currDate));
        }
        sql = "select stockid,dte,p_high,p_low,price,tr";
        sql = sql + " from stk " + sWhere + " ORDER BY stockid,dte";
        //sql = sql + " from stk " + sWhere + " AND stockid='2330' ORDER BY stockid,dte";
        stmt = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            if (!tmpId.equals(rs.getString("stockid"))) {
                for (i = 0; i < QueTR.length; i++) {
                    QueTR[i] = 0d;
                }
                tot = 0d;
                idxLast = 0;
                tmpId = rs.getString("stockid");
                noEnoughRecords = false;
                pdc = rs.getDouble("price");

                if (!rs.next()) { //第一筆只用來得到pdc(前日收盤價)
                    noEnoughRecords = true;
                } else {
                    for (i = 0; i <= days - 2; i++) {//讀進前面days-1天
                        QueTR[idxLast] = getTr(pdc, rs);
                        tot = tot + QueTR[idxLast];
                        //System.out.printf("(%d)%s pdc = %.2f, price = %.2f tot=%.2f \n", 
                        //        idxLast, rs.getDate("dte"), pdc, rs.getDouble("price"), tot);
                        pdc = rs.getDouble("price");
                        rs.next();
                        if (rs.isAfterLast() || !(rs.getString("stockid")).equals(tmpId)) {
                            noEnoughRecords = true;
                            break;
                        }
                        idxLast = idxLast + 1;
                    } // for
                }

            } // if !tmpId.equals(
            if (!noEnoughRecords) {
                QueTR[idxLast] = getTr(pdc, rs);
                tot = tot + QueTR[idxLast];
                ma = tot / days;
                //System.out.printf("(%d)%s pdc = %.2f, price = %.2f tot=%.2f average=%.2f \n", 
                //        idxLast, rs.getDate("dte"), pdc, rs.getDouble("price"), tot, ma);
                tmpVal = rs.getDouble("tr");
                if (rs.wasNull()) {
                    tmpVal = 0d;
                }
                if (isAll || tmpVal == 0d) {
                    rs.updateDouble("tr", ma);
                    rs.updateRow();
                }
                i = (idxLast + 1) % days; //最後一個的下一個就是第一個
                tot = tot - QueTR[i];
                idxLast = (idxLast + 1) % days;

                pdc = rs.getDouble("price");
            }//if (!noEnoughRecords)
        } // while
        rs.close();
        System.out.println("Calc ATR " + days + " Done!");
    }
/**
 * 執行方式: java CalcAtr [isAll]<br> 
 * @param args isall-- Y:產生全部，注意：將非常耗時, N=快速產生(近日)
 */
    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** CalcAtr() *****");
        try {
            CalcAtr oo = new CalcAtr();
            if (args.length > 0 && args[0].equals("Y")) {
                oo.isAll = true;
            }
            oo.calc_all_atr();
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
