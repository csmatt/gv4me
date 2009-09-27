/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.gvLogin;
import gvME.gvME;
import java.io.IOException;
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
 *
 * @author matt
 */
public class Login extends WaitScreen implements CommandListener {
    private Command loginCmd, loginAgainCmd, cancelLoginCmd;
    private TextField usernameTF, passwordTF;
    private Form loginScreen;
    private Alert loginFailedAlert, noConAlert;
    private String username, password;
    private String exceptionType = "";
    private Image image;
    private gvME midlet;
    
    public Login(String username, String password, gvME midlet)
    {
        super(gvME.dispMan.getDisplay());
   //     this.userSettings = gvME.userSettings;
        this.username = username;
        this.password = password;
        this.midlet = midlet;
        setTitle("Logging In...");
        setCommandListener(this);
        setImage(getImage());
        setText("");
        setTask(getSimpleCancellableTask());
    }

    public Image getImage() {
        if (image == null) {
            try {
                image = Image.createImage("/pics/gvIcon.png");
                //image from Matthew Rex Downham
            } catch (java.io.IOException e) {
                e.printStackTrace();
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
            passwordTF = new TextField("Password", "", 40, TextField.PASSWORD);
        }
        return passwordTF;
    }

    public Alert getLoginFailedAlert() {
        if(loginFailedAlert == null)
        {
            loginFailedAlert = new Alert("Login Error", "Invalid username or password!", null, AlertType.WARNING);
            loginFailedAlert.setTimeout(2000);
        }
        return loginFailedAlert;
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
            cancelLoginCmd = new Command("Quit", Command.CANCEL, 0);
        }
        return cancelLoginCmd;
    }

    public void commandAction(Command command, Displayable displayable) {
        if (displayable == loginScreen) {
            if (command == loginCmd) {
                try {
                    this.username = usernameTF.getString();
                    this.password = passwordTF.getString();
                    gvLogin.setLoginInfo(username, password);
                    gvME.dispMan.switchDisplayable(null, this);
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        else if (displayable == noConAlert)
        {
            if(command == loginAgainCmd)
            {
                gvME.dispMan.switchDisplayable(null, new Login(username, password, midlet));
            }
            else if(command == cancelLoginCmd)
            {
                midlet.exitMIDlet();
            }
        }
        else if (displayable == this) {
            if (command == WaitScreen.FAILURE_COMMAND) {
                if(exceptionType == null || exceptionType.equals("cnf"))
                {
                    gvME.dispMan.switchDisplayable(getNoConAlert(), gvME.getMenu());
                }
                else
                {
                    gvME.dispMan.switchDisplayable(getLoginFailedAlert(), getLoginScreen());
                }
            } else if (command == WaitScreen.SUCCESS_COMMAND && loginScreen != null) {
                try {
                    gvLogin.saveLoginInfo();
                } catch (RecordStoreException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public SimpleCancellableTask getSimpleCancellableTask()
    {
        SimpleCancellableTask task = new org.netbeans.microedition.util.SimpleCancellableTask();
        task.setExecutable(new org.netbeans.microedition.util.Executable() {
            public void execute() throws ConnectionNotFoundException, Exception {
                try{
                    gvLogin.initLogin();
                    gvME.dispMan.switchDisplayable(null, gvME.getMenu());
                }
                catch(ConnectionNotFoundException cnf)
                {
                    exceptionType = "cnf";
                    throw cnf;
                }
                catch(Exception e)
                {
                    exceptionType = "inv";
                    throw e;
                }
            }
        });
        return task;
    }
}
