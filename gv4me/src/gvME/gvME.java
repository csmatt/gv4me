

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;


import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.netbeans.microedition.lcdui.LoginScreen;
import org.netbeans.microedition.lcdui.WaitScreen;
import ui.*;

/**
 * @author matt
 */
public class gvME extends MIDlet implements CommandListener {
    private String cookieStoreName = "cookieStore";
    private boolean midletPaused = false;
    
    private Timer timer;
    private long timerDelay = 30000;
    public String rnr = "";
    public settings userSettings = new settings();
    public LoginScreen loginScreen;
    public DisplayManager dispMan;
    
    private Command exitCommand;
    private Command exitGVSMS;
    private Command backFromInbox;
    private Command minimize;
    private Command delInboxItem;
    private Command backFromSettings;
    private Command okSaveSettings;
    private Command okMakeCall;
    private List menu;
    private WriteMsg newSMS;
    private Inbox InboxList;
    private Form changeSettingsMenu;
    private TextField passwordTextField;
    private TextField usernameTextField;
    private TextField callFromTextField;
    private TextField intervalTextField;
    private WaitScreen callWaitScreen;
    private Command backFromMakeCall;
    private Login loginUI;
    private static int numNewMsgs;

    public static int countCons = 0;

    public gvME()
    {

    }

    /*
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() throws InvalidRecordIDException, IOException, RecordStoreNotOpenException, RecordStoreException {//GEN-END:|0-initialize|0|0-preInitialize

        dispMan = new DisplayManager(this);
        //sets locally stored conversations
        parseMsgs.initStoredConvos();

        this.userSettings = new settings();

        try {
            RecordStore cookieRS = RecordStore.openRecordStore(cookieStoreName, true);
            if(cookieRS.getNumRecords() != 0)
            {
                cookieRS.closeRecordStore();
                RecordStore.deleteRecordStore(cookieStoreName);
            }
        }
        catch(Exception e)
        {
          //ignore. just here to catch RS not found exception
        }
    }

    public void startMIDlet() throws IOException, Exception {  
        checkLoginInfo();
        createTimer();
    }

    public void resumeMIDlet() {
        dispMan.getDisplay();
    }

//<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        
        if (displayable == loginScreen)
        {
            if (command == LoginScreen.LOGIN_COMMAND)
            {
                userSettings.setUsername(loginScreen.getUsername());
                userSettings.setPassword(loginScreen.getPassword());
                loginUI = new Login(this);
                dispMan.switchDisplayable(null, loginUI);
            }
        }
         if (displayable == callWaitScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {
//TODO
            } else if (command == WaitScreen.SUCCESS_COMMAND) {
                dispMan.switchDisplayable(null, getMenu());
            }
        } else if (displayable == changeSettingsMenu) {
            if (command == backFromSettings) {
                dispMan.switchDisplayable(null, getMenu());
            } else if (command == okSaveSettings) {
                try {
                    changeSettings();
                } catch (RecordStoreException ex) {
                    ex.printStackTrace();
                }
                dispMan.switchDisplayable(null, getMenu());
            }
        } else if (displayable == menu) {
            if (command == List.SELECT_COMMAND) {
                try {
                    menuAction();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }                
            } else if (command == exitGVSMS) {               
                exitMIDlet();              
            } else if (command == minimize) {
                this.pauseApp();
            }
        }
    }

    public LoginScreen createLoginScreen()
    {
        LoginScreen ls = new LoginScreen(dispMan.getDisplay());
	    ls.setLabelTexts("Username", "Password");
	    ls.setTitle("GV Login");
	    ls.addCommand(LoginScreen.LOGIN_COMMAND);
	    ls.setCommandListener(this);
	    ls.setBGColor(-3355444);
	    ls.setFGColor(0);
	    ls.setUseLoginButton(false);
	    ls.setLoginButtonText("Login");
        return ls;
    }

    synchronized public void createTimer()
    {
        timer = new Timer();
        timer.schedule(new checkInbox(), timerDelay, Long.parseLong(userSettings.getCheckInterval())*1000);
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommand ">//GEN-BEGIN:|18-getter|0|18-preInit
    /**
     * Returns an initiliazed instance of exitCommand component.
     * @return the initialized component instance
     */
    public Command getExitCommand() {
        if (exitCommand == null) {//GEN-END:|18-getter|0|18-preInit
            // write pre-init user code here
            exitCommand = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|18-getter|1|18-postInit
            
        }//GEN-BEGIN:|18-getter|2|
        return exitCommand;
    }
    //</editor-fold>

    public void changeSettings() throws RecordStoreException
    {
        String interval = userSettings.getCheckInterval();
        String username = userSettings.getUsername();
        String callFrom = userSettings.getCallFrom();
        String tfInterval = intervalTextField.getString();
        String tfUsername = usernameTextField.getString();
        String tfPassword = passwordTextField.getString();
        String tfCallFrom = callFromTextField.getString();

        if(!tfInterval.equals(interval))
        {
            userSettings.setCheckInterval(tfInterval);
            timer.cancel();
            createTimer();
        }
        if(!tfUsername.equals(username))
        {
            userSettings.setUsername(tfUsername);
        }
        if(!tfPassword.equals(""))
        {
            userSettings.setPassword(tfPassword);
        }
        if(!tfCallFrom.equals(callFrom) && !tfCallFrom.equals(""))
        {
            userSettings.setCallFrom(tfCallFrom);
        }
        userSettings.updateSettings();
    }

