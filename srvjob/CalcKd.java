
import java.sql.*;

/**
 * 計算 KD9
 *
 * @author huangtm
 */
public final class CalcKd {

    Boolean isAll; // true-全部資料重新產生，false-只針對今天的資料
    StkDb oStk;
    Statement stmt;

    CalcKd() throws SQLException {
        isAll = false;
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
    }

    void calc_all_kd() throws SQLException {
        CalcKd1(9);
    } // calc_all_kd

    void CalcKd1(int days) throws SQLException {
        ResultSet rs;
        int i, idxLast = 0;
        String tmpId, sWhere, sFromDate, sql;
        String Fldnm_K, Fldnm_D;
        Double[] QueHigh = new Double[days];
        Double[] QueLow = new Double[days];
        Boolean noEnoughRecords = true;
        java.util.Date dCurrDate; // 資料庫中的最後一天
        Double oldValue;
        Double prevK, prevD, maxHigh, minLow, rsv, price;
        Double kd_k, kd_d;

        prevK = 0d;
        prevD = 0d;
        if (days == 9) {
            Fldnm_K = "KDK";
            Fldnm_D = "KDD";
        } else {
            Fldnm_K = "K" + days;
            Fldnm_D = "D" + days;
        }

        tmpId = "";
        dCurrDate = oStk.strToDate(oStk.currDate);
        sFromDate = oStk.getPrevStockDate(dCurrDate, days + 1 + oStk.tourdays);
        if (isAll) {
            sWhere = "";
        } else {
            sWhere = String.format(" WHERE dte > %s and dte <= %s",
                    oStk.padCh(sFromDate), oStk.padCh(oStk.currDate));
        }
        sql = "select stockid,dte,p_high,p_low,price,kdk,kdd from stk "
                + sWhere + " ORDER BY stockid,dte";

        stmt = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            if (!tmpId.equals(rs.getString("stockid"))) {
                for (i = 0; i < QueHigh.length; i++) {
                    QueHigh[i] = 0d;
                    QueLow[i] = 0d;
                }
                prevK = 50d; //第1筆的Kt-1設為50
                prevD = 50d;
                idxLast = 0;
                tmpId = rs.getString("stockid");

                //System.out.println("stockid=" + tmpId);
                noEnoughRecords = false;
                for (i = 0; i <= days - 2; i++) {
                    QueHigh[idxLast] = rs.getDouble("p_high");
                    QueLow[idxLast] = rs.getDouble("p_low");
                    if (!isAll) {
                        //oldValue = rs.getDouble(Fldnm_K);
                        if (!rs.wasNull()) {
                            prevK = rs.getDouble(Fldnm_K);
                            prevD = rs.getDouble(Fldnm_D);
                        } else {
                            prevK = 0d;
                            prevD = 0d;
                        }
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
                QueHigh[idxLast] = rs.getDouble("p_high");
                QueLow[idxLast] = rs.getDouble("p_low");
                maxHigh = QueHigh[0];
                minLow = QueLow[0];
                for (i = 0; i < QueHigh.length; i++) {
                    if (QueHigh[i] > maxHigh) {
                        maxHigh = QueHigh[i];
                    }
                    if (QueLow[i] < minLow) {
                        minLow = QueLow[i];
                    }
                } // for
                price = rs.getDouble("price");
                if (maxHigh.equals(minLow) || price == 0.0d) {
                    rsv = 100.0d;
                } else {
                    rsv = (price - minLow) / (maxHigh - minLow) * 100;
                }
                kd_k = (rsv / 3) + (prevK * 2 / 3);
                kd_d = (kd_k / 3) + (prevD * 2 / 3);

                oldValue = rs.getDouble(Fldnm_K);
                if (isAll || oldValue == 0d) {
                    rs.updateDouble(Fldnm_K, kd_k);
                    rs.updateDouble(Fldnm_D, kd_d);
                    rs.updateRow();
                }
                prevK = kd_k;
                prevD = kd_d;

                idxLast = (idxLast + 1) % days;
            }
        } // while

        rs.close();

        System.out.println(
                "Calc KD" + days + " Done!");
    }

    /**
     * 執行方式: java CalcKd [isAll]
     *
     * @param args [] Y:產生全部，注意：將非常耗時
     */
    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** CalcKd() *****");
        try {
            CalcKd oMain = new CalcKd();
            if (args.length > 0 && args[0].equals("Y")) {
                oMain.isAll = true;
            }
            oMain.calc_all_kd();
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
