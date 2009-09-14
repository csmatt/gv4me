
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
public class createConnection{
    public static synchronized HttpsConnection open(String url, String reqMethod, Vector reqProps, String postData) throws IOException, Exception
    {
        int respCode = 0;
        String loc = "";
        String[] props = null;
        HttpsConnection c = (HttpsConnection) RMSCookieConnector.open(url);
        try{
            c.setRequestMethod(reqMethod);

            for(int i = 0; reqProps.elementAt(i).equals(null) && i < reqProps.size(); i++)
            {
                props = (String[]) reqProps.elementAt(i);
                c.setRequestProperty(props[0], props[1]);
            }

            if(reqMethod.equals("POST") && !postData.equals(""))
            {
                OutputStream os = c.openOutputStream();
                os.write(postData.getBytes());
                os.close();
                os = null;
            }

            RMSCookieConnector.getCookie(c);
            loc = c.getHeaderField("Location");
            respCode = c.getResponseCode();
            
//            while(respCode != 200 && respCode == 302 && loc != null)
//            {
//                c = RMSCookieConnector.open(loc);
//                respCode = c.getResponseCode();
//                loc = c.getHeaderField("Location");
//                System.out.println(String.valueOf(respCode));
//                System.out.println(loc);
//            }

        }
        catch(ConnectionNotFoundException cnf)
        {
            throw cnf;
        }
        finally
        {
            return c;
        }
    }
    
    public static String getPageData(HttpsConnection c) throws IOException
    {
        DataInputStream dis = c.openDataInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read = dis.read(buffer);
        System.out.println(c.getURL());
        while(read != -1)
        {
            baos.write(buffer,0,read);
            read = dis.read(buffer);
        }

        return (new String(baos.toByteArray()));
    }

    public static String get_rnr_se(HttpsConnection c) throws IOException
    {
        String check = getPageData(c);
        String rnrVal = "";
        int rnrInd = 0;

        rnrInd = check.indexOf("_rnr_se");

        if(rnrInd > -1){
        rnrInd = check.indexOf("value=\"", rnrInd);

        int lastIndex = check.indexOf("\"", rnrInd+8);

        rnrVal = check.substring(rnrInd+7, lastIndex);
        //  System.out.println("rnrVal= "+rnrVal);
        //  System.out.println(check);
        }
        return rnrVal;
    }

    public static void close(HttpsConnection c)
    {
        RMSCookieConnector.close(c, null, null);
    }
}