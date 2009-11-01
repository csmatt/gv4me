/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.Logger;
import gvME.connMgr;
import gvME.gvME;
import gvME.settings;
import gvME.tools;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.io.ConnectionNotFoundException;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 * Logs user into his/her Google Voice account and, upon successful login, saves user credentials for future logins
 * @author Matt Defenthaler
 */
public class Login extends WaitScreen implements CommandListener {
    private static final String rnrURL = "https://www.google.com/voice/m/i/voicemail?p=1000";
    private static final String clientLoginURL = "https://www.google.com/accounts/ClientLogin";
    private Command loginCmd, loginAgainCmd, cancelLoginCmd;
    private TextField usernameTF, passwordTF;
    private Form loginScreen;
    private Alert invalidCredsAlert, noConAlert, errorAlert;
    private String username, password;
    private String exceptionType = "";
    private Image image;

    /**
     * Login constructor
     * @throws IOException
     * @throws Exception
     */
    public Login() throws IOException, Exception
    {
        super(gvME.dispMan.getDisplay());
        setTitle("Logging In...");
        addCommand(getCancelLoginCmd());
        setCommandListener(this);
        setImage(getImage());
        setText("");
        setTask(getSimpleCancellableTask());
        checkLoginInfo();
    }

    /**
     * Checks to see if the username and password are stored, if they aren't the login screen is displayed
     * @throws IOException
     * @throws Exception
     */
    private void checkLoginInfo() throws IOException, Exception {
        username = settings.getUsername();
        password = settings.getPassword();
        if (!username.equals("") && !password.equals("")) {
            gvME.dispMan.switchDisplayable(null, this);
        }
        else
        {
            gvME.dispMan.switchDisplayable(null, getLoginScreen());
        }
    }

    /**
     * Adds the username and password to the settings class and updates the recordstore with this data
     * @throws RecordStoreException
     * @throws RecordStoreException
     */
    private void saveLoginInfo() throws RecordStoreException
    {
            settings.setUsername(username);
            settings.setPassword(password);
            settings.updateSettings();
    }

    public Image getImage() {
        if (image == null) {
            try {
                image = Image.createImage("/pics/gvIcon.png");
                //image from Matthew Rex Downham
            } catch (IOException ex) {
                Logger.add(getClass().getName(), ex.getMessage());
                ex.printStackTrace();
            }
        }
        return image;
    }

    public Form getLoginScreen()
    {
        if(loginScreen == null)
        {
            loginScreen = new Form("Login");
            loginScreen.append(getUsernameTF());
            loginScreen.append(getPasswordTF());
            loginScreen.addCommand(getLoginCmd());
            loginScreen.addCommand(getCancelLoginCmd());
            loginScreen.setCommandListener(this);
        }
        return loginScreen;
    }

    private TextField getUsernameTF()
    {
        if(usernameTF == null)
        {
            usernameTF = new TextField("Username", "", 40, TextField.ANY);
        }
        return usernameTF;
    }

    private TextField getPasswordTF()
    {
        if(passwordTF == null)
        {
            passwordTF = new TextField("Password", "", 40, TextField.PASSWORD | TextField.ANY);
        }
        return passwordTF;
    }

    public Alert getInvalidCredsAlert() {
        if(invalidCredsAlert == null)
        {
            invalidCredsAlert = new Alert("Login Error", "Invalid username or password!", null, AlertType.WARNING);
            invalidCredsAlert.setTimeout(2000);
        }
        return invalidCredsAlert;
    }

    public Alert getErrorAlert(String info)
    {
        if(errorAlert == null)
        {
            errorAlert = new Alert("Error", info , null, AlertType.WARNING);
            errorAlert.addCommand(getLoginAgainCmd());
            errorAlert.addCommand(getCancelLoginCmd());
            errorAlert.setCommandListener(this);
        }
        return errorAlert;
    }

    public Alert getNoConAlert()
    {
        if(noConAlert == null)
        {
            noConAlert = new Alert("Couldn't Connect", "No network found.", null, AlertType.WARNING);
            noConAlert.addCommand(getLoginAgainCmd());
            noConAlert.addCommand(getCancelLoginCmd());
            noConAlert.setCommandListener(this);
        }
        return noConAlert;
    }

    private Command getLoginCmd() {
        if (loginCmd == null) {
            loginCmd = new Command("Login", Command.OK, 1);
        }
        return loginCmd;
    }

    private Command getLoginAgainCmd() {
        if (loginAgainCmd == null) {
            loginAgainCmd = new Command("Try Again", Command.OK, 0);
        }
        return loginAgainCmd;
    }

