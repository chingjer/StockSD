
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Usage: java CompareDB isAll tbl fld1 fld2 fld3 isAll: Y-列印全部錯誤，N=有錯誤即停止
 * tbl:哪一個表格，如stk, stkid...
 *
 * @author huangtm
 */
public class CompareDB {

    public static void main(String[] args) {
        StkDb oMySql;
        StkDb oAccess;
        ResultSet rsM, rsA;
        ResultSetMetaData rsmd;
        String sqlM, sqlA;
        String currDateA, stockId;
        int i, errs, cx, ax;
        String tbl, fld = null;
        boolean isAll = false; // 列印全部錯誤
        boolean isMatch = true, isOk = false;
        double valM, valA;
        String strM = null, strA = null;
        java.util.Date dteM, dteA;
        int intM, intA;

        String columnName[];
        int columnCount = 0;
        int argType[];

        System.setErr(System.out);
        if (args.length < 3) {
            System.out.println("args error!");
            System.exit(-1);
        }
        if (args[0].equals("Y")) {
            isAll = true;
        }
        tbl = args[1];
        errs = 0;
        System.out.println("\r\n***** CompareDB() *****");
        try {
            oMySql = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
            oMySql.init("P");
            oAccess = new StkDb("ACCESS", "stock", "", ""); // must create ODBC:stock
            oAccess.init("");

            oMySql.stmt = oMySql.conn.createStatement();
            oAccess.stmt = oAccess.conn.createStatement();
            if (tbl.equals("stk")) {
                sqlM = String.format("select * from %s where dte ='%s' order by stockid",
                        tbl, oMySql.currDate);
            } else {
                sqlM = String.format("select * from %s order by stockid", tbl);
            }
            rsM = oMySql.stmt.executeQuery(sqlM);

            /*
             取得要比較欄位的欄位型態，同時驗證是否正確
             */
            rsmd = rsM.getMetaData();
            columnCount = rsmd.getColumnCount();
            columnName = new String[columnCount];
            argType = new int[args.length];
            for (cx = 0; cx < columnCount; cx++) {
                columnName[cx] = rsmd.getColumnName(cx + 1).toLowerCase();
            }
            for (ax = 2; ax < args.length; ax++) {
                args[ax] = args[ax].toLowerCase();
                argType[ax] = -1;
                for (cx = 0; cx < columnCount; cx++) {
                    if (args[ax].equals(columnName[cx])) {
                        argType[ax] = rsmd.getColumnType(cx + 1);
                        System.out.println(args[ax] + " Type is " + argType[ax]);
                        break;
                    }
                }
                if (argType[ax] == -1) {
                    System.out.println("要比較的 fldname 錯誤");
                    System.exit(-1);
                }
            }

            currDateA = oAccess.dateToStr(oMySql.strToDate(oMySql.currDate));
            while (rsM.next() && isMatch) {
                stockId = rsM.getString("stockid");
                if (tbl.equals("stk")) {
                    sqlA = String.format("select * from %s where stockid='%s' and dte =#%s#",
                            tbl, stockId, currDateA);
                } else {
                    sqlA = String.format("select * from %s where stockid='%s'",
                            tbl, stockId);

                }
                rsA = oAccess.stmt.executeQuery(sqlA);
                if (!rsA.next()) {
                    System.out.println("Error! Not Found in Access!\r\n" + sqlA);
                    System.exit(-1);
                }
                for (i = 2; i < args.length; i++) {
                    fld = args[i];
                    isOk = true;
                    if (argType[i] == Types.VARCHAR) {
                        strM = rsM.getString(fld);
                        strA = rsA.getString(fld);
                        if (strM == null) {
                            strM = "null";
                        }
                        if (strA == null) {
                            strA = "null";
                        }
                        if (!strM.equals(strA)) {
                            isOk = false;
                        }
                    } else if (argType[i] == Types.DATE) {
                        dteM = rsM.getDate(fld);
                        dteA = rsA.getDate(fld);
                        if (!dteM.equals(dteA)) {
                            isOk = false;
                            strM = String.valueOf(dteM);
                            strA = String.valueOf(dteA);
                        }
                    } else {
                        valM = rsM.getDouble(fld);
                        valA = rsA.getDouble(fld);
                        if (Math.abs(valA - valM) > 0.05) {
                            isOk = false;
                            strM = String.valueOf(valM);
                            strA = String.valueOf(valA);
                        }
                    }
                    if (!isOk) {
                        System.out.printf("UnMatch! id=%s,dte=%s,fld=%s, MySql=%s, Access=%s\n\r",
                                stockId, currDateA, fld, strM, strA);
                        if (!isAll) {
                            isMatch = false;
                            break;
                        }
                        errs++;

                    }
                }
            }
            if (errs == 0) {
                System.out.println("恭喜您！完全符合");
            } else {
                System.out.println("錯誤筆數:" + errs);
            }
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
