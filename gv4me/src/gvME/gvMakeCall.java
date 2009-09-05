/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.HttpsConnection;

/**
 *
 * @author matt
 */
public class gvMakeCall {
    private gvME midlet;
    private Vector reqProps;
    private settings userSettings;
    private String rnr;

    public gvMakeCall(gvME midlet)
    {
        this.midlet = midlet;
        this.reqProps = parseMsgs.getReqProps();
        this.userSettings = midlet.userSettings;
        this.rnr = midlet.rnr;
    }

    public void makeCall(String contacting) throws IOException, Exception
    {
        String[] strings = {"outgoingNumber=", contacting, "&forwardingNumber=+1", userSettings.getCallFrom(), "&subscriberNumber=undefined&remember=0&_rnr_se=", rnr};
        String postData = tools.combineStrings(strings);
        System.out.println(userSettings.getCallFrom());
        String[] contentLen = {"Content-Length", String.valueOf(postData.length())};
        reqProps.insertElementAt(contentLen, 2);
        HttpsConnection c = createConnection.open("https://www.google.com/voice/call/connect", "POST", reqProps, postData);
                    DataInputStream dis = c.openDataInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read = dis.read(buffer);

            while(read != -1)
            {
                baos.write(buffer,0,read);
                read = dis.read(buffer);
            }
            dis.close();
            createConnection.close(c);
           String html = new String(baos.toByteArray());
           System.out.println(html);
        c.close();

    }
}
