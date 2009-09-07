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
public class gvMakeCall {
    private static final String callURL = "https://www.google.com/voice/call/connect";
    private Vector reqProps;
    private String rnr;

    public gvMakeCall()
    {
        this.reqProps = parseMsgs.getReqProps();
        this.rnr = gvME.getRNR();
    }

    public void makeCall(String contacting) throws IOException, Exception
    {
        String[] strings = {"outgoingNumber=", contacting, "&forwardingNumber=+1", gvME.userSettings.getCallFrom(), "&subscriberNumber=undefined&remember=0&_rnr_se=", rnr};
        String postData = tools.combineStrings(strings);
        String[] contentLen = {"Content-Length", String.valueOf(postData.length())};
        reqProps.insertElementAt(contentLen, 2);
        HttpsConnection c = createConnection.open(callURL, "POST", reqProps, postData);
        c.close();
    }
}
