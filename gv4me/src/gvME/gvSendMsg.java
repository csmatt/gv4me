/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.HttpsConnection;

/**
 *
 * @author matt
 */
public class gvSendMsg {
    private static final String textURL = "https://www.google.com/voice/sms/send";
    private static final String replyURL = "https://www.google.com/voice/m/sendsms";

    public static void sendMsg(textConvo original, String sendingTo, String msg, String rnr, Vector reqProps) throws IOException, Exception
    {
        String[] strings = new String[8];
        String postData = "";
        String url = "";
        String text = URLUTF8Encoder.encode(msg);
        if(original != null) //if this is a reply
        {
            String replyNum = original.getReplyNum();
            sendingTo = replyNum;//.substring(replyNum.indexOf("+1")+2, replyNum.indexOf("&c=1"));
            url = replyURL;
            String[] stringBuff = {"_rnr_se=", rnr, "&number=1", sendingTo, "&id=", original.getMsgID(), "&c=1&smstext=", text};
            strings = stringBuff;
        }
        else
        { //if this is a forward or new message
            url = textURL;
            String[] stringBuff = {"id=&phoneNumber=+1", sendingTo, "&text=", text, "&_rnr_se=", rnr};
            strings = stringBuff;
        }
        postData = tools.combineStrings(strings);
        String[] contentLen = {"Content-Length", String.valueOf(postData.length())};
        reqProps.insertElementAt(contentLen, 2);

        HttpsConnection sendCon = createConnection.open(url, "POST", reqProps, postData);
        createConnection.close(sendCon);

    }
}