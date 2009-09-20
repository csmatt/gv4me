/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.HttpsConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
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
    private static Command loginAgainCmd;
    private static Command cancelLoginCmd;
    private static String requestBody;
    private static Login login;

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

    public static void initLogin() throws IOException, Exception
    {
        String[] reqBodyArray = {
                        "accountType=GOOGLE&Email=",
                        gvLogin.username, //gvME.userSettings.getUsername(),
                        "&Passwd=",
                        gvLogin.password,//gvME.userSettings.getPassword(),
                        "&service=grandcentral&source=gvSMS",
                        postData
                        };
        requestBody =  tools.combineStrings(reqBodyArray);

        logIn();
        login = null;
    }
    
    public static void logIn() throws IOException, Exception
    {
        HttpsConnection c = null;
        String[] props = {"Content-Length", String.valueOf(requestBody.length())};
        reqProps.insertElementAt(props, 2);
        try{
            c = submitInfo(requestBody);
        }
        catch(ConnectionNotFoundException cnf)
        {
            throw new ConnectionNotFoundException();
        }
        reqProps.removeElementAt(2);

        String rnr = createConnection.get_rnr_se(c);
        System.out.println("rnr= "+rnr);

        //set rnr in gvME so other classes have access
        gvME.setRNR(rnr);
    }

    private static HttpsConnection submitInfo(String requestBody) throws ConnectionNotFoundException, IOException, Exception
    {
            HttpsConnection c = createConnection.open(logInURL, "POST", reqProps , requestBody);
//            String auth = createConnection.getAuth(c);
//            createConnection.close(c);
//            System.out.println(auth);
            return c;
    }

    public static void saveLoginInfo() throws RecordStoreException, RecordStoreException
    {
            gvME.userSettings.setUsername(gvLogin.username);
            gvME.userSettings.setPassword(gvLogin.password);
            gvME.userSettings.updateSettings();
    }
}
