/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.gvLogin;
import gvME.gvME;
import gvME.settings;
import java.io.IOException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStoreException;
import org.netbeans.microedition.lcdui.LoginScreen;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 *
 * @author matt
 */
public class Login extends WaitScreen implements CommandListener {
    private gvME midlet;
    private LoginScreen loginScreen;
    private gvLogin login;
    private Alert loginFailedAlert;
    private settings userSettings;
    private Image image;
    
    public Login(gvME midlet)
    {
        super(midlet.dispMan.getDisplay());
        this.midlet = midlet;
        this.userSettings = midlet.userSettings;
        this.loginScreen = midlet.loginScreen;
        this.login = new gvLogin(midlet, this);

        setTitle("Logging In...");
        setCommandListener(this);
        setImage(getImage());
        setText("");
        setTask(getSimpleCancellableTask());
    }

    /**
     * Returns an initiliazed instance of image component.
     * @return the initialized component instance
     */
    public Image getImage() {
        if (image == null) {
            try {
                image = Image.createImage("/pics/gvIcon.png");
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    public LoginScreen getLoginScreen()
    {
        return this.loginScreen;
    }

    /**
     * Returns an initiliazed instance of loginFailedAlert component.
     * @return the initialized component instance
     */
    public Alert getLoginFailedAlert() {
        loginFailedAlert = new Alert("Login Error", "Invalid username or password!", null, AlertType.WARNING);//GEN-BEGIN:|207-getter|1|207-postInit
        loginFailedAlert.setTimeout(2000);
        if(loginScreen != null)
            loginScreen.setPassword("");
        return loginFailedAlert;
    }
    public void commandAction(Command command, Displayable displayable) {
        if (displayable == loginScreen) {
            if (command == LoginScreen.LOGIN_COMMAND) {
                try {
                    login.logIn(userSettings);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        else if (displayable == this) {
            if (command == WaitScreen.FAILURE_COMMAND) {
                midlet.dispMan.switchDisplayable(getLoginFailedAlert(), getLoginScreen());
            } else if (command == WaitScreen.SUCCESS_COMMAND && midlet.loginScreen != null) {
                try {
                    login.saveLoginInfo(userSettings);
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
            public void execute() throws Exception {
                try{
                    login.logIn(userSettings);
                    midlet.dispMan.switchDisplayable(null, midlet.getMenu());
                }
                catch(Exception e)
                {
                    System.out.println(e.toString());
                    throw e;
                }
            }
        });
        return task;
    }
}
