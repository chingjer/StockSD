
import java.util.*;

/**
 * 網頁讀取後的特殊處理
 *
 * @author huangtm
 */
class MyWebPage extends GetWebPage {

    MyWebPage(String url,String chSet) {
        super(url,chSet);
        removeNonTag(pageContent);
        MyFunc.sbReplace(pageContent, "&nbsp;", " ", true);
        MyFunc.sbReplace(pageContent, "\t", "", true);
        //System.out.println(pageContent.toString());
    }

    /**
     * 1. 移除HTML TAG 以外的修飾文字，譬如 < TD style='xxx'> 變成 < TD><BR>
     * 2. 將TAG變成大寫<BR>
     * 3. 只保留< TABLE>< TR>等Tag其他全部移去
     */
    private void removeNonTag(StringBuilder sb1) {
        char[] chs;
        int i, flag;
        String s1, tag;
        //String removeTag = "<A></A><P></P><FONT></FONT><SPAN></SPAN>";
        String reservTag = "<TABLE></TABLE><TR></TR><TD></TD>";
        chs = sb1.toString().toCharArray();
        flag = 0;
        s1 = "";
        tag = "";
        for (i = 0; i < chs.length; i++) {
            if (flag == 0 && chs[i] == '<') {
                flag = 1;
            }
            if (flag == 1 && chs[i] == ' ') {
                flag = 2;
            }
            if (flag > 0 && chs[i] == '>') {
                flag = 3;
            }
            if (flag == 0) {
                s1 += chs[i];
            }
            if (flag == 1) {
                tag += Character.toUpperCase(chs[i]);
            }
            if (flag == 3) {
                tag += '>';
                //if (!removeTag.contains(tag)) {
                if (reservTag.contains(tag)) {
                    s1 += tag;
                }
                flag = 0;
                tag = "";
            }

        }
        if (sb1.length() != 0) {
            sb1.delete(0, sb1.length());
        }
        sb1.append(s1);
    }

    /**
     * 解析 pageContent 內容取出 begStr 之後各個 Column 的內容存放在 Token 集合中<br>
     * 考量< /TD>並非絕對必要(有些人會省略)，所以必須以< TD>為分隔
     *
     * @param begStr
     * @return StringTokenizer，為各個Column(< TD>)的內容
     */
    public StringTokenizer tableRow(String begStr) {
        String s1;
        int i, j;
        if ((i = pageContent.indexOf(begStr)) == -1) {
            return null;
        }
        i += begStr.length();
        if ((i = pageContent.indexOf("<TD>", i)) == -1) {
            return null;
        }
        if ((j = pageContent.indexOf("</TR>", i)) == -1) {
            return null;
        }
        s1 = pageContent.substring(i, j);
        //System.out.println(s1);
        s1 = s1.replaceAll("</TD>", "");
        s1 = s1.replaceAll("<TD>", "\t");
        s1 = s1.substring(1) + "\t";//第一個<TD>不加\t
        //System.out.println(s1);
        StringTokenizer st = new StringTokenizer(s1, "\t");
        return st;
    }

    /**
     * 解析 webPage 內容取出 begStr 之後的 Table RowS<br>
     * 從第 begRow(從1開始) 往下取出 rows行<br>
     * 當rows=-1為不限幾行,0=begrow本行，1=begrow~下一行
     *
     * @param begStr
     * @param begRow
     * @param rows
     * @return 表格內容。每一個row改用"\n"分隔， 每一個< /TD>改用\t當分隔
     */
    public String ParseTable(String begStr, int begRow, int rows) {
        int i, j, r;
        String sRslt, sRow, s1;
        Boolean inTag;
        char[] chs;

        if ((i = pageContent.indexOf(begStr)) == -1) {
            return "";
        }
        i += begStr.length();
        if ((j = pageContent.indexOf("</TABLE>", i)) == -1) {
            return "";
        }
        s1 = pageContent.substring(i, j);
        sRslt = "";
        r = 0;
        do {
            r++;
            if ((i = s1.indexOf("<TR>")) == -1) {
                break;
            } else {
                i += 4;
            }
            if ((j = s1.indexOf("</TR>", i)) == -1) {
                break;
            }
            sRow = s1.substring(i, j);
            s1 = s1.substring(j + 5);
            if (r < begRow) {
                continue;
            }
            sRow = sRow.replaceAll("</TD>", "");
            sRow = sRow.replaceAll("<TD>", "\t");
            sRow = sRow.substring(1) + "\t";
            sRslt = sRslt + sRow + "\n";

        } while (rows == -1 || r < begRow + rows);
        return sRslt;
    }

    public String adjustToken(String token) {
        //----- 校正字串內容，譬如將','從數字欄位移除
        token = token.replace(",", "");
        token = token.replace("N/A", "0");
        token = token.replace("%", "");
        return token;
    }

};
