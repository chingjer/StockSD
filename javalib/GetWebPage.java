
import java.net.*;
import java.io.*;
/**
 * 讀取某網頁，將其內容放入 pageContent字串
 * @author huangtm
 */
public class GetWebPage
{
  public StringBuilder pageContent=new StringBuilder(5000);
  public GetWebPage( String pUrl, String chSet )
  {
    try
      {
        URL url = new URL(pUrl);
        URLConnection connection = url.openConnection();
                 
        //BufferedReader in = new BufferedReader(
        //  new InputStreamReader(connection.getInputStream()));                   
        BufferedReader in = new BufferedReader(
          new InputStreamReader(connection.getInputStream(),chSet));                   
        String inputLine;
        while( null != (inputLine = in.readLine()) )
        {       
          pageContent.append(inputLine);
        }
        in.close();      
      } //end of try
      catch(IOException e){
          pageContent = new StringBuilder("");
      }
      catch(Exception e){ 
            System.err.println(e.getMessage());
            System.err.println("================================");
            e.printStackTrace();
            System.exit(-1);
      } 
    finally{
  }
    }
}  