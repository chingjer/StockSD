
import java.io.*;
import java.sql.*;

/**
 * <h1>文字檔轉入資料庫</h1>
 * 從Access舊系統匯出文字檔約以半年為一個檔，約1500*120=18萬筆記錄。
 * 此時用phpMyAdmin的匯入功能因檔案太大無法正常執行，故使用本程式 直接轉入，同時可以解決格式不一的問題。
 * <p>
 * 使用的方法是：首先在Access中匯出文字檔(有標題，以","分隔欄位，注意：欄位名稱
 * 必須與欲轉入MySQL的Table欄位名稱相同，但兩者欄位數目與先後順序可以不同。<br>
 * 如果欄位名稱不同，你可以在產生文字檔後再改標題行名稱(用NOTEPAD++改)<br>
 * 
 * Usage: java ImportText dbname tbl txtFile"
 *
 * @author huangtm
 * @version 1.0
 * @since 2015-01-16
 */
class ImportText {

    final int MAX_COLUMNS = 100;
    StkDb oStk;
    String tableName;
    String sourceFile;
    int cnt_all, cnt_ins;
    String sourceFields[];
    int targetNumColumns; // 實際欄位數
    int atSrcPos[] = new int[MAX_COLUMNS];//targetFields 對應的 sourceFields 欄位位置
    String targetFields[] = new String[MAX_COLUMNS];
    int targetTypes[] = new int[MAX_COLUMNS];

    Statement stmt;
    String sInsert;
    StringBuilder sInsSql1 = new StringBuilder(200);
    StringBuilder sInsSql2 = new StringBuilder(200);

