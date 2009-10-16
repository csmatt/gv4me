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
    private static Vector reqProps = new Vector(10);
    private static HttpsConnection c = null;

    public static void initReqProps()
    {
        String[] contentType = {"Content-Type", "application/x-www-form-urlencoded"};
        String[] connection = {"Connection", "keep-alive"};
        reqProps.insertElementAt(contentType, 0);
        reqProps.insertElementAt(connection, 1);
    }

    public static void addReqProp(String[] reqProp)
    {
        reqProps.insertElementAt(reqProp, 2);
    }

    public static Vector getReqProps()
    {
        return reqProps;
    }

    private static void insertReqProps(Vector custReqProps) throws IOException
    {
        for(int i = 0; i < reqProps.size(); i++)
        {
            String[] props = (String[]) reqProps.elementAt(i);
            c.setRequestProperty(props[0], props[1]);
        }
        if(custReqProps != null)
        {
            for(int i = 0; i < custReqProps.size(); i++)
            {
                String[] props = (String[]) custReqProps.elementAt(i);
                c.setRequestProperty(props[0], props[1]);
            }
        }
    }

    public static synchronized void open(String url, String reqMethod, Vector custReqProps, String postData) throws ConnectionNotFoundException, IOException, Exception
    {
        int respCode = 0;
        String loc = "";
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

            RMSCookieConnector.getCookie(c);
            loc = c.getHeaderField("Location");
            respCode = c.getResponseCode();
            System.out.println(String.valueOf(respCode));
            System.out.println(loc);
            for(int i = 0; i < 5 && respCode == 302 && loc != null && !loc.equals(""); i++)
            {//prevent redirect loop from continuing forever
                close();

                c = RMSCookieConnector.open(loc);

                insertReqProps(custReqProps);
                RMSCookieConnector.getCookie(c);
                respCode = c.getResponseCode();
                loc = c.getHeaderField("Location");
                System.out.println(String.valueOf(respCode));
                System.out.println(loc);
            }

          //  System.out.println(getPageData(c));
//            if(respCode == 400)
//            {
//               // Logger.add("connMgr", "got a 400");
//                connMgr.close();
//                RMSCookieConnector.removeCookies();
//                gvLogin.logIn();
//                connMgr.open(url, reqMethod, reqProps, postData);
//            }

        }
        catch(ConnectionNotFoundException cnf)
        {
            Logger.add("createConnection", cnf.getMessage());
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

    public static String getAuth() throws IOException
    {
        String respMsg = getPageData();//c.getResponseMessage();
        int index = respMsg.indexOf("Auth");
        return respMsg.substring(index+5, respMsg.indexOf("\n", index));
    }

    public static String getPageData() throws IOException
    {
        DataInputStream dis = c.openDataInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read = dis.read(buffer);
        //System.out.println(c.getURL());
        while(read != -1)
        {
            baos.write(buffer,0,read);
            read = dis.read(buffer);
        }
        String dataString = new String(baos.toByteArray());
        dis.close();
        baos.close();
//        System.out.println(dataString);
        return dataString;
    }

    public static String get_rnr_se() throws IOException
    {
        String check = getPageData();
//        System.out.println(check);
        String rnrVal = "";
        int rnrInd = 0;

        rnrInd = check.indexOf("_rnr_se");

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

    public static void close() throws IOException
    {
        RMSCookieConnector.close(c, null, null);
        c = null;
    }
}