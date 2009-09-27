

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;


import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.netbeans.microedition.lcdui.WaitScreen;
import ui.*;

/**
 * @author matt
 */
public class gvME extends MIDlet implements CommandListener {
    private final String sentBoxStore = "sentBoxStore";
    private final String outboxStore = "outboxStore";
    private boolean midletPaused = false;    
    private static Timer timer;
    private static long timerDelay = 30000;
    private static String rnr;
    public static settings userSettings;
    public static DisplayManager dispMan;
    private static Command exitCmd;
    private static Command minimize;
    private static Command backFromInbox;
    private static Command backFromSettings;
    private static Command okSaveSettings;
    private static Command okMakeCall;
    private static List menu;
    private static WriteMsg newSMS;
    private static Inbox InboxList;
    private Form changeSettingsMenu;
    private TextField passwordTextField;
    private TextField usernameTextField;
    private TextField callFromTextField;
    private TextField intervalTextField;
    private WaitScreen callWaitScreen;
    private Command backFromMakeCall;
    private static CommandListener cl;
    private static int numNewMsgs;
    public static int countCons;
    public static MailBox SentBox;
    public static Outbox outbox;

    /*
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() throws InvalidRecordIDException, IOException, RecordStoreNotOpenException, RecordStoreException {//GEN-END:|0-initialize|0|0-preInitialize
        userSettings = new settings();
        countCons = 0;
        dispMan = new DisplayManager(this);
        cl = this; //reference to 'this' for CommandListener of static method getMenu()
        SentBox = getSentBox();
        outbox = getOutbox();
        parseMsgs.setReqProps();
        try {
            RMSCookieConnector.removeCookies();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    public void startMIDlet() throws IOException, Exception {  
        gvLogin waitForLogin = new gvLogin(this);
        waitForLogin.checkLoginInfo();
        waitForLogin = null;
        createTimer();
    }

    public void resumeMIDlet() {
        dispMan.getDisplay();
    }

    public void commandAction(Command command, Displayable displayable) {
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
            } else if (command == exitCmd) {               
                exitMIDlet();              
            } else if (command == minimize) {
                this.pauseApp();
            }
        }
    }

    public static void setRNR(String rnr)
    {
        gvME.rnr = rnr;
    }

    public static String getRNR()
    {
        return gvME.rnr;
    }

    synchronized public static void createTimer()
    {
        timer = new Timer();
        timer.schedule(new checkInbox(), timerDelay, Long.parseLong(userSettings.getCheckInterval())*1000);
    }

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

    public static List getMenu() {
        if (menu == null) {
            menu = new List("Menu", Choice.IMPLICIT);
            menu.append("Write New", null);
            menu.append("Inbox", null);
            menu.append("Make Call", null);
            menu.append("Sent Box", null);
            menu.append("Outbox", null);
            menu.append("Settings", null);
            menu.addCommand(getExitCmd());
            menu.addCommand(getMinimize());
            menu.setCommandListener(cl);
        }
        return menu;
    }

    public void menuAction() throws IOException, Exception {
        String __selectedString = getMenu().getString(getMenu().getSelectedIndex());
        if (__selectedString != null) {
            if (__selectedString.equals("Write New")) {
                if(newSMS != null)
                    newSMS.setString("");
                    newSMS = new WriteMsg("Write New", null);
                    dispMan.switchDisplayable(null, newSMS);
            } else if (__selectedString.equals("Inbox")) {               
                dispMan.switchDisplayable(null, getInbox());
            } else if (__selectedString.equals("Outbox")) {
                dispMan.switchDisplayable(null, getOutbox());
            } else if (__selectedString.equals("Sent Box")) {
                dispMan.switchDisplayable(null, getSentBox());
            } else if (__selectedString.equals("Settings")) {            
                dispMan.switchDisplayable(null, getChangeSettingsMenu());                
            } else if (__selectedString.equals("Make Call")) {                
                MakeCall mc = new MakeCall();
                ChooseContact cc = new ChooseContact(getMenu(), mc);
                dispMan.switchDisplayable(null, cc);                
            }
        }        
    }

    public MailBox getSentBox() throws RecordStoreException, IOException
    {
        if(SentBox == null)
        {
            SentBox = new MailBox("Sent Box", sentBoxStore);
        }
        return SentBox;
    }

    public static Inbox getInbox() throws IOException, Exception
    {
        if(InboxList == null)
        {
            InboxList = new Inbox();
        }
        return InboxList;
    }

    public static Outbox getOutbox() throws RecordStoreException, IOException
    {
        if(outbox == null)
        {
            outbox = new Outbox();
        }
        return outbox;
    }

    public static Command getExitCmd() {
        if (exitCmd == null) {
            exitCmd = new Command("Exit", Command.EXIT, 0);
        }
        return exitCmd;
    }

    public Command getBackFromInbox() {
        if (backFromInbox == null) {
            backFromInbox = new Command("Back", Command.BACK, 0);//GEN-LINE:|82-getter|1|82-postInit
            
        }
        return backFromInbox;
    }

    public static Command getMinimize() {
        if (minimize == null) {
            minimize = new Command("Minimize", Command.BACK, 0);        
        }
        return minimize;
    }

    public Command getOkMakeCall() {
        if (okMakeCall == null) {
            okMakeCall = new Command("Ok", Command.OK, 0);
            
        }
        return okMakeCall;
    }

    public Command getBackFromMakeCall() {
        if (backFromMakeCall == null) {
            backFromMakeCall = new Command("Back", Command.BACK, 0);            
        }
        return backFromMakeCall;
    }

    public Command getOkSaveSettings() {
        if (okSaveSettings == null) {
            okSaveSettings = new Command("Save", Command.OK, 0);            
        }
        return okSaveSettings;
    }

    public Command getBackFromSettings() {
        if (backFromSettings == null) {
            backFromSettings = new Command("Cancel", Command.BACK, 0);            
        }
        return backFromSettings;
    }

    public Form getChangeSettingsMenu() {
        if (changeSettingsMenu == null) {
            changeSettingsMenu = new Form("Change Settings", new Item[] { getIntervalTextField(), getUsernameTextField(), getPasswordTextField(), getCallFromTextField() });//GEN-BEGIN:|233-getter|1|233-postInit
            changeSettingsMenu.addCommand(getOkSaveSettings());
            changeSettingsMenu.addCommand(getBackFromSettings());
            changeSettingsMenu.setCommandListener(this);
        }
        return changeSettingsMenu;
    }

    public TextField getUsernameTextField() {
        if (usernameTextField == null) {
            String userName = userSettings.getUsername();
            usernameTextField = new TextField("Username:", userName, 32, TextField.ANY);            
        }
        return usernameTextField;
    }

    public TextField getPasswordTextField() {
        if (passwordTextField == null) {
            passwordTextField = new TextField("Password:", null, 32, TextField.PASSWORD);    
        }
        return passwordTextField;
    }

    public TextField getIntervalTextField() {
        if (intervalTextField == null) {
            String interval = userSettings.getCheckInterval();
            intervalTextField = new TextField("Check Inbox (secs)", interval, 32, TextField.NUMERIC);//GEN-LINE:|240-getter|1|240-postInit 
        }
        return intervalTextField;
    }

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

    public static class checkInbox extends TimerTask{
        public final void run() {
            Vector newMsgs = null;
            try {
                newMsgs = parseMsgs.readMsgs();
            } catch (ConnectionNotFoundException cnf) {
                System.out.println("Connection Not Found.");
                createTimer();
                return;
            }catch (IOException ex) {
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
                    getInbox().updateInbox(newMsgs);
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


