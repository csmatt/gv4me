/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.HttpsConnection;

/**
 *
 * @author Matt Defenthaler
 */
public class connMgr{
    private static Vector reqProps = new Vector();
    private static HttpsConnection c = null;
    private static final String beginNumber = "<b class=\"ms3\">";
    private static final String endNumber = "</b>";

    public static synchronized void open(String url, String reqMethod, Vector custReqProps, String postData) throws ConnectionNotFoundException, IOException, Exception
    {
        OutputStream os = null;
        try{
            c = RMSCookieConnector.open(url);
            c.setRequestMethod(reqMethod);
            insertReqProps(custReqProps);

            if(reqMethod.equals("POST") && !postData.equals(""))
            {
                os = c.openOutputStream();
                os.write(postData.getBytes());
                os.close();
            }

            int respCode = c.getResponseCode();
            if(respCode != 200 && respCode != 302)
            {
                Logger.add("response Code= ", String.valueOf(respCode));
            }

            redirectHandler(custReqProps);
        }
        catch(ConnectionNotFoundException cnf)
        {
            close();
            throw cnf;
        }
        finally{
            try{
            os.close();
            }
            catch(Exception ignore){}
        }
    }

    public static int getResponseCode() throws IOException
    {
        return c.getResponseCode();
    }

    private static void redirectHandler(Vector reqProps) throws IOException, ConnectionNotFoundException, Exception
    {
        String loc = "";
        for(int i = 0; i < 5 && c.getResponseCode() == 302 && (loc = c.getHeaderField("Location")) != null; i++)
        {//prevent redirect loop from continuing forever
            close();
            c = RMSCookieConnector.open(loc);
            insertReqProps(reqProps);
        }
    }

    public static String getAuth() throws IOException
    {
        String respMsg = getPageData();//c.getResponseMessage();
        int index = respMsg.indexOf("Auth");
        return respMsg.substring(index+5, respMsg.indexOf("\n", index));
    }

    public static String getPageDataChunk(String lookingFor) throws IOException
    {
        String dataString = "";
        DataInputStream dis = null;
        ByteArrayOutputStream baos = null;
        int chunkSize = 300;
        try{

            dis = c.openDataInputStream();
            baos = new ByteArrayOutputStream();
            byte[] data = new byte[chunkSize];
            int dataSizeRead = 0;//size of data read from input stream.
            for(;(dataSizeRead = dis.read(data))!= -1;data = new byte[chunkSize*=2])
            {
                 baos.write(data, 0, dataSizeRead);
                 dataString = new String (baos.toByteArray());
                 if(dataString.indexOf(lookingFor) >= 0)
                     break;
//                     System.out.println("Data Size Read = "+dataSizeRead);
            }
            
        }
        catch(Exception ignore){}
        
        finally{
            baos.flush();
            dis.close();
            baos.close();
            return dataString;
        }
        
    }

    public static String getPageData() throws IOException
    {
        String dataString = "";
        DataInputStream dis = null;
        ByteArrayOutputStream baos = null;

        try{
            int length = (int)c.getLength();
            dis = c.openDataInputStream();
            baos = new ByteArrayOutputStream();
            byte[] data = null;
            if(length == -1)
            {
                int chunkSize = 1500;
                data = new byte[chunkSize];
                int dataSizeRead = 0;//size of data read from input stream.
                while((dataSizeRead = dis.read(data))!= -1)
                {
                     baos.write(data, 0, dataSizeRead );
//                     System.out.println("Data Size Read = "+dataSizeRead);
                }
                dataString = new String(baos.toByteArray());
            }
            else
            {
                data = new byte[length];
                dis.readFully(data);
                dataString = new String(data);
            }
        }
        catch(IOException ex)
        {
            Logger.add("connMgr", "getPageData", ex.getMessage());
            throw ex;
        }
        finally{
            baos.flush();
            dis.close();
            baos.close();
    //        System.out.println(dataString);
            return dataString;
        }
    }

    public static String get_rnr_se() throws IOException
    {
        String check = getPageData();
        String rnrVal = "";
        if(settings.getGVNumber() == null || settings.getGVNumber().equals(""))
        {
            int numberInd = check.indexOf(beginNumber) + beginNumber.length();

            String number = check.substring(numberInd, check.indexOf(endNumber)).trim();
            StringBuffer numberBuf = new StringBuffer();
            numberBuf.append(number.substring(1, 4));
            numberBuf.append(number.substring(6, 9));
            numberBuf.append(number.substring(10));

            number = new String(numberBuf);
            settings.setGVNumber(number);
     //       System.out.println(number);
        }
        int rnrInd = check.indexOf("_rnr_se");

        if(rnrInd > -1){
            rnrInd = check.indexOf("value=\"", rnrInd);
            int lastIndex = check.indexOf("\"", rnrInd+8);
            rnrVal = check.substring(rnrInd+7, lastIndex);
        }
        else if(check.indexOf("incorrect") >= 0)
        {
            throw new IOException("Invalid Username or Password");
        }
        else if(rnrVal.equals(""))
        {
            throw new IOException("rnr blank");
        }
        return rnrVal;
    }

    public static void initReqProps()
    {
        String[] contentType = {"Content-Type", "application/x-www-form-urlencoded"};
        String[] connection = {"Connection", "keep-alive"};
        reqProps.addElement(contentType);
        reqProps.addElement(connection);
    }

    public static void addReqProp(String[] reqProp)
    {
        reqProps.addElement(reqProp);
    }

    public static Vector getReqProps()
    {
        return reqProps;
    }

    private static void insertReqProps(Vector custReqProps) throws IOException
    {
        for(int i = reqProps.size() - 1; i >= 0; i--)
        {
            String[] props = (String[]) reqProps.elementAt(i);
            c.setRequestProperty(props[0], props[1]);
        }
        if(custReqProps != null)
        {
            for(int i = custReqProps.size() - 1; i >= 0; i--)
            {
                String[] props = (String[]) custReqProps.elementAt(i);
                c.setRequestProperty(props[0], props[1]);
            }
        }
    }
    
    public static String getHttpsConnectionValue()
    {
        try{
            return c.toString();
        }
        catch(Exception ex)
        {
            return "null";
        }
    }
    
    public static void close() throws IOException
    {
        RMSCookieConnector.close(c, null, null);
        try{
            c.close();
        }
        catch(Exception ignore){}
        c = null;
    }
}