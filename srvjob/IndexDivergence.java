
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <h1>計算各種指標背離</h1>
 * usage: java IndexDivergence dbname index-field [startDate endDate]<br>
 * 當沒有參數時則為stk的最後一天 dbname : like 127.0.0.1/mystk
 *
 * @author huangtm
 */
class IndexDivergence {

    final int IndHi_1 = 0; //index高點1
    final int IndHi_2 = 1; //index高點2
    final int IndLo_1 = 2; //index低點1
    final int IndLo_2 = 3; //index低點2
    final int PrHi_1 = 4; //Price高點1
    final int PrHi_2 = 5; //Price高點2
    final int PrLo_1 = 6; //Price低點1
    final int PrLo_2 = 7; //Price低點2
    final int FLAG_3 = 8;
    final int P_HIGH = 9; //最高價
    final int P_LOW = 10; //最低價
    final int idxVal = 11; //指標 SHORT
    final int idxVal2 = 12; //指標 LONG

    StkDb oStk;
    Statement stmt, stmt2;
    ResultSet rs;
    String startDate, endDate;
    String stockid;
    java.util.Date dte;
    int currFlag;
    String idxFld;

    double[] vv = new double[17];
    boolean isDebug, isAction;
    double hiP, loP, revH, revL;

    IndexDivergence(String args[]) throws SQLException {
        String dbName;
        if (args.length < 2) {
            System.err.println("Usage:java IndexDivergence dbname index-field [startDate endDate]");
            System.exit(-1);
        }
        dbName = args[0];

        oStk = new StkDb(DbConfig.DB_TYPE, dbName, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
        idxFld = args[1];
        if (args.length < 4) {
            endDate = oStk.currDate;
            startDate = oStk.getPrevStockDate(oStk.strToDate(endDate), 40);
        } else {
            startDate = args[2];
            endDate = args[3];
        }
        isDebug = false;

    }

    public void start() throws SQLException {
        String LastId;
        int i;
        String sql;
        double pStop;
        String typ, sWhere = "";
        ResultSet rs3;
        Statement stmt3;
        if (idxFld.contains("rsi")) {
            hiP = 80.0;
            loP = 20.0;
            revH = 65.0;
            revL = 35.0;
        } else if (idxFld.contains("kdk")) {
            hiP = 90.0;
            loP = 10.0;
            revH = 65.0;
            revL = 35.0;
        } else {
            System.err.println("IdxFld must be kdk, rsi6,rsi12.");
            System.exit(-1);
        }
        stmt = oStk.conn.createStatement();
        stmt2 = oStk.conn.createStatement();
        stmt3 = oStk.conn.createStatement();

        //sql = "delete from divergence where idx='" + idxFld + "'";
        //stmt.executeUpdate(sql);
        if (isDebug) {
            sWhere = " AND stockid in ('2330')";
        }
        sql = String.format("select * from stk WHERE dte between %s AND %s " + sWhere
                + " order by stockid,dte", oStk.padCh(startDate), oStk.padCh(endDate));
        System.out.println(sql);
        rs = stmt.executeQuery(sql);
        LastId = "xxxx";
        while (rs.next()) {
            stockid = rs.getString("stockid");
            if (!stockid.equals(LastId)) {
                initData();
                LastId = stockid;
            }// if
            vv[idxVal] = rs.getDouble(idxFld);
            vv[P_HIGH] = rs.getDouble("p_high");
            vv[P_LOW] = rs.getDouble("p_low");
            dte = rs.getDate("dte");

            CalcDivergence();

            if (currFlag == -5 || currFlag == 5) {
                if (currFlag == -5) {
                    pStop = vv[PrLo_2] - 0.1;
                    typ = "+";
                } else {
                    pStop = vv[PrHi_2] + 0.1;
                    typ = "-";
                }
                sql = String.format(
                        "select * from divergence where "
                        + "typ='%s' and idx='%s' and stockid='%s' and dte =%s",
                        typ, idxFld, stockid, oStk.dateToStrCh(dte));
                rs3 = stmt3.executeQuery(sql);
                if (!rs3.next()) {
                    sql = String.format(
                            "insert into divergence(typ,idx,stockid,dte,price,p_stop,vol) values("
                            + "'%s','%s','%s',%s,%.2f,%.2f,%.0f)",
                            typ, idxFld, stockid, oStk.dateToStrCh(dte), rs.getDouble("price"),
                            pStop, rs.getDouble("vol"));
                    //System.out.println(sql);
                    stmt2.executeUpdate(sql);
                }
                initData();
            }
        }//while
        System.out.printf("***** IndexDivergence Complete *****(%s~%s)\r\n", startDate, endDate);

    }//start()

    private void initData() {
        int i;
        currFlag = 0;
        for (i = 0; i < vv.length; i++) {
            vv[i] = 0.0;
        }//for

    }

    void debugIt(String spec, double val) {
        if (isDebug) {
            isAction = true;
            System.out.printf("%s %s (%d), %.2f [%s]\n",
                    stockid, oStk.dateToStr(dte), currFlag, val, spec);
        }
    }

    void CalcDivergence() {
        isAction = false;
        // ----- index突破80 flag設為1
        if (vv[idxVal] >= hiP && currFlag != 1) {
            currFlag = 1;
            vv[IndHi_1] = vv[idxVal];
            vv[PrHi_1] = vv[P_HIGH];
            debugIt("空頭背離開始", vv[idxVal]);
            return;
        }
        if (vv[idxVal] <= loP && currFlag != -1) {
            currFlag = -1;
            vv[IndLo_1] = vv[idxVal];
            vv[PrLo_1] = vv[P_LOW];
            debugIt("多頭背離開始", vv[idxVal]);
            return;
        }
        // ----- flag == 1開始追蹤最高價與最高index
        if (currFlag == 1) {
            if (vv[IndHi_1] < vv[idxVal]) {
                vv[IndHi_1] = vv[idxVal];
                debugIt("CHG HIGH1 Idx", vv[idxVal]);
            }
            if (vv[PrHi_1] < vv[P_HIGH]) {
                vv[PrHi_1] = vv[P_HIGH];
                debugIt("**CHG HIGH1 Price", vv[PrHi_1]);
            }
        }
        if (currFlag == -1) {
            if (vv[IndLo_1] > vv[idxVal]) {
                vv[IndLo_1] = vv[idxVal];
                debugIt("CHG LOW1 Idx", vv[idxVal]);
            }
            if (vv[PrLo_1] > vv[P_LOW]) {
                vv[PrLo_1] = vv[P_LOW];
                debugIt("**CHG LOW1 Price", vv[PrLo_1]);
            }
        }
        // ----- index跌破80 flag設為2
        if (currFlag == 1 && vv[idxVal] < hiP) {
            currFlag = 2;
            debugIt("chg to FLAG2", vv[idxVal]);
            return;
        }
        if (currFlag == -1 && vv[idxVal] > loP) {
            currFlag = -2;
            debugIt("chg to FLAG2", vv[idxVal]);
            return;
        }
        // ----- flag==2又跌破65，flag設為3
        if (currFlag == 2 && vv[idxVal] < revH) {
            currFlag = 3;
            vv[FLAG_3] = vv[idxVal];
            debugIt("chg to FLAG3", vv[idxVal]);
            return;
        }
        if (currFlag == -2 && vv[idxVal] > revL) {
            currFlag = -3;
            vv[FLAG_3] = vv[idxVal];
            debugIt("chg to FLAG3", vv[idxVal]);
            return;
        }
        // ----- flag==3時開始追蹤index最低值,記錄在vv[FLAG_3]
        if (currFlag == 3 && vv[idxVal] < vv[FLAG_3]) {
            vv[FLAG_3] = vv[idxVal];
            debugIt("Flag3's low index", vv[idxVal]);
        }
        if (currFlag == -3 && vv[idxVal] > vv[FLAG_3]) {
            vv[FLAG_3] = vv[idxVal];
            debugIt("Flag3's high index", vv[idxVal]);
        }
        // ----- flag==3且股價越過第一個高點(flag==1追蹤的)變成4
        if (currFlag == 3 && vv[P_HIGH] >= vv[PrHi_1]) {
            currFlag = 4;
            vv[PrHi_2] = vv[P_HIGH];
            vv[IndHi_2] = vv[idxVal];
            debugIt("chg to Flag4", vv[idxVal]);
            return;
        }
        if (currFlag == -3 && vv[P_LOW] >= vv[PrLo_1]) {
            currFlag = -4;
            vv[PrLo_2] = vv[P_LOW];
            vv[IndLo_2] = vv[idxVal];
            debugIt("chg to Flag-4", vv[idxVal]);
            return;
        }
        // ----- flag ==4時追蹤第二個高點
        if (currFlag == 4) {
            if (vv[P_HIGH] > vv[PrHi_2]) {
                vv[PrHi_2] = vv[P_HIGH];
            }
            if (vv[idxVal] > vv[IndHi_2]) {
                vv[IndHi_2] = vv[idxVal];
            }
        }
        if (currFlag == -4) {
            if (vv[P_LOW] < vv[PrLo_2]) {
                vv[PrLo_2] = vv[P_LOW];
            }
            if (vv[idxVal] < vv[IndLo_2]) {
                vv[IndLo_2] = vv[idxVal];
            }
        }
        // -----
        if (currFlag == 4 && (vv[IndHi_2] > vv[IndHi_1] || vv[IndHi_2] > hiP)) {
            /*
             currFlag = 1;
             vv[IndHi_1] = vv[IndHi_2];
             if (vv[PrHi_2] > vv[PrHi_1]) {
             vv[PrHi_1] = vv[PrHi_2];
             }
             */
            currFlag = 1;
            vv[IndHi_1] = vv[idxVal];
            vv[PrHi_1] = vv[P_HIGH];

            debugIt("chg Flag 4->1", vv[idxVal]);
            return;
        }
        if (currFlag == -4 && (vv[IndLo_2] < vv[IndLo_1] || vv[IndLo_2] < loP)) {
            /*
             currFlag = -1;
             vv[IndLo_1] = vv[IndLo_2];
             if (vv[PrLo_2] < vv[PrLo_1]) {
             vv[PrLo_1] = vv[PrLo_2];
             }
             */
            currFlag = -1;
            vv[IndLo_1] = vv[idxVal];
            vv[PrLo_1] = vv[P_LOW];

            debugIt("chg Flag -4->-1", vv[idxVal]);
            return;
        }

        //----- 完成 -----
        if (currFlag == 4 && vv[idxVal] < vv[FLAG_3]
                && vv[IndHi_1] > vv[IndHi_2] && vv[PrHi_1] <= vv[PrHi_2]) {
            currFlag = 5;
            debugIt("chg to Flag5", vv[idxVal]);
            return;

        }
        if (currFlag == -4 && vv[idxVal] > vv[FLAG_3]
                && vv[IndLo_1] < vv[IndLo_2] && vv[PrLo_1] >= vv[PrLo_2]) {
            currFlag = -5;
            debugIt("chg to Flag-5", vv[idxVal]);
            return;
        }
        //-----
        if (currFlag == 5 && vv[P_HIGH] > vv[PrHi_2]) {
            vv[PrHi_1] = vv[PrHi_2];
            vv[PrHi_2] = vv[P_HIGH];
            currFlag = 4;
            debugIt("chg 5->4", vv[idxVal]);
            return;
        }
        if (currFlag == -5 && vv[P_LOW] < vv[PrLo_2]) {
            vv[PrLo_1] = vv[PrLo_2];
            vv[PrLo_2] = vv[P_LOW];
            currFlag = -4;
            debugIt("chg -5->-4", vv[idxVal]);
            return;
        }
        if (!isAction) {
            debugIt("*no Action*", vv[idxVal]);
        }

    }//CalcDivergence

    public static void main(String[] args) {
        System.setErr(System.out);
        try {
            IndexDivergence oo = new IndexDivergence(args);
            oo.start();
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
