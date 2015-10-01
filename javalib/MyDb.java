import java.sql.*;
import java.util.*;
import java.text.*;

/**
取得與資料庫的連結，以及提供各種資料庫不同日期格式的處理函式
目前可連結MySQL與Access(ODBC)
*/
class MyDb
{
	public Connection conn;
	public String date_delimiter,date_ch;
	public String dbType; // MSSQL, ODBC, ACCESS

	MyDb(String sType, String sDBName, String sUser, String sPwd)
	{
		ResultSet rs;
		String sql;

		try
		{
			if (sType.equals("MYSQL"))
			{
				//Class.forName("com.mysql.jdbc.Driver").newInstance();

				//Connection conn = 
				//  DriverManager.getConnection("jdbc:mysql://127.0.0.1/mytest?user=root&password=");
				//Connection conn = 
				//  DriverManager.getConnection("jdbc:mysql://localhost/stock", "root", "");
				Properties p = new Properties();
				p.put("characterEncoding", "utf8" );  // UTF8
				p.put("useUnicode", "TRUE" );
				p.put("user", sUser);
				p.put("password", sPwd);
				// sDBName like "127.0.0.1/StockDB"
				conn = 
					DriverManager.getConnection("jdbc:mysql://" + sDBName, p);
				date_delimiter = "-";
				date_ch = "'"; // 'yyyy-mm-dd'
			}
			else if ( sType.equals("ACCESS"))
			{
				Class.forName("sun.jdbc.odbc.JdbcOdbcDriver"); 
				// sDBName is odbc name, like "stock"
				conn = DriverManager.getConnection("jdbc:odbc:" + sDBName);  
				date_delimiter = "/";
				date_ch = "#"; // #yyyy/mm/dd#
			}
			dbType = sType;
		} // try

		catch (SQLException ex)
		{
			System.err.println(ex.getLocalizedMessage());
			System.err.println(ex);
			System.exit(-1);
		}
		catch (ClassNotFoundException ex)
		{
			System.out.println("*** Driver Error ***");
			System.err.println(ex);
			System.exit(-1);
		}
		catch (Exception ex)
		{
			System.err.println(ex);
			System.exit(-1);
		}

	} // MyDb()
	public java.util.Date addToDate( java.util.Date dte,int i) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(dte); 
		c1.add(Calendar.DATE, i);
		return c1.getTime();
	}
	/* 回傳日期字串 yyyy?MM?dd 如2009-09-01*/ 
	public String dateToStr( java.util.Date dte) 
	{     
		String sFormat = "yyyy"+date_delimiter+"MM"+date_delimiter+"dd";
		SimpleDateFormat sdf =  new SimpleDateFormat(sFormat);
		return String.valueOf(sdf.format(dte)); 
	}
	public String dateToStrCh( java.util.Date dte) 
	{     
		return padCh(dateToStr(dte)); 
	}
	public String padCh(String sDate) 
	{     
		return date_ch + sDate + date_ch;
	}
	public String dateTimeToStr( java.util.Date dte) 
	{     
		String sFormat = "yyyy"+date_delimiter+"MM"+date_delimiter+"dd kk:mm:ss";
		SimpleDateFormat sdf =  new SimpleDateFormat(sFormat);
		return String.valueOf(sdf.format(dte)); 
	}
	/* 轉換 yyyy-MM-dd(或yyyy/mm/dd) 為 Date */ 
	public java.util.Date strToDate( String sdate) 
	{
		java.util.Date dte;
		String sFormat = "yyyy"+date_delimiter+"MM"+date_delimiter+"dd";
		SimpleDateFormat sdf =  new SimpleDateFormat(sFormat);
		try{

			dte = sdf.parse(sdate);
		}
		catch(ParseException ex)
		{
			System.err.println(ex);
			dte = null;
		}
		return dte;
	}

} // MyDb class