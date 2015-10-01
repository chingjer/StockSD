
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author huangtm
 */
public class MyFunc {

    public static StringBuilder sInsSql1, sInsSql2, sUpdSql;

    static {
        sInsSql1 = new StringBuilder(200);
        sInsSql2 = new StringBuilder(200);
        sUpdSql = new StringBuilder(200);
    }

    public static void sbReplace(StringBuilder builder, String from, String to, boolean isAll) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            if (!isAll) {
                break;
            }
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(from, index);
        }
    }

    public static void initStr(StringBuilder sb1) {
        if (sb1.length() != 0) {
            sb1.delete(0, sb1.length());
        }
    }

    public static void initSql() {
        initStr(sUpdSql);
        initStr(sInsSql1);
        initStr(sInsSql2);
    }

    /**
     * 移除sql字串的最後一個字
     */
    public static void genSqlEnd() {
        sUpdSql.deleteCharAt(sUpdSql.length() - 1);
        sInsSql1.deleteCharAt(sInsSql1.length() - 1);
        sInsSql2.deleteCharAt(sInsSql2.length() - 1);
    }

    public static String getInsertSql(String tbl) {
        return String.format("insert into %s (%s) Values(%s)",
                tbl, sInsSql1.toString(), sInsSql2.toString());
    }

    public static String getUpdateSql(String tbl, String sWhere) {
        return String.format("update %s set %s %s",
                tbl, sUpdSql.toString(), sWhere);
    }

    /**
     * 產生將來可以在insert, update SQL指令使用的字串，如下：
     * <p>
     * s1msert = "insert into stkbasic (" + sInsSql1 + ") Values(" + sInsSql2 +
     * ")"; <br>
     * sUpdate = "update stkbasic set " + sUpdSql + " where stockid = '" +
     * stockId + "'";
     *
     * @param token Field Value
     * @param fld Field Name
     * @param strMark 前後字元
     */
    public static void genSql_d(double token, String fld, String strMark) {
        genSql(String.valueOf(token), fld, strMark);
    }

    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public static void genSql(String token, String fld, String strMark) {
        //System.out.print(fld);
        //System.out.print("===");
        //System.out.println(token);
        sUpdSql.append(fld + "=" + strMark + token + strMark + ",");
        sInsSql1.append(fld + ","); // Insert Sql Fields Part
        sInsSql2.append(strMark + token + strMark + ",");	// Insert Sql Values Part	
        return;
    }

    public static void download(String source, String destination, HttpURLConnection pConn)
            throws IOException, ConnectException {

        InputStream is;
        HttpURLConnection conn;

        if (pConn != null) {
            conn = pConn;
        } else {
            conn = (HttpURLConnection) new URL(source).openConnection();
        }
        is = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        for (int length; (length = is.read(buffer)) > 0; fos.write(buffer, 0, length));
        fos.close();
        is.close();
    }

    public static HttpURLConnection excutePost(String targetURL, String urlParameters) {
        URL url;
        DataOutputStream wr;
        HttpURLConnection connection = null;
        try {
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", ""
                    + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "UTF-8");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {

            //if (connection != null) {
            //    connection.disconnect();
            //}
        }
        return connection;
    }
}
