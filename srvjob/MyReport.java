
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author huangtm
 */
public class MyReport {

    StkDb oStkDb;
    String rptHeadStyle = "font-size: medum;color: #FF6600;font-weight: bold";
    String titleStyle = "font-size: medium;color: #800000;font-weight: normal";
    String tableHeadStyle = "background-color:#c0c0c0";
    String tableStyle = "border: 1px solid #0000ff;font-size: small;";
    String tdStyle = "border: 1px solid #999999;font-size: small;";
    String reportId = ""; //用來區別哪一個報表

    String[] colNames;
    String[] moreColNames;
    int[] hideColPos;//要隱藏的欄位位置
    int numTotCols;//總欄位數=ResultSet的欄位數+增加的欄位
    int numMore;
    String appendId; // 新增行是別，若不是""則增加一行

    MyReport(StkDb oStk) {
        numMore = -1;
        oStkDb = oStk;
    }

    /**
     * 在顯示之前先檢查，如果符合該筆會被ByPass,這個方法通常被override
     *
     * @param rs ResultSet
     * @return true:ByPass, false: 正常顯示不跳過,s, 預設是return false
     */
    public boolean isByPass(ResultSet rs) throws SQLException {
        return false;
    }

    /**
     * override 這個function 以加入額外的資訊，如link
     *
     * @param ix 第幾個Column(從1開始)
     * @param rslt ResultSet
     * @return 在< TD>之後顯示的內容，可以很複雜到加入jScript
     * @throws SQLException
     */
    public String getColumn(int ix, ResultSet rslt) throws SQLException {
        return rslt.getString(ix);
    }

    /**
     * override 這個function 以加入額外的資訊，如[法人買賣超]等
     *
     * @param ix moreColumn的第幾個Column(從1開始)
     * @param rslt ResultSet
     * @return 在< TD>之後顯示的內容，可以很複雜到加入jScript
     * @throws SQLException
     */
    public String getMoreColumn(int ix, ResultSet rslt) throws SQLException {
        return "";
    }

    /**
     * 如果內容太多，可以增加一行
     *
     * @param rslt 查詢後的ResultSet
     * @param cols 該TABLE的總欄數=FieldColumnNum + moreColumnNum - hideColNum
     * @return 要顯示的字串
     * @throws SQLException
     */
    public String getAppendLine(ResultSet rslt, int cols) throws SQLException {
        return "";
    }

    /**
     * 報表初始化
     *
     * @param pMoreCol ResultSet之外多出來的欄位，以","分隔
     * @param pHideColPos ResultSet要隱藏的欄位位置，以","分隔，從1開始
     * @param append_id
     */
    public void init(String pMoreCol, String pHideColPos, String append_id) {
        String aa[];
        String bb[];
        int i;
        if (!"".equals(pMoreCol)) {
            aa = pMoreCol.split(",");
            numMore = aa.length;
            moreColNames = new String[numMore];
            System.arraycopy(aa, 0, moreColNames, 0, numMore);
            for (i = 0; i < moreColNames.length; i++) {
                moreColNames[i] = moreColNames[i].trim().toLowerCase();
            }
        }else{
            numMore = 0;
            moreColNames = new String[0];           
        }
        if (!"".equals(pHideColPos)) {
            bb = pHideColPos.split(",");
            hideColPos = new int[bb.length];
            for (i = 0; i < bb.length; i++) {
                hideColPos[i] = Integer.parseInt(bb[i]);
            }
        }else{
            hideColPos = new int[0];
        }
        appendId = append_id;
    }

    public boolean isHide(int jx) {
        int i;
        boolean is_hide = false;
        for (i = 0; i < hideColPos.length; i++) {
            if (hideColPos[i] == jx) {
                is_hide = true;
                break;
            }
        }
        return is_hide;
    }

    public void reportHead(BufferedWriter bw, String rptHeader) throws IOException {
        StringBuilder sb1 = new StringBuilder(1000);
        sb1.append("<html><head>\n");
        sb1.append("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n");
        sb1.append("<title>" + rptHeader + "</title>\n");

        sb1.append("<style type=\"text/css\">\n");
        sb1.append("table {" + tableStyle + "}\n");
        sb1.append("td {" + tdStyle + "}\n");
        sb1.append("</style>\n");

        sb1.append("<script language='jscript'>\n");
        sb1.append("function openWin(link,winName){\n");
        sb1.append("window.open(link, winName)\n");
        sb1.append("}</script>\n");

        sb1.append("</head>\n<body>\n");
        sb1.append(String.format("<p style=\"%s\">%s</p>", rptHeadStyle, rptHeader));
        bw.write(sb1.toString());
    }

    public void setReportId(String id) {
        reportId = id;
    }

    public String getReportId() {
        return reportId;
    }

    public StkDb getStkDb() {
        return oStkDb;
    }

    public String getAppendId() {
        return appendId;
    }

    public void reportTail(BufferedWriter bw) throws IOException {
        StringBuilder sb1 = new StringBuilder(50);
        sb1.append("</body></html>\n");
        bw.write(sb1.toString());
        bw.close();
    }

    public boolean reportToFile(String title, ResultSet rs, BufferedWriter bw)
            throws SQLException, IOException {

        ResultSetMetaData rsmd;
        boolean more;
        String ss;
        int i, numCols;

        if (numMore == -1) {
            System.err.println("你必須在reportToFile()之前先呼叫init(pMoreCol)");
            System.exit(-1);
        }
        if (title != null) {
            bw.write(String.format("<P style=\"%s\">%s</P><P>", titleStyle, title));
        }
        more = rs.next();
        if (!more) {
            bw.write("沒有符合的資料！");
            return false;
        }
        rsmd = rs.getMetaData();
        numCols = rsmd.getColumnCount();
        colNames = new String[numCols];
        numTotCols = numCols + numMore;
        ss = String.format("<TABLE cellpadding='3' cellspacing='0'><TR style=\"%s\">", tableHeadStyle);
        for (i = 1; i <= numCols; i++) {
            if (!isHide(i)) {
                colNames[i - 1] = rsmd.getColumnName(i);
                ss += "<td>" + colNames[i - 1];
            }
        }
        for (i = 1; i <= numMore; i++) {
            ss += "<td>" + moreColNames[i - 1];
        }

        bw.write(ss);
        bw.newLine();
        while (more) {
            if (isByPass(rs)) {
                more = rs.next();
                continue;
            }
            ss = "<TR>";
            for (i = 1; i <= numCols; i++) {
                if (!isHide(i)) {
                    ss += "<TD>" + getColumn(i, rs);
                }
            }
            for (i = 1; i <= numMore; i++) {
                ss += "<TD>" + getMoreColumn(i, rs);
            }
            bw.write(ss);
            bw.newLine();
            if (!"".equals(appendId)) {
                ss = getAppendLine(rs, numTotCols - hideColPos.length);
                bw.write(ss);
                bw.newLine();
            }
            more = rs.next();
        }
        bw.write("</TABLE>");
        numMore = -1;
        return true;
    }

}
