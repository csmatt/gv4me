/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.HttpsConnection;
import javax.microedition.rms.RecordStoreException;
import ui.Login;

/**
 *
 * @author matt
 */
public class gvLogin {
    private static final String logInURL = "https://www.google.com/accounts/LoginAuth?btmpl=mobile&amp;continue=https%3A%2F%2Fwww.google.com%2Fvoice%2Fm&service=grandcentral&ltmpl=mobile";
    private static final String postData = "rememberme=true&ltmpl=mobile&continue=https%3A%2F%2Fwww.google.com%2Fvoice%2Fm&ltmpl=mobile&btmpl=mobile&ltmpl=mobile&rmShown=1&signIn=Sign+in";
    private static Vector reqProps;
    private static String username;
    private static String password;
    private static String requestBody;
    private static Login login;

    public gvLogin(gvME midlet) throws IOException, Exception
    {
        gvLogin.username = settings.getUsername();
        gvLogin.password = settings.getPassword();
        gvLogin.reqProps = parseMsgs.getReqProps();
        login = new Login(username, password, midlet);
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
                        gvLogin.username,
                        "&Passwd=",
                        gvLogin.password,
                        "&service=grandcentral&source=gv4me",
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
        String rnr;
        try{
            c = submitInfo(requestBody);
            rnr = createConnection.get_rnr_se(c);
        }
        catch(ConnectionNotFoundException cnf)
        {
            Logger.add("gvLogin", "logIn", cnf.getMessage());
            throw cnf;
        }
        reqProps.removeElementAt(2);
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
            settings.setUsername(gvLogin.username);
            settings.setPassword(gvLogin.password);
            settings.updateSettings();
    }
}
