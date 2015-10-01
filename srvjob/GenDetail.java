
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author huangtm
 */
public class GenDetail {

    final int MAX_COLUMNS = 100;

    StkDb oStk;
    BufferedWriter bw;
    Connection conn;
    String outFile;
    String Flds[] = new String[MAX_COLUMNS];
    int Typs[] = new int[MAX_COLUMNS];

    private Statement stmt;
    private ResultSet rs;
    private String sql;
    private String tbl;
    private String path;
    private int cols;
    private int numColumns;

    GenDetail(String[] args) throws SQLException, IOException {
                String dbName;

        if (args.length < 4) {
            System.err.println("Usage: java GenDetail dbname tbl path table-columns");
            System.exit(-1);
        }
        dbName = args[0];
        tbl = args[1];
        path = args[2];
        cols = Integer.parseInt(args[3]);
        oStk = new StkDb(DbConfig.DB_TYPE, dbName, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
        conn = oStk.conn;
        outFile = String.format("%s%s.html", path, tbl);
        bw = Files.newBufferedWriter(Paths.get(outFile), StandardCharsets.UTF_8);
    }

    void getTargetFieldsInfo() throws SQLException {
        DatabaseMetaData dma;
        ResultSet results;
        String catalog = null;
        int i = 0;

        dma = oStk.conn.getMetaData();
        results = dma.getColumns(catalog, null, tbl, null);
        while (results.next()) {
            Flds[i] = results.getString("COLUMN_NAME");
            Typs[i] = results.getInt("DATA_TYPE");
            //if (Typs[i]==Types.VARCHAR) 
            i++;
        }
        numColumns = i;

    }

    public void start() throws SQLException, IOException {
        int i;
        String s1, s2, sClass;
        getTargetFieldsInfo();
        s1 = String.format("<body>" +
                "<form name='%s_form' id='%s_form'>" +
                "<TABLE cellpadding='3' cellspacing='0' border='0' width=100%%>\n",
                tbl, tbl);
        s2 = "";
        for (i = 0; i < numColumns; i++) {
            if (i % cols == 0) {
                s2 += "</TR>\n<TR>\n";
            }
            s2 += String.format("<TD><label for='%1$s'>%1$s</label><TD>", Flds[i]);
            if (Typs[i] == Types.DATE) {
                sClass = "class='datepicker'";
            }else if (Typs[i] == Types.INTEGER) {
                sClass = "class='integer'";
            }else if (Typs[i] == Types.DOUBLE) {
                sClass = "class='double'";
            } else {
                sClass = "";
            }
            s2 += String.format("<input type=text name='%1$s' id='%1$s' size=10 %2$s>\n",
                    Flds[i], sClass);
        }
        s2 = s2.substring(6);
        s1 += s2 + "</TR>\n</TABLE></form></body>";
        bw.write(s1);
        bw.close();
    }

    public static void main(String[] args) {
        try {
            GenDetail oo = new GenDetail(args);
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
    } //main

}