    private Command getCancelLoginCmd() {
        if (cancelLoginCmd == null) {
            cancelLoginCmd = new Command("Cancel", Command.CANCEL, 0);
        }
        return cancelLoginCmd;
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == cancelLoginCmd)
        {
            gvME.dispMan.switchDisplayable(null, gvME.getMenu());
        }
        else if (displayable == loginScreen) {
            if (command == loginCmd) {
                try {
                    this.username = usernameTF.getString();
                    this.password = passwordTF.getString();
                    gvME.dispMan.switchDisplayable(null, this);
                }catch (Exception ex) {
                    Logger.add(getClass().getName(), ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
        else if (displayable == noConAlert || displayable == errorAlert)
        {
            if(command == loginAgainCmd)
            {
                try {
                    gvME.dispMan.switchDisplayable(null, new Login());
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
           // else if(command == cancelLoginCmd)
           // {
           //     gvME.dispMan.switchDisplayable(null, gvME.getMenu());
            //}
        }
        else if (displayable == this) {
            if (command == WaitScreen.FAILURE_COMMAND) { //An exception was thrown during login
                if(exceptionType == null || exceptionType.equals("cnf"))
                {//ConnectionNotFoundException was thrown
                    gvME.dispMan.switchDisplayable(getNoConAlert(), gvME.getMenu());
                }
                else if(exceptionType.equals("inv"))
                {//A 4XX response was received from the server indicating invalid username or password
                    gvME.dispMan.switchDisplayable(getInvalidCredsAlert(), getLoginScreen());
                }
                else
                {//an unknown error was thrown
                    gvME.dispMan.switchDisplayable(getErrorAlert(exceptionType), gvME.getMenu());
                }
            } else if (command == WaitScreen.SUCCESS_COMMAND) 
                {//the username and password successfully logged the user in
                if(loginScreen != null){
                    try {//save the username and password only if the user just entered them
                        saveLoginInfo();
                    } catch (RecordStoreException ex) {
                        Logger.add(getClass().getName(), ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                gvME.createTimer();
            }
            else if(command == cancelLoginCmd)
            {
                try {
                    connMgr.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                gvME.dispMan.switchDisplayable(null, gvME.getMenu());
            }
        }
    }

    /**
     * Posts login data and retrieves rnr value and ClientLogin auth token
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws Exception
     */
    private void initLogin() throws ConnectionNotFoundException, IOException, Exception
    {
        String[] reqBodyArray = {
                        "accountType=GOOGLE&Email=",
                        username,
                        "&Passwd=",
                        password,
                        "&service=grandcentral&source=gv4me"
                        };
        String requestBody =  tools.combineStrings(reqBodyArray);

        Vector reqProps = new Vector(5);
        String[] contentLength = {"Content-Length", String.valueOf(requestBody.length())};
        reqProps.addElement(contentLength);
        connMgr.open(clientLoginURL, "POST", reqProps, requestBody);
        String auth = connMgr.getAuth();
        int respCode = connMgr.getResponseCode();
        connMgr.close();
        if(respCode >= 400 && respCode < 500)
            throw new IOException("Invalid Username or Password");
        System.out.println(auth);
        gvME.setAuth(auth);

        String[] combined = {rnrURL, "&auth=", auth};
        connMgr.open(tools.combineStrings(combined), "GET", null, "");
        String rnr = connMgr.get_rnr_se();
        connMgr.close();
        gvME.setRNR(rnr);
    }

    public SimpleCancellableTask getSimpleCancellableTask()
    {
        SimpleCancellableTask task = new org.netbeans.microedition.util.SimpleCancellableTask();
        task.setExecutable(new org.netbeans.microedition.util.Executable() {
            public void execute() throws ConnectionNotFoundException, Exception {
                try{
                    initLogin();
                    gvME.dispMan.switchDisplayable(null, gvME.getMenu());
                }
                catch(ConnectionNotFoundException cnf)
                {
                    exceptionType = "cnf";
                    Logger.add(getClass().getName(), cnf.getMessage());
                    throw cnf;
                }
                catch(IOException ioe)
                {
                    if(ioe.getMessage().equals("Invalid Username or Password"))
                    {
                        exceptionType = "inv";
                    }
                    Logger.add(getClass().getName(), ioe.getMessage());
                    throw ioe;
                }
                catch(Exception ex)
                {
                    exceptionType = ex.getMessage() + " " + ex.toString();
                    Logger.add(getClass().getName(), ex.getMessage());
                    throw ex;
                }
            }
        });
        return task;
    }
}
