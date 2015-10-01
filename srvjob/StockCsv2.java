
import java.lang.*;
import java.util.*;
import java.io.*;
import java.sql.*;

/**
 * <h1>上市與上櫃每日三大法人買賣超資料轉入資料庫</h1>
 * 從證交所與櫃買中心下載的三大法人買賣超資料轉入資料庫 <br>
 * 允許多個檔案同時轉入， 此時需注意檔名的先後順序。
 *
 * @author huangtm
 * @version 1.0
 */
class StockCsv2 {

    StkDb oStk;
    Statement stmt;
    String baseDir;
    String sInsert, sUpdate;
    int cnt_all, cnt_ins;

    /**
     * @param pBaseDir 盤後資料下載檔目錄，如：C:\mystk\import\
     */
    StockCsv2(String pBaseDir) {
        baseDir = pBaseDir;
        try {
            oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
            stmt = oStk.conn.createStatement();
            oStk.init("P");
        } catch (SQLException ex) {
            System.out.println("SQL Error");
            ex.printStackTrace();
            System.exit(-1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * 讀取 baseDir目錄中所有A11*.csv(證交所) , RSTA*.csv(櫃買) 然後依序處理
     */
    @SuppressWarnings("unchecked")
    public void doImport() throws
            FileNotFoundException, SQLException, IOException {
        File ImportDir;
        File[] aFiles;
        String s1, ext;
        int i, j;
        String sFilter = ".csv;.CSV";

        ImportDir = new File(baseDir);
        if (!ImportDir.exists()) {
            System.err.println("沒有這個目錄 " + ImportDir);
            System.exit(-1);
        }
        aFiles = ImportDir.listFiles();
        if (aFiles.length == 0) {
            System.out.println("No Files Match!");
            System.exit(-1);
        }
        Arrays.sort(aFiles, new Comparator() {
            @Override
            public int compare(Object f1, Object f2) {
                return ((File) f1).getName().compareTo(((File) f2).getName());
            }
        });
        for (i = 0; i < aFiles.length; i++) {
            if (!aFiles[i].isDirectory()) {
                s1 = aFiles[i].getName();
                j = s1.indexOf(".");
                ext = s1.substring(j);
                if (s1.substring(0, 3).equals("T2_") && sFilter.contains(ext)) {
                    System.out.println(s1);
                    Import1_TWSE(aFiles[i]);
                } else if (s1.substring(0, 3).equals("G2_") && sFilter.contains(ext)) {
                    System.out.println(s1);
                    Import1_GRETAI(aFiles[i]);
                }
            }
        } // for
    } // Import()

    /**
     * 讀取csv檔案中含有年月日那一行中的日期
     *
     * @param sLine csv檔案中的一行資料
     * @return 日期字串，如"2014/01/31"(Access) 或 "2014/01/31"(MySql)
     */
    String getCsvDate(String sLine) {
        String sDate, yy, mm, dd;
        int i;
        sLine = sLine.replace("\"", "");//將引號去掉
        i = sLine.indexOf("年");
        yy = sLine.substring(0, i);
        mm = sLine.substring(i + 1, i + 3);
        i = sLine.indexOf("月") + 1;
        dd = sLine.substring(i, i + 2);
        if (yy.length() < 4) {
            sDate = "" + (1911 + Integer.parseInt(yy));
        } else {
            sDate = yy;
        }
        sDate += oStk.date_delimiter + mm + oStk.date_delimiter + dd;
        System.out.printf("sDate=%s, yy=%s\n", sDate, yy);
        return sDate;

    }

    /**
     * 移除雜亂的字元，如"\"",",","=","--"等
     *
     * @param sLine 從csv讀進來的一行字
     * @return 移除雜亂字元後的字串
     */
    private String removeMessyChar(String sLine) {
        char ch[];
        StringBuilder sb2 = new StringBuilder(200);
        String s2;
        boolean isdeli = false;

        ch = sLine.toCharArray();
        MyFunc.initStr(sb2);
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == '\"') {
                isdeli = !isdeli;
            } else {
                if (isdeli) {
                    if (ch[i] != ',') {
                        sb2.append(ch[i]);
                    }
                } else {
                    sb2.append(ch[i]);
                }
            }
        } // for
        s2 = sb2.toString().replace("--", "00");
        s2 = s2.replace("0-", "00");
        s2 = s2.replace("=", "");
        return s2;
    }

    /**
     * 利用取得的SQL字串實際異動 stk, sthid 最後更新 SYSPARM 中的 currentdate 欄位
     *
     * @param sUpdate
     * @param sInsert
     * @param aa csv一行分解為陣列，aa[0]是stockid
     * @param sDate
     * @throws SQLException
     */
    private void updData(String aa[]) throws SQLException {
        String sql2;
        ResultSet rs;
        cnt_all++;
        if (stmt.executeUpdate(sInsert) != 1) {
            System.err.println(sInsert);
            System.exit(-1); // 不應該有不成功情形
            //stmt.executeUpdate(sUpdate);
        } else {
            cnt_ins++;
        }
    }

    /**
     * 處理上市三大法人
     *
     * @param fi 徵交所網頁selType=ALL or ALLBUT0999
     */
    private void Import1_TWSE(File fi) throws
            FileNotFoundException, SQLException, IOException {
        BufferedReader inf;
        String s1, s2, sDate;
        Boolean isBegin, isValid;
        int lines, i;
        String[] aa;
        double nn;

        cnt_all = 0;
        cnt_ins = 0;
        sDate = "";
        inf = new BufferedReader(new FileReader(fi));
        isBegin = false;
        lines = 0;
        while ((s1 = inf.readLine()) != null) {
            lines++;
            if (lines == 1) {
                sDate = getCsvDate(s1);
                stmt.executeUpdate(
                        "delete from tppii where typ='T' and dte=" + oStk.padCh(sDate));
            } else if (lines >= 3) {
                //--- 解決字串中的,號，如"200,450,000"==> 200450000
                aa = removeMessyChar(s1).split(",");
                isValid = true;
                if (aa.length < 11) {
                    isValid = false;
                }
                if (aa[0].length() == 0) {
                    isValid = false;
                } else if (aa[0].length() != 4 && aa[0].substring(0, 1).equals("0")) {
                    isValid = false;
                }
                for (i = 0; i < aa.length; i++) {
                    aa[i] = aa[i].trim();
                }
                if (isValid) { // update or insert to table stk
                    MyFunc.initSql();
                    MyFunc.genSql("T", "typ", "'");
                    MyFunc.genSql(oStk.padCh(sDate), "dte", "");
                    MyFunc.genSql(aa[0], "stockid", "'");
                    nn = Double.parseDouble(aa[2]) - Double.parseDouble(aa[3]); //外資買-賣
                    MyFunc.genSql_d(nn / 1000d, "a_qty", "");
                    nn = Double.parseDouble(aa[4]) - Double.parseDouble(aa[5]); //投信買-賣
                    MyFunc.genSql_d(nn / 1000d, "b_qty", "");
                    nn = Double.parseDouble(aa[6]) - Double.parseDouble(aa[7]); //自營買-賣
                    nn += Double.parseDouble(aa[8]) - Double.parseDouble(aa[9]); //自營避買-賣
                    MyFunc.genSql_d(nn / 1000d, "c_qty", "");
                    nn = Double.parseDouble(aa[10]); //三大法人合計
                    MyFunc.genSql_d(nn / 1000d, "tot_qty", "");
                    MyFunc.genSqlEnd();
                    sInsert = MyFunc.getInsertSql("tppii");
                    sUpdate = MyFunc.getUpdateSql("tppii", String.format(
                            "where stockid = '%s' and dte=%s", aa[0], oStk.padCh(sDate)));
                    updData(aa);
                } //  update or insert to table stk
                else {
                    //System.out.printf("Ingore Id=%s,Len=%d, aa.length=%d\r\n", aa[0], aa[0].length(), aa.length);

                }

            } // elseif begin
        } // while
        inf.close();
        System.out.printf("ALL#=%d, INS#=%d\r\n", cnt_all, cnt_ins);
    } // function

    /**
     * 處理上櫃三大法人
     *
     * @param fi
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    private void Import1_GRETAI(File fi) throws
            FileNotFoundException, SQLException, IOException {
        BufferedReader inf;
        String s1, sDate;
        int lines, i;
        String[] aa;
        Double nn;

        cnt_all = 0;
        cnt_ins = 0;

        sDate = "";
        inf = new BufferedReader(new FileReader(fi));
        lines = 0;
        while ((s1 = inf.readLine()) != null) {
            lines++;
            if (lines == 1) {
                sDate = getCsvDate(s1);
                stmt.executeUpdate(
                        "delete from tppii where typ='G' and dte=" + oStk.padCh(sDate));
            } else if (lines >= 3) {
                //--- 解決字串中的,號，如"200,450,000"==> 200450000
                //if (lines > 8)	{ break;}
                aa = removeMessyChar(s1).split(",");
                for (i = 0; i < aa.length; i++) {
                    aa[i] = aa[i].trim();
                }
                if (aa.length < 16 || aa[0].length() != 4) {
                    //System.out.printf("Ingore Id=%s,Len=%d, aa.length=%d\r\n", aa[0], aa[0].length(), aa.length);
                } else {
                    MyFunc.initSql();
                    MyFunc.genSql("G", "typ", "'");
                    MyFunc.genSql(oStk.padCh(sDate), "dte", "");
                    MyFunc.genSql(aa[0], "stockid", "'");
                    nn = Double.parseDouble(aa[4]); //外資買-賣
                    MyFunc.genSql_d(nn / 1000d, "a_qty", "");
                    nn = Double.parseDouble(aa[7]); //投信買-賣
                    MyFunc.genSql_d(nn / 1000d, "b_qty", "");
                    nn = Double.parseDouble(aa[8]); //自營買-賣+自營避買-賣
                    MyFunc.genSql_d(nn / 1000d, "c_qty", "");
                    nn = Double.parseDouble(aa[15]); //三大法人合計
                    MyFunc.genSql_d(nn / 1000d, "tot_qty", "");
                    MyFunc.genSqlEnd();
                    sInsert = MyFunc.getInsertSql("tppii");
                    sUpdate = MyFunc.getUpdateSql("tppii", String.format(
                            "where stockid = '%s' and dte=%s", aa[0], oStk.padCh(sDate)));
                    updData(aa);
                }
            } // elseif begin
        } // while
        inf.close();
        System.out.printf("ALL#=%d, INS#=%d\r\n", cnt_all, cnt_ins);
    } // function

    /**
     * 執行方式: java StockCsv2 [importdir] 如果沒有[importdir] 則預設為目前目錄，");
     *
     * @param args [importdir]
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {

        String sBaseDir;
        System.setErr(System.out);

        System.out.println("\r\n***** StockCsv2 (轉入csv檔) *****");
        if (args.length > 0) {
            sBaseDir = args[0];
        } else {
            sBaseDir = "./";
        }
        //System.out.println ("Dir is "+sBaseDir);
        StockCsv2 csv = new StockCsv2(sBaseDir);
        try {
            csv.doImport();
        } catch (FileNotFoundException e) {
            System.err.println("FileNot Found!");
            e.printStackTrace();
            System.exit(-1);
        } catch (SQLException e) {
            System.err.println(csv.sInsert);
            System.err.println("SQL Error");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("IOException ");
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

    } //main

};
