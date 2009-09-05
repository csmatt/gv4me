/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.HttpsConnection;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.netbeans.microedition.lcdui.LoginScreen;
import ui.Login;

/**
 *
 * @author matt
 */
public class gvLogin {
    private final String logInURL = "https://www.google.com/accounts/LoginAuth?btmpl=mobile&amp;continue=https%3A%2F%2Fwww.google.com%2Fvoice%2Fm&amp;service=grandcentral&amp;ltmpl=mobile";
    private final String postData = "rememberme=true&ltmpl=mobile&continue=https%3A%2F%2Fwww.google.com%2Fvoice%2Fm&ltmpl=mobile&btmpl=mobile&ltmpl=mobile&rmShown=1&signIn=Sign+in";
    private gvME midlet;
    private Vector reqProps;
    private String rnr;
    private Login login;
    private LoginScreen loginScreen;

    public gvLogin(gvME midlet, Login login)
    {
        initialize(midlet);
        this.login = login;
        this.loginScreen = midlet.loginScreen;
    }

    public gvLogin(gvME midlet) {
        initialize(midlet);
    }

    public void initialize(gvME midlet)
    {
        this.midlet = midlet;
        this.reqProps = parseMsgs.getReqProps();
        this.rnr = midlet.rnr;
    }

    public void logIn(settings userSettings) throws IOException, Exception
    {
        String[] reqBodyArray = {
                                "accountType=GOOGLE&Email=",
                                userSettings.getUsername(),
                                "&Passwd=",
                                userSettings.getPassword(),
                                "&service=grandcentral&source=gvSMS",
                                postData
                                };
        String requestBody =  tools.combineStrings(reqBodyArray);

        String[] props = {"Content-Length", String.valueOf(requestBody.length())};
        reqProps.insertElementAt(props, 2);

        HttpsConnection c = createConnection.open(logInURL, "POST", reqProps , requestBody);

        reqProps.removeElementAt(2);
        String loc = c.getHeaderField("Location");
        createConnection.close(c);
        c = null;

        if(loc != null)
        {
            HttpsConnection checkCookie = createConnection.open(loc, "GET", reqProps, null);

            loc = checkCookie.getHeaderField("Location");

            createConnection.close(checkCookie);
            checkCookie = null;

            HttpsConnection rnrCon = createConnection.open(loc, "GET", reqProps, null);
            rnr = createConnection.get_rnr_se(rnrCon);
            System.out.println("rnr= "+rnr);
            createConnection.close(rnrCon);
            rnrCon = null;

            //send rnr back to gvME so other classes have access
            midlet.rnr = this.rnr;
        }
        else
        {
            throw new Exception("Login Failed. Bad username or password");
        }
    }

    synchronized public void saveLoginInfo(settings userSettings) throws RecordStoreException, RecordStoreException
    {
        try {
            String username = loginScreen.getUsername();
            String password = loginScreen.getPassword();
            userSettings.setUsername(username);
            userSettings.setPassword(password);
            userSettings.updateSettings();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
        }
    }
}
