/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.gvLogin;
import gvME.gvME;
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
    private static Command loginAgainCmd;
    private static Command cancelLoginCmd;
    private LoginScreen loginScreen;
    private Alert loginFailedAlert;
    public static Alert noConAlert;
 //   private settings userSettings;
    private String username, password;
    private Image image;
    
    public Login(String username, String password)
    {
        super(gvME.dispMan.getDisplay());
   //     this.userSettings = gvME.userSettings;
        this.username = username;
        this.password = password;
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
        if(loginScreen == null)
        {
            loginScreen = new LoginScreen(gvME.dispMan.getDisplay());
	    loginScreen.setLabelTexts("Username", "Password");
	    loginScreen.setTitle("GV Login");
	    loginScreen.addCommand(LoginScreen.LOGIN_COMMAND);
	    loginScreen.setCommandListener(this);
	    loginScreen.setBGColor(-3355444);
	    loginScreen.setFGColor(0);
	    loginScreen.setUseLoginButton(false);
	    loginScreen.setLoginButtonText("Login");
        }
        loginScreen.setPassword("");
        return this.loginScreen;
    }

    /**
     * Returns an initiliazed instance of loginFailedAlert component.
     * @return the initialized component instance
     */
    public Alert getLoginFailedAlert() {
        if(loginFailedAlert == null)
        {
            loginFailedAlert = new Alert("Login Error", "Invalid username or password!", null, AlertType.WARNING);//GEN-BEGIN:|207-getter|1|207-postInit
            loginFailedAlert.setTimeout(2000);
        }
        return loginFailedAlert;
    }


    private static Command getLoginAgainCmd() {
        if (loginAgainCmd == null) {
            loginAgainCmd = new Command("Try Again", Command.OK, 1);
        }
        return loginAgainCmd;
    }

    private static Command getCancelLoginCmd() {
        if (cancelLoginCmd == null) {
            cancelLoginCmd = new Command("Cancel", Command.CANCEL, 0);
        }
        return cancelLoginCmd;
    }

    public void commandAction(Command command, Displayable displayable) {
        if (displayable == loginScreen) {
            if (command == LoginScreen.LOGIN_COMMAND) {
                try {
                    this.username = loginScreen.getUsername();
                    this.password = loginScreen.getPassword();
                    gvLogin.setLoginInfo(username, password);
                    gvME.dispMan.switchDisplayable(null, this);
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        else if (displayable == this) {
            if (command == WaitScreen.FAILURE_COMMAND) {
                gvME.dispMan.switchDisplayable(getLoginFailedAlert(), getLoginScreen());
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
            public void execute() throws Exception {
                try{
                    gvLogin.logIn();         
                    gvME.dispMan.switchDisplayable(null, gvME.getMenu());
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
