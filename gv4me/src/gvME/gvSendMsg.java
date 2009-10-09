/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.HttpsConnection;

/**
 *
 * @author matt
 */
public class gvSendMsg {
    private static final String textURL = "https://www.google.com/voice/sms/send";
    private static final String replyURL = "https://www.google.com/voice/m/sendsms";

    public static void sendMsg(textConvo original, String sendingTo, String msg, String rnr, Vector reqProps) throws ConnectionNotFoundException, IOException, Exception
    {
        String[] strings = new String[8];
        String postData = "";
        String url = "";
        String text = URLUTF8Encoder.encode(msg);
        if(original != null) //if this is a reply
        {
            String replyNum = original.getReplyNum();
            sendingTo = replyNum;
            url = replyURL;
            String[] stringBuff = {"_rnr_se=", rnr, "&number=1", sendingTo, "&id=", original.getMsgID(), "&c=1&smstext=", text};
            strings = stringBuff;
            stringBuff = null;
        }
        else
        { //if this is a forward or new message
            url = textURL;
            String[] stringBuff = {"id=&phoneNumber=+1", sendingTo, "&text=", text, "&_rnr_se=", rnr};
            strings = stringBuff;
            stringBuff = null;
        }

        postData = tools.combineStrings(strings);
        String[] contentLen = {"Content-Length", String.valueOf(postData.length())};
        reqProps.insertElementAt(contentLen, 2);
        HttpsConnection sendCon = null;
        try{
            sendCon = createConnection.open(url, "POST", reqProps, postData);
        }
        catch(ConnectionNotFoundException cnf)
        {
            Logger.add("gvSendMsg", cnf.getMessage());
            throw cnf;
        }
        try{
            createConnection.close(sendCon);
        }
        catch(Exception ignore)
        {}
        
    }
}