    /**
     * @param pBaseDir 盤後資料下載檔目錄，如：C:\mystk\import\
     */
    ImportText(String args[]) {
        String dbName;
        
        if (args.length < 3) {
            System.out.println("Usage: java ImportText dbname tbl txtFile");
            System.exit(-1);
        }
        
        dbName = args[0];
        tableName = args[1];
        sourceFile = args[2];
        try {
            oStk = new StkDb(DbConfig.DB_TYPE, dbName, DbConfig.DB_USER, DbConfig.DB_PWD);
            stmt = oStk.conn.createStatement();
            oStk.init("");
            getTargetFieldsInfo();
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
     * 取得target Table的欄位名稱與欄位資料型態
     *
     * @throws SQLException
     */
    void getTargetFieldsInfo() throws SQLException {
        DatabaseMetaData dma;
        ResultSet results;
        String catalog = null;
        int i = 0;

        dma = oStk.conn.getMetaData();
        results = dma.getColumns(catalog, null, tableName, null);
        while (results.next()) {
            targetFields[i] = results.getString("COLUMN_NAME");
            targetTypes[i] = results.getInt("DATA_TYPE");
            //if (targetTypes[i]==Types.VARCHAR) 
            i++;
        }
        targetNumColumns = i;
    }

    /**
     * 計算TABLE的每一個欄位對應到Source檔的哪一個位置
     */
    void setAtSourcePosition() {
        for (int i = 0; i < targetNumColumns; i++) {
            targetFields[i] = targetFields[i].toLowerCase();
        }
        for (int i = 0; i < sourceFields.length; i++) {
            sourceFields[i] = sourceFields[i].toLowerCase();
        }
        for (int i = 0; i < targetNumColumns; i++) {
            atSrcPos[i] = -1;
            for (int jx = 0; jx < sourceFields.length; jx++) {
                if (targetFields[i].equals(sourceFields[jx])) {
                    atSrcPos[i] = jx;
                    break;
                }
            }
            if (atSrcPos[i] != -1) {
                System.out.printf("%d,%s,對應Text欄位%d(%s)\r\n",
                        i, targetFields[i], atSrcPos[i], sourceFields[atSrcPos[i]]);
            }
        }
    }

    /**
     * 移除雜亂的字元，如"\"",",","=","--"等
     *
     * @param sLine 從csv讀進來的一行字
     * @return 移除雜亂字元後的字串
     */
    String removeMessyChar(String sLine) {
        char ch[];
        StringBuilder sb2 = new StringBuilder(200);
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
        return sb2.toString();
    }

    /**
     * 利用取得的SQL字串實際異動 stk
     *
     * @throws SQLException
     */
    void updData() throws SQLException {
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
     * 匯入
     */
    void doImport() throws
            FileNotFoundException, SQLException, IOException {
        BufferedReader inf;
        String s1, sMark;
        Boolean isBegin, isValid;
        int fileType;
        int lines, i;
        String[] aa;
        Double vol;

        cnt_all = 0;
        cnt_ins = 0;
        inf = new BufferedReader(new FileReader(sourceFile));
        isBegin = false;
        lines = 0;
        while ((s1 = inf.readLine()) != null) {
            lines++;
            if (s1.substring(s1.length() - 1).equals(",")) {
                s1 += " ";
            }

            aa = removeMessyChar(s1).split(",");
            if (lines == 1) {//標題行
                sourceFields = new String[aa.length];
                for (i = 0; i < aa.length; i++) {
                    sourceFields[i] = aa[i]; // input field name
                }
                setAtSourcePosition();
                for (i = 0; i < targetNumColumns; i++) {
                    if (atSrcPos[i] != -1) {
                        sInsSql1.append(targetFields[i] + ",");
                    }
                }
                sInsSql1.deleteCharAt(sInsSql1.length() - 1);
            } else {
                sInsSql2.delete(0, sInsSql2.length());
                for (i = 0; i < targetNumColumns; i++) {
                    if (atSrcPos[i] != -1) {
                        if (targetTypes[i] == Types.VARCHAR
                                || targetTypes[i] == Types.DATE) {
                            sMark = "'";
                        } else {
                            sMark = "";
                            if ("".equals(aa[atSrcPos[i]].trim())) {
                                aa[atSrcPos[i]] = "0";
                            }
                        }
                        if (targetTypes[i] == Types.DATE && "".equals(aa[atSrcPos[i]])) {
                            aa[atSrcPos[i]] = "0000-00-00";
                        }
                        //System.out.printf("%d at Text %d\r\n", i, atSrcPos[i]);
                        try {
                            sInsSql2.append(sMark + aa[atSrcPos[i]] + sMark + ",");
                        } catch (Exception e) {
                            System.out.println(s1);
                            e.printStackTrace();
                            System.exit(-1);
                        }
                    }//if -1
                }//for
                sInsSql2.deleteCharAt(sInsSql2.length() - 1);
                sInsert = String.format("insert into %s (%s) Values(%s)",
                        tableName, sInsSql1.toString(), sInsSql2.toString());
                if (lines % 2000 == 1) {
                    //System.err.printf("%d %s\r\n",lines,sInsSql1);
                    System.err.printf("%d\r\n", lines);
                }
                updData();
            } //  update or insert to table stk
        } // while
        inf.close();
        System.out.printf("ALL#=%d, INS#=%d\r\n", cnt_all, cnt_ins);
    } // function

    /**
     * 執行方式: java ImportText [importdir] 如果沒有[importdir] 則預設為目前目錄，");
     *
     * @param args [importdir]
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args ) {
        //System.setErr(System.out);
        System.out.println("\r\n***** ImportText (text匯入MySQL) *****");
        //System.out.println ("Dir is "+txtFile);
        ImportText obj = new ImportText(args);
        try {
            obj.doImport();
        } catch (FileNotFoundException e) {
            System.err.println("FileNot Found!");
            e.printStackTrace();
            System.exit(-1);
        } catch (SQLException e) {
            System.err.println(obj.sInsert);
            System.err.println("SQL Error，請先檢查是否資料重複！");
            System.err.println(e.getMessage());
            System.err.println("================================");
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
