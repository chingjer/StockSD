
import java.sql.*;

/**
 * 計算三大法人賣賣超彙總表
 *
 * @Usage java CalcTppiiSum
 * @author huangtm
 */
public final class CalcTppiiSum {

    final double QTY_CRI = 0.1; //當買賣超>=5日均量*QTY_CRI時才計入賣賣超天數

    final int ITOT = 0; // nn[i]
    final int IA = 1;
    final int IB = 2;
    final int IC = 3;
    final int VOL1 = 0; //1日買超
    final int VOL5 = 1;
    final int VOL20 = 2;
    final int DAYS_5B = 3; //5日買超天數
    final int DAYS_5S = 4; //5日賣超天數
    final int DAYS_20B = 5;
    final int DAYS_20S = 6;

    StkDb oStk;
    Statement stmt;
    long nn[][] = new long[4][7];
    int iDays;
    String lastId;

    CalcTppiiSum() throws SQLException {
        oStk = new StkDb(DbConfig.DB_TYPE, DbConfig.DB_NAME, DbConfig.DB_USER, DbConfig.DB_PWD);
        oStk.init("P");
    }

    void init() {
        int i, j;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 7; j++) {
                nn[i][j] = 0L;
            }
        }
        iDays = 0;
    }

    void doUpdate() throws SQLException {
        ResultSet rs;
        Statement stmtUpdate;
        boolean isInsert = false;
        int ix, jx, cx;

        stmtUpdate = oStk.conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        String sql;
        sql = String.format("select * from tppii_sum where stockid='%s'", lastId);
        rs = stmtUpdate.executeQuery(sql);
        if (!rs.next()) {
            isInsert = true;
            rs.moveToInsertRow();
            rs.updateString("stockid", lastId);
        }
        rs.updateDate("dte", Date.valueOf(oStk.currDate));
        for (ix = 0; ix < nn.length; ix++) {
            for (jx = 0; jx < nn[0].length; jx++) {
                cx = (ix * nn[0].length) + jx + 3; //Column從1開始算,stockid,dte,nn[0][0]...故+3
                rs.updateLong(cx, nn[ix][jx]);
            }
        }
        if (isInsert) {
            rs.insertRow();
        } else {
            rs.updateRow();
        }

    }

    boolean isLargeVol(long qty, long va) {
        boolean rslt = false;
        if (va != 0) {
            if ((double)Math.abs(qty)  >= (double)(va * QTY_CRI)) {
                rslt = true;
            }
        }
        return rslt;
    }

    void start() throws SQLException {
        ResultSet rs;
        java.util.Date dCurrDate;
        String sFromDate, sql;
        long qtya, qtyb, qtyc, qtyt,va5;

        dCurrDate = oStk.strToDate(oStk.currDate);
        stmt = oStk.conn.createStatement();
        //--- delete 30天以前的資料
        sFromDate = oStk.getPrevStockDate(dCurrDate, 30);
        sql = String.format("delete from tppii where dte < %s",oStk.padCh(sFromDate));
        stmt.executeUpdate(sql);        
        //-----
        sFromDate = oStk.getPrevStockDate(dCurrDate, 20);
        sql = String.format("select t.*,s.va5 from tppii t inner join stk s " + ""
                + "on t.stockid = s.stockid and t.dte = s.dte where t.dte between %s and %s "
                + " order by t.stockid,t.dte DESC", oStk.padCh(sFromDate), oStk.padCh(oStk.currDate));
        rs = stmt.executeQuery(sql);
        lastId = "XXXX";
        while (rs.next()) {
            if (!lastId.equals(rs.getString("stockid"))) {
                if (!"XXXX".equals(lastId)) {
                    doUpdate();
                }
                lastId = rs.getString("stockid");
                init();
            }
            qtyt = rs.getLong("tot_qty");
            qtya = rs.getLong("a_qty");
            qtyb = rs.getLong("b_qty");
            qtyc = rs.getLong("c_qty");
            va5 = rs.getLong("va5");
            if (iDays == 0) {
                nn[ITOT][VOL1] = qtyt;
                nn[IA][VOL1] = qtya;
                nn[IB][VOL1] = qtyb;
                nn[IC][VOL1] = qtyc;
            }
            if (iDays < 5) {
                nn[ITOT][VOL5] += qtyt;
                nn[IA][VOL5] += qtya;
                nn[IB][VOL5] += qtyb;
                nn[IC][VOL5] += qtyc;
                if (isLargeVol(qtyt,va5)) {
                    if (qtyt > 0) {
                        nn[ITOT][DAYS_5B] += 1;
                    } else if (qtyt < 0) {
                        nn[ITOT][DAYS_5S] += 1;
                    }
                }
                if (isLargeVol(qtya,va5)) {
                    if (qtya > 0) {
                        nn[IA][DAYS_5B] += 1;
                    } else if (qtya < 0) {
                        nn[IA][DAYS_5S] += 1;
                    }
                }
                if (isLargeVol(qtyb,va5)) {
                    if (qtyb > 0) {
                        nn[IB][DAYS_5B] += 1;
                    } else if (qtyb < 0) {
                        nn[IB][DAYS_5S] += 1;
                    }
                }
                if (isLargeVol(qtyc,va5)) {
                    if (qtyc > 0) {
                        nn[IC][DAYS_5B] += 1;
                    } else if (qtyc < 0) {
                        nn[IC][DAYS_5S] += 1;
                    }
                }

            }
            if (iDays < 20) {
                nn[ITOT][VOL20] += qtyt;
                nn[IA][VOL20] += qtya;
                nn[IB][VOL20] += qtyb;
                nn[IC][VOL20] += qtyc;
                if (isLargeVol(qtyt,va5)) {
                    if (qtyt > 0) {
                        nn[ITOT][DAYS_20B] += 1;
                    } else if (qtyt < 0) {
                        nn[ITOT][DAYS_20S] += 1;
                    }
                }
                if (isLargeVol(qtya,va5)) {
                    if (qtya > 0) {
                        nn[IA][DAYS_20B] += 1;
                    } else if (qtya < 0) {
                        nn[IA][DAYS_20S] += 1;
                    }
                }
                if (isLargeVol(qtyb,va5)) {
                    if (qtyb > 0) {
                        nn[IB][DAYS_20B] += 1;
                    } else if (qtyb < 0) {
                        nn[IB][DAYS_20S] += 1;
                    }
                }
                if (isLargeVol(qtyc,va5)) {
                    if (qtyc > 0) {
                        nn[IC][DAYS_20B] += 1;
                    } else if (qtyc < 0) {
                        nn[IC][DAYS_20S] += 1;
                    }
                }
            }
            iDays += 1;
        }//while
        doUpdate();
        rs.close();
    }

    public static void main(String[] args) {
        System.setErr(System.out);
        System.out.println("\r\n***** CalcTppiiSum() *****");
        try {
            CalcTppiiSum oo = new CalcTppiiSum();
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