    /**
     * Returns an initiliazed instance of menu component.
     * @return the initialized component instance
     */
    public List getMenu() {
        if (menu == null) {//GEN-END:|24-getter|0|24-preInit
            // write pre-init user code here
            menu = new List("Menu", Choice.IMPLICIT);//GEN-BEGIN:|24-getter|1|24-postInit
            menu.append("Write New", null);
            menu.append("Inbox", null);
            menu.append("Settings", null);
            menu.append("Make Call", null);
            menu.addCommand(getExitGVSMS());
            menu.addCommand(getMinimize());
            menu.setCommandListener(this);
            menu.setSelectedFlags(new boolean[] { false, false, false, false });//GEN-END:|24-getter|1|24-postInit
        }
        return menu;
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: menuAction ">//GEN-BEGIN:|24-action|0|24-preAction
    /**
     * Performs an action assigned to the selected list element in the menu component.
     */
    public void menuAction() throws IOException, Exception {//GEN-END:|24-action|0|24-preAction
        String __selectedString = getMenu().getString(getMenu().getSelectedIndex());//GEN-BEGIN:|24-action|1|28-preAction
        if (__selectedString != null) {
            if (__selectedString.equals("Write New")) {//GEN-END:|24-action|1|28-preAction
                
                if(newSMS != null)
                    newSMS.setString("");
                newSMS = new WriteMsg(this, "Write New", null);
                dispMan.switchDisplayable(null, newSMS);
            } else if (__selectedString.equals("Inbox")) {               
                    dispMan.switchDisplayable(null, getInbox());               
            } else if (__selectedString.equals("Settings")) {            
                dispMan.switchDisplayable(null, getChangeSettingsMenu());                
            } else if (__selectedString.equals("Make Call")) {                
                MakeCall mc = new MakeCall(this);
                ChooseContact cc = new ChooseContact(this, mc);
                dispMan.switchDisplayable(null, cc);                
            }
        }        
    }

    public Inbox getInbox() throws IOException, Exception
    {
        if(InboxList == null)
        {
            InboxList = new Inbox(this);
        }
        InboxList.updateInbox();
        return InboxList;
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitGVSMS ">//GEN-BEGIN:|62-getter|0|62-preInit
    /**
     * Returns an initiliazed instance of exitGVSMS component.
     * @return the initialized component instance
     */
    public Command getExitGVSMS() {
        if (exitGVSMS == null) {//GEN-END:|62-getter|0|62-preInit
            
            exitGVSMS = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|62-getter|1|62-postInit
            
        }
        return exitGVSMS;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backFromInbox ">//GEN-BEGIN:|82-getter|0|82-preInit
    /**
     * Returns an initiliazed instance of backFromInbox component.
     * @return the initialized component instance
     */
    public Command getBackFromInbox() {
        if (backFromInbox == null) {
            backFromInbox = new Command("Back", Command.BACK, 0);//GEN-LINE:|82-getter|1|82-postInit
            
        }
        return backFromInbox;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: minimize ">//GEN-BEGIN:|149-getter|0|149-preInit
    /**
     * Returns an initiliazed instance of minimize component.
     * @return the initialized component instance
     */
    public Command getMinimize() {
        if (minimize == null) {
            minimize = new Command("Minimize", Command.BACK, 0);
            
        }
        return minimize;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: delInboxItem ">//GEN-BEGIN:|161-getter|0|161-preInit
    /**
     * Returns an initiliazed instance of delInboxItem component.
     * @return the initialized component instance
     */
    public Command getDelInboxItem() {
        if (delInboxItem == null) {
            delInboxItem = new Command("Delete", Command.ITEM, 1);//GEN-LINE:|161-getter|1|161-postInit
            
        }
        return delInboxItem;
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: checkLoginInfo ">//GEN-BEGIN:|191-if|0|191-preIf
    /**
     * Performs an action assigned to the checkLoginInfo if-point.
     */
    public void checkLoginInfo() throws IOException, Exception {
        if(loginUI == null)
        {
            loginUI = new Login(this);
        }
        if (!userSettings.getUsername().equals("") && !userSettings.getPassword().equals("")) {//GEN-LINE:|191-if|1|192-preAction
            dispMan.switchDisplayable(null, loginUI);
        } else {
            this.loginScreen = createLoginScreen();
            dispMan.switchDisplayable(null, this.loginScreen);
        }
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okMakeCall ">//GEN-BEGIN:|226-getter|0|226-preInit
    /**
     * Returns an initiliazed instance of okMakeCall component.
     * @return the initialized component instance
     */
    public Command getOkMakeCall() {
        if (okMakeCall == null) {
            okMakeCall = new Command("Ok", Command.OK, 0);
            
        }
        return okMakeCall;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backFromEnterPhone ">//GEN-BEGIN:|228-getter|0|228-preInit
    /**
     * Returns an initiliazed instance of backFromEnterPhone component.
     * @return the initialized component instance
     */
    public Command getBackFromMakeCall() {
        if (backFromMakeCall == null) {
            backFromMakeCall = new Command("Back", Command.BACK, 0);
            
        }
        return backFromMakeCall;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okSaveSettings ">//GEN-BEGIN:|234-getter|0|234-preInit
    /**
     * Returns an initiliazed instance of okSaveSettings component.
     * @return the initialized component instance
     */
    public Command getOkSaveSettings() {
        if (okSaveSettings == null) {
            okSaveSettings = new Command("Save", Command.OK, 0);            
        }
        return okSaveSettings;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backFromSettings ">//GEN-BEGIN:|236-getter|0|236-preInit
    /**
     * Returns an initiliazed instance of backFromSettings component.
     * @return the initialized component instance
     */
    public Command getBackFromSettings() {
        if (backFromSettings == null) {
            backFromSettings = new Command("Cancel", Command.BACK, 0);            
        }
        return backFromSettings;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: changeSettingsMenu ">//GEN-BEGIN:|233-getter|0|233-preInit
    /**
     * Returns an initiliazed instance of changeSettingsMenu component.
     * @return the initialized component instance
     */
    public Form getChangeSettingsMenu() {
        if (changeSettingsMenu == null) {
            changeSettingsMenu = new Form("Change Settings", new Item[] { getIntervalTextField(), getUsernameTextField(), getPasswordTextField(), getCallFromTextField() });//GEN-BEGIN:|233-getter|1|233-postInit
            changeSettingsMenu.addCommand(getOkSaveSettings());
            changeSettingsMenu.addCommand(getBackFromSettings());
            changeSettingsMenu.setCommandListener(this);
        }
        return changeSettingsMenu;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: usernameTextField ">//GEN-BEGIN:|238-getter|0|238-preInit
    /**
     * Returns an initiliazed instance of usernameTextField component.
     * @return the initialized component instance
     */
    public TextField getUsernameTextField() {
        if (usernameTextField == null) {
            String userName = userSettings.getUsername();
            usernameTextField = new TextField("Username:", userName, 32, TextField.ANY);            
        }
        return usernameTextField;
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: passwordTextField ">//GEN-BEGIN:|239-getter|0|239-preInit
    /**
     * Returns an initiliazed instance of passwordTextField component.
     * @return the initialized component instance
     */
    public TextField getPasswordTextField() {
        if (passwordTextField == null) {
            passwordTextField = new TextField("Password:", null, 32, TextField.PASSWORD);
            
        }
        return passwordTextField;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: intervalTextField ">//GEN-BEGIN:|240-getter|0|240-preInit
    /**
     * Returns an initiliazed instance of intervalTextField component.
     * @return the initialized component instance
     */
    public TextField getIntervalTextField() {
        if (intervalTextField == null) {
            String interval = userSettings.getCheckInterval();
            intervalTextField = new TextField("Check Inbox (secs)", interval, 32, TextField.NUMERIC);//GEN-LINE:|240-getter|1|240-postInit
            
        }
        return intervalTextField;
    }
 
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: callFromTextField ">//GEN-BEGIN:|246-getter|0|246-preInit
    /**
     * Returns an initiliazed instance of callFromTextField component.
     * @return the initialized component instance
     */
    public TextField getCallFromTextField() {
        if (callFromTextField == null) {
            String callFrom = userSettings.getCallFrom();
            callFromTextField = new TextField("Call From:", callFrom, 32, TextField.PHONENUMBER);//GEN-LINE:|246-getter|1|246-postInit
            
        }
        return callFromTextField;
    }

    /**
     * Exits MIDlet.
     */
    public void exitMIDlet() {
        dispMan.switchDisplayable (null, null);
        destroyApp(true);
        notifyDestroyed();
    }

    /**
     * Called when MIDlet is started.
     * Checks whether the MIDlet has already been started and initialize/starts or resumes the MIDlet.
     */
    public void startApp() {
        if (midletPaused) {
            resumeMIDlet ();
        } else {
            
            try {
                initialize ();
                startMIDlet();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp() {
        midletPaused = true;
        dispMan.switchDisplayable (null, null);
    }

    /**
     * Called to signal the MIDlet to terminate.
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional) {
        timer.cancel();
      //  try {
      //      userSettings.updateContacts();
      //  } catch (RecordStoreException ex) {
      //      ex.printStackTrace();
     //   }
    }

    public static void setNumNewMsgs(int newMsgCnt)
    {
        gvME.numNewMsgs = newMsgCnt;
    }
    public class checkInbox extends TimerTask{
        public final void run() {
            try {
                parseMsgs.readMsgs();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(numNewMsgs > 0)
            {
                Alert newMsgAlert = new Alert("New Messages");
                newMsgAlert.setString(numNewMsgs+" new messages");
                try {
                    numNewMsgs = 0;
                    dispMan.switchDisplayable(newMsgAlert, getInbox());
                    dispMan.vibrate(400);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}


