/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.HttpsConnection;
import javax.microedition.rms.RecordStoreException;
import ui.Login;

/**
 *
 * @author matt
 */
public class gvLogin {
    private static final String logInURL = "https://www.google.com/accounts/LoginAuth?btmpl=mobile&amp;continue=https%3A%2F%2Fwww.google.com%2Fvoice%2Fm&amp;service=grandcentral&amp;ltmpl=mobile";
    private static final String postData = "rememberme=true&ltmpl=mobile&continue=https%3A%2F%2Fwww.google.com%2Fvoice%2Fm&ltmpl=mobile&btmpl=mobile&ltmpl=mobile&rmShown=1&signIn=Sign+in";
    private static Vector reqProps;
    private static String username;
    private static String password;
    private Login login;

    public gvLogin() throws IOException, Exception
    {
        gvLogin.username = gvME.userSettings.getUsername();
        gvLogin.password = gvME.userSettings.getPassword();
        gvLogin.reqProps = parseMsgs.getReqProps();
        login = new Login(username, password);
    }

    public void checkLoginInfo() throws IOException, Exception {
        if (!gvLogin.username.equals("") && !gvLogin.password.equals("")) {
            gvME.dispMan.switchDisplayable(null, login);
        }
        else
        {
            gvME.dispMan.switchDisplayable(null, login.getLoginScreen());
        }
    }
    
    public static void setLoginInfo(String username, String password)
    {
        gvLogin.username = username;
        gvLogin.password = password;
    }

    public static void logIn() throws IOException, Exception
    {
        String[] reqBodyArray = {
                                "accountType=GOOGLE&Email=",
                                gvLogin.username, //gvME.userSettings.getUsername(),
                                "&Passwd=",
                                gvLogin.password,//gvME.userSettings.getPassword(),
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
            String rnr = createConnection.get_rnr_se(rnrCon);
            System.out.println("rnr= "+rnr);
            createConnection.close(rnrCon);
            rnrCon = null;

            //set rnr in gvME so other classes have access
            gvME.setRNR(rnr);
        }
        else
        {
            System.out.println(username + " " + password);
            throw new Exception("Login Failed. Bad username or password");
        }
    }

    public static void saveLoginInfo() throws RecordStoreException, RecordStoreException
    {
            gvME.userSettings.setUsername(gvLogin.username);
            gvME.userSettings.setPassword(gvLogin.password);
            gvME.userSettings.updateSettings();
    }
}
