
import java.sql.*;

public final class CalcMfi {

    Boolean isAll; // true-全部資料重新產生，false-只針對今天的資料
    StkDb oStk;
    Statement stmt;

    CalcMfi() throws SQLException {
        isAll = false;
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
    } // 

    Double getPt(ResultSet rs) {
        Double p_high, p_low, price, pt;
        pt = 0d;
        try {
            p_high = rs.getDouble("p_high");
            p_low = rs.getDouble("p_low");
            price = rs.getDouble("price");
            pt = (p_high + p_low + price) / 3;
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            System.exit(-1);
        }
        return pt;
    }

    void calc_all_mfi() throws SQLException {
        CalcMfi1(14);
    }

    void CalcMfi1(int days) throws SQLException {
        ResultSet rs;
        int i, j, idxLast = 0;
        String tmpId, sWhere, sFromDate, sql;
        Double[] QueP = new Double[days]; // Queue of Price
        Double[] QueV = new Double[days]; // Queue of Volumn
        Double[] QueS = new Double[days]; // Queue of Sign (漲跌)
        Boolean noEnoughRecords = true;
        java.util.Date dCurrDate; // 資料庫中的最後一天
        Double dec = 0d;

        Double prevPrice = 0d, totPositive, totNegative, nMFI, tt, price;

        tmpId = "";
        dCurrDate = oStk.strToDate(oStk.currDate);
        sFromDate = oStk.getPrevStockDate(dCurrDate, days + 2 + oStk.tourdays);
        if (isAll) {
            sWhere = "";
        } else {
            sWhere = String.format(" WHERE dte > %s and dte <= %s",
                    oStk.padCh(sFromDate), oStk.padCh(oStk.currDate));
        }
        sql = "select stockid,dte,price,p_high,p_low,vol,mfi from stk "
                + sWhere + " ORDER BY stockid,dte";

        stmt = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            if (!tmpId.equals(rs.getString("stockid"))) {
                for (i = 0; i < QueP.length; i++) {
                    QueP[i] = 0d;
                    QueV[i] = 0d;
                    QueS[i] = 0d;
                }

                idxLast = 0;
                j = 0;
                tmpId = rs.getString("stockid");
                noEnoughRecords = false;
                /*
                Access系統這裡是 prevPrice=0, 可能會影響第一筆的正負值
                */
                //prevPrice = rs.getDouble("price"); // 這裡也是錯的
                prevPrice = getPt(rs);
                
                rs.next(); 
                if (rs.isAfterLast() || !(rs.getString("stockid")).equals(tmpId)) {
                    noEnoughRecords = true;
                } else {
                    for (i = 0; i <= days - 2; i++) {
                        //System.out.println("dte= " + rs.getDate("dte")+","+(++j)+",pt="+getPt(rs));
                        price = rs.getDouble("price");
                        QueP[idxLast] = getPt(rs);
                        QueV[idxLast] = rs.getDouble("vol");
                        QueS[idxLast] = QueP[idxLast] - prevPrice;
                        prevPrice = QueP[idxLast];
                        rs.next();
                        if (rs.isAfterLast() || !(rs.getString("stockid")).equals(tmpId)) {
                            noEnoughRecords = true;
                            break;
                        }
                        idxLast = idxLast + 1;
                    } // for
                } // else
            } // if (!tmpId.equals

            if (!noEnoughRecords) {

                price = rs.getDouble("price");
                QueP[idxLast] = getPt(rs);
                QueV[idxLast] = rs.getDouble("vol");
                QueS[idxLast] = QueP[idxLast] - prevPrice;
                prevPrice = QueP[idxLast];

                totPositive = 0d;
                totNegative = 0d;

                for (i = 0; i < QueP.length; i++) {
                    if (QueS[i] >= 0) {
                        //System.out.println("+ P="+(QueP[i] * QueV[i]));
                        totPositive += (QueP[i] * QueV[i]);
                    } else {
                        //System.out.println("- P="+(QueP[i] * QueV[i]));
                        totNegative += (QueP[i] * QueV[i]);
                    }
                }

                //System.out.println("totPositive " + totPositive);
                //System.out.println("totNegative " + totNegative);
                if (totNegative == 0d) {
                    nMFI = 100d;
                } else {
                    nMFI = 100 - 100 / (1 + (totPositive / totNegative));
                }

                //System.out.println("MFI= " + nMFI );
                dec = rs.getDouble("MFI");
                if (isAll || dec == 0d) {
                    rs.updateDouble("MFI", nMFI);
                    rs.updateRow();
                }

                idxLast = (idxLast + 1) % days;
            }
        } // while
        rs.close();
        System.out.println("Calc MFI " + days + " Done!");

    }

    /**
     * 執行方式: java CalcMfi [isAll]
     *
     * @param args [] Y:產生全部，注意：將非常耗時
     */
    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** CalcMfi() *****");
        try {
            CalcMfi oo = new CalcMfi();
            if (args.length > 0 && args[0].equals("Y")) {
                oo.isAll = true;
            }
            oo.calc_all_mfi();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.err.println("================================");
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

    }
}
