
import java.lang.*;
import java.util.*;
import java.math.*;
import java.io.*;
import java.sql.*;

/**
 * ******************************************************************
 * 計算 KD9 *******************************************************************
 */
public final class CalcRsi {

    Boolean isAll; // true-全部資料重新產生，false-只針對今天的資料
    StkDb oStk;
    Statement stmt;

    CalcRsi() throws SQLException {
        isAll = false;
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
    }

    void calc_all_rsi() throws SQLException {
        CalcRsi1(6);
        CalcRsi1(12);
    }

    void CalcRsi1(int days) throws SQLException {
        ResultSet rs;
        int i;
        String tmpStockId, sWhere, sFromDate, sql;

        java.util.Date dCurrDate; // 資料庫中的最後一天
        Double dec = 0d;

        Double prevPrice = 0d, price;

        Boolean noEnoughRecords = true;
        Double nRSI;
        Double totU = 0d, totD = 0d, Dif = 0d;

        Double emaU_1 = 0d, emaD_1 = 0d, emaU = 0d, emaD = 0d;
        String fldRSI, fldEmaU, fldEmaD;

        tmpStockId = "";
        fldRSI = "RSI" + days;
        fldEmaU = "EMAU" + days;
        fldEmaD = "EMAD" + days;

        dCurrDate = oStk.strToDate(oStk.currDate);
        sFromDate = oStk.getPrevStockDate(dCurrDate, days + 2 + oStk.tourdays);
        if (isAll) {
            sWhere = "";
        } else {
            sWhere = String.format(" WHERE dte > %s and dte <= %s",
                    oStk.padCh(sFromDate), oStk.padCh(oStk.currDate));
        }
        sql = "select stockid,dte,price," + fldRSI + "," + fldEmaU + "," + fldEmaD
                + " from stk " + sWhere + " ORDER BY stockid,dte";
        stmt = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            if (!tmpStockId.equals(rs.getString("stockid"))) {
                totU = 0d;
                totD = 0d;
                emaU_1 = 0d;
                emaD_1 = 0d;
                emaU = 0d;
                emaD = 0d;

                tmpStockId = rs.getString("stockid");
                noEnoughRecords = false;
                prevPrice = rs.getDouble("price"); // 第1筆只讀取作為計算漲跌
                rs.next();
                if (rs.isAfterLast() || !(rs.getString("stockid")).equals(tmpStockId)) {
                    noEnoughRecords = true;
                } else {
                    for (i = 0; i <= days - 2; i++) {
                        price = rs.getDouble("price");
                        Dif = price - prevPrice;
                        if (Dif >= 0d) {
                            totU = totU + Dif;
                        } else {
                            totD = totD + -1d * Dif;
                        }

                        prevPrice = price;
                        dec = rs.getDouble(fldEmaU);
                        if (isAll || dec == 0d) {
                            emaU_1 = -1d;
                            emaD_1 = -1d;
                        } else {
                            emaU_1 = rs.getDouble(fldEmaU);
                            emaD_1 = rs.getDouble(fldEmaD);
                        }

                        rs.next();
                        if (rs.isAfterLast() || !(rs.getString("stockid")).equals(tmpStockId)) {
                            noEnoughRecords = true;
                            break;
                        }
                    } // for
                } // else
            } // if (!tmpStockId.equals

            if (!noEnoughRecords) {
                price = rs.getDouble("price");
                Dif = price - prevPrice;
                if (Dif >= 0d) {
                    totU = totU + Dif;
                } else {
                    totD = totD + -1d * Dif;
                }
                if (emaU_1 == -1d) {
                    emaU = totU / days;
                    emaD = totD / days;
                } else {
                    if (Dif >= 0d) {
                        emaU = emaU_1 * (days - 1d) / days + Dif / days;
                        emaD = emaD_1 * (days - 1d) / days;
                    } else {
                        emaD = emaD_1 * (days - 1d) / days + -1d * Dif / days;
                        emaU = emaU_1 * (days - 1d) / days;
                    }
                }
                if (emaD == 0d) {
                    nRSI = 100d;
                } else {
                    nRSI = 100d - (100d / (1d + (emaU / emaD)));
                }

                dec = rs.getDouble(fldRSI);
                if (isAll || dec == 0d) {
                    rs.updateDouble(fldRSI, nRSI);
                    rs.updateDouble(fldEmaU, emaU);
                    rs.updateDouble(fldEmaD, emaD);
                    rs.updateRow();
                    emaU_1 = emaU;
                    emaD_1 = emaD;
                } else {
                    emaU_1 = rs.getDouble(fldEmaU);
                    emaD_1 = rs.getDouble(fldEmaD);
                }

                prevPrice = price;
            } //if (!noEnoughRecords)				
        } // while
        rs.close();
        System.out.println("Calc " + fldRSI + " Done!");
    }

    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** CalcRsi() *****");
        try {
            CalcRsi oo = new CalcRsi();
            if (args.length > 0 && args[0].equals("Y")) {
                oo.isAll = true;
            }
            oo.calc_all_rsi();
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
