
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 搭配「參數管理子系統」的ssd
 *
 * @since 2015/05/05
 * @author huangtm
 */
abstract class ssdp extends ssd {

    final int NUM_FIX = 14;//固定參數的數目
    final int NUM_VAR = 12;//變動參數的最大數目
    final int BEGCOL_PARA = 6;//在bt_para中除key值以外，第1個參數起始的位置(start with 1)
    public String args[];//有命令列執行時附加的變動參數，為sys subsys datecodes
    public String dateCodes;//由命令列傳入指定日期代號，就不需要重新Compile程式，可為多個以,分隔如"A,B,C...."
    public String[] fixParaNames;//固定參數名稱
    public String[] varParaNames;//變動參數名稱
    public int paraNum[];//bt_para中的手動輸入num
    int numVarPara;//實際變動參數的數目

    @Override
    public void setSys() {
        sys = args[0];
        subsys = args[1];
        dateCodes = args[2];
    }
    @Override
    public int getParaNum(int px){
        return paraNum[px];
    }

    @Override
    public void setParm() {
        Statement stmt;
        String sql;
        ResultSet rs;
        ResultSetMetaData rsmd;
        int numCols = 0, recs = 0, i, px;
        StringBuilder sqlb = new StringBuilder(300);

        try {
            getVarParaNames();
            getFixParaNames();
            stmt = conn.createStatement();
            sql = String.format("select count(*) as cnt from bt_para where "
                    + "sys='%s' and subsys='%s' and enabled = 1", sys, subsys);
            //System.out.println(sql);
            rs = stmt.executeQuery(sql);
            rs.next();
            recs = rs.getInt("cnt");
            if (recs == 0) {
                System.out.printf("sys=%s,subsys=%s bt_para中沒有符合的紀錄！\n", sys, subsys);
                System.exit(-1);
            } else {
                para_grp = new String[recs];
                paraNum = new int[recs];
            }
            rs.close();

            sql = String.format("select * from bt_para where "
                    + "sys='%s' and subsys='%s' and enabled = 1 ORDER by num", sys, subsys);
            rs = stmt.executeQuery(sql);
            rsmd = rs.getMetaData();
            numCols = rsmd.getColumnCount();
            px = 0;
            while (rs.next()) {
                initStr(sqlb);
                for (i = BEGCOL_PARA; i <= numCols; i++) // Column 1 是 NUM(排序用)不讀入
                {
                    sqlb.append(rs.getString(i).trim() + (i == numCols ? "" : ","));
                }
                System.out.println("(" + getParaNum(px) + ")" + sqlb.toString());
                paraNum[px] = rs.getInt("num");
                para_grp[px++] = sqlb.toString();
            }

        } catch (SQLException e) {
            System.err.println("setParm() Error");
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void getVarParaNames() throws SQLException {
        Statement stmt;
        String sql, aa[], s1;
        ResultSet rs;
        ResultSetMetaData rsmd;
        int columnCount, i;
        varParaNames = new String[NUM_VAR];
        sql = String.format("SELECT * from bt_paraname where sys = '%s' and subsys='%s'",
                sys, subsys);
        //System.out.println(sql);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
            numVarPara = 0;
            for (i = 3; i <= columnCount; i++) {
                s1 = rs.getString(i);
                //System.out.println(s1);
                if (!s1.equals("")) {
                    aa = s1.split("=");
                    varParaNames[i - 3] = aa[0].toUpperCase();
                    numVarPara++;
                } else {
                    varParaNames[i - 3] = "";
                }
            }
        }
    }
    public void getFixParaNames() throws SQLException {
        Statement stmt;
        String sql, aa[], s1;
        ResultSet rs;
        ResultSetMetaData rsmd;
        int columnCount, i;
        fixParaNames = new String[NUM_FIX];
        sql = String.format("SELECT * from bt_para LIMIT 1");
        //System.out.println(sql);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
            for (i = BEGCOL_PARA; i <= 19; i++) {
                s1 = rsmd.getColumnName(i);
                fixParaNames[i - BEGCOL_PARA] = s1.toUpperCase();
                //System.out.println("("+(i-5)+")"+fixParaNames[i - BEGCOL_PARA]);
            }
        }
    }

    @Override
    public void printPara() {
        int i;
        String s1 = getFixParaSpec();
        for (i = 0; i < numVarPara; i++) {
            s1 += String.format("\r\n(%d)%s=%s", i + NUM_FIX, varParaNames[i], para[i + NUM_FIX]);
        }
        debugPrint(s1);
    }
    
    public String getParaVal(String pnm){
        int i;
        String s1 = pnm.toUpperCase();
        for (i=0;i<NUM_VAR;i++){
            if (s1.equals(varParaNames[i])){
                return para[i+NUM_FIX];
            }
        }
        for (i=0;i<NUM_FIX;i++){
            if (s1.equals(fixParaNames[i])){
                return para[i];
            }
        }        
        System.err.println("Error parameter Name = " + s1);
        System.exit(-1);
        return "";
    }
}
