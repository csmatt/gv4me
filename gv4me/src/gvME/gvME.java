

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
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.netbeans.microedition.lcdui.WaitScreen;
import ui.*;

/**
 * @author matt
 */
public class gvME extends MIDlet implements CommandListener {
    private static final String sentBoxStore = "sentBoxStore";
    private static boolean midletPaused = false;
    private static Timer timer;
    private static long timerDelay = 30000;
    private static String rnr;
    private static Command exitCmd, minimize;
    private static Inbox InboxList;
    private static CommandListener cl;
    private static int numNewMsgs;
    private static Command backCmd, saveSettingsCmd;
    private static List menu;
    private WriteMsg newSMS;
    private static Form changeSettingsMenu;
    private static TextField passwordTextField, usernameTextField, callFromTextField, intervalTextField;
    private WaitScreen callWaitScreen;
    private Alert noCallFromAlert;
    public int countCons;
    public static MailBox SentBox;
    public static Outbox outbox;
    public static settings userSettings;
    public static DisplayManager dispMan;
    
    /*
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() throws InvalidRecordIDException, IOException, RecordStoreNotOpenException, RecordStoreException {//GEN-END:|0-initialize|0|0-preInitialize
        userSettings = new settings();
        countCons = 0;
        dispMan = new DisplayManager(this);
        cl = this; //reference to 'this' for CommandListener of static method getMenu()
        parseMsgs.setReqProps();
        try {
            RMSCookieConnector.removeCookies();
        } catch (Exception ignore)
        {}
        menu = getMenu();
        InboxList = new Inbox();
    }

    public void startMIDlet() throws IOException, Exception {
        gvLogin waitForLogin = new gvLogin(this);
        waitForLogin.checkLoginInfo();
        waitForLogin = null;
        SentBox = getSentBox();
        outbox = getOutbox();
        if(Integer.parseInt(userSettings.getCheckInterval()) > 0)
            createTimer();
    }

    public void resumeMIDlet() {
        dispMan.getDisplay();
    }

//<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicate that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {
        if (displayable == changeSettingsMenu) {
            if (command == backCmd) {
                dispMan.switchDisplayable(null, getMenu());
            } else if (command == saveSettingsCmd) {
                try {
                    changeSettings();
                } catch (RecordStoreException ex) {
                    Logger.add(getClass().getName(), ex.getMessage());
                    ex.printStackTrace();
                }
                dispMan.switchDisplayable(null, getMenu());
            }
        } else if (displayable == menu) {
            if (command == List.SELECT_COMMAND) {
                try {
                    menuAction();
                } catch (IOException ex) {
                    Logger.add(getClass().getName(), ex.getMessage());
                    ex.printStackTrace();
                } catch (Exception ex) {
                    Logger.add(getClass().getName(), ex.getMessage());
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

    public static void createTimer()
    {
        timer = new Timer();
        timer.schedule(new checkInbox(), timerDelay, Long.parseLong(userSettings.getCheckInterval())*1000);
    }

    public static void cancelTimer()
    {
        timer.cancel();
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
            if(Integer.parseInt(tfInterval) > 0)
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
        if(!tfCallFrom.equals(callFrom))
        {
            userSettings.setCallFrom(tfCallFrom);
        }
        userSettings.updateSettings();
    }

    /**
     * Returns an initiliazed instance of menu component.
     * @return the initialized component instance
     */
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

    public static void setMenu(int itemNum, String item)
    {
        menu.set(itemNum, item, null);
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: menuAction ">//GEN-BEGIN:|24-action|0|24-preAction
    /**
     * Performs an action assigned to the selected list element in the menu component.
     */
    public void menuAction() throws IOException, Exception {
        String __selectedString = getMenu().getString(getMenu().getSelectedIndex());
        if (__selectedString != null) {
            if (__selectedString.equals("Write New")) {
                if(newSMS != null)
                    newSMS.setString("");
                    newSMS = new WriteMsg("Write New", null);
                    dispMan.switchDisplayable(null, newSMS);
            } else if (__selectedString.startsWith("Inbox")) {
                dispMan.switchDisplayable(null, getInbox());
            } else if (__selectedString.equals("Outbox")) {
                dispMan.switchDisplayable(null, getOutbox());
            } else if (__selectedString.equals("Sent Box")) {
                dispMan.switchDisplayable(null, getSentBox());
            } else if (__selectedString.equals("Settings")) {            
                dispMan.switchDisplayable(null, getChangeSettingsMenu());                
            } else if (__selectedString.equals("Make Call")) {
                if(userSettings.getCallFrom().equals(""))
                    gvME.dispMan.switchDisplayable(getNoCallFromAlert(), gvME.getChangeSettingsMenu());
                else{
                    MakeCall mc = new MakeCall();
                    ChooseContact cc = new ChooseContact(getMenu(), mc);
                    mc = null;
                    dispMan.switchDisplayable(null, cc);
                }
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

    public Outbox getOutbox() throws RecordStoreException, IOException
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

    private static Command getBackCmd() {
        if (backCmd == null) {
            backCmd = new Command("Back", Command.BACK, 0);
            
        }
        return backCmd;
    }

    public static Command getMinimize() {
        if (minimize == null) {
            minimize = new Command("Minimize", Command.BACK, 0);        
        }
        return minimize;
    }

    private static Command getSaveSettingsCmd() {
        if (saveSettingsCmd == null) {
            saveSettingsCmd = new Command("Save", Command.OK, 1);           
        }
        return saveSettingsCmd;
    }

    public static Form getChangeSettingsMenu() {
        if (changeSettingsMenu == null) {
            changeSettingsMenu = new Form("Change Settings", new Item[] { getUsernameTextField(), getPasswordTextField(), getCallFromTextField(), getIntervalTextField() });//GEN-BEGIN:|233-getter|1|233-postInit
            changeSettingsMenu.addCommand(getSaveSettingsCmd());
            changeSettingsMenu.addCommand(getBackCmd());
            changeSettingsMenu.setCommandListener(cl);
        }
        return changeSettingsMenu;
    }

    private static TextField getUsernameTextField() {
        if (usernameTextField == null) {
            String userName = userSettings.getUsername();
            usernameTextField = new TextField("Username:", userName, 40, TextField.ANY);
        }
        return usernameTextField;
    }

    private static TextField getPasswordTextField() {
        if (passwordTextField == null) {
            passwordTextField = new TextField("Password:", null, 40, TextField.PASSWORD);
        }
        return passwordTextField;
    }

    private static TextField getIntervalTextField() {
        if (intervalTextField == null) {
            String interval = userSettings.getCheckInterval();
            intervalTextField = new TextField("Check Inbox (secs). 0 for never", interval, 10, TextField.NUMERIC);//GEN-LINE:|240-getter|1|240-postInit
        }
        return intervalTextField;
    }

    private static TextField getCallFromTextField() {
        if (callFromTextField == null) {
            String callFrom = userSettings.getCallFrom();
            callFromTextField = new TextField("Call From:", callFrom, 15, TextField.PHONENUMBER);//GEN-LINE:|246-getter|1|246-postInit
        }
        return callFromTextField;
    }

    private Alert getNoCallFromAlert()
    {
        if(noCallFromAlert == null)
        {
            noCallFromAlert = new Alert("Number Not Found", "Enter Your Number", null, AlertType.WARNING);
            noCallFromAlert.setTimeout(2000);
        }
        return noCallFromAlert;
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
                Logger.add(getClass().getName(), ex.getMessage());
                ex.printStackTrace();
            } catch (Exception ex) {
                Logger.add(getClass().getName(), ex.getMessage());
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
                Logger.add(getClass().getName(), cnf.toString());
                System.out.println("Connection Not Found.");
                createTimer();
                return;
            }catch (IOException ex) {
                Logger.add(getClass().getName(), "checkInbox", ex.toString());
                ex.printStackTrace();
            } catch (Exception ex) {
                Logger.add(getClass().getName(), "checkInbox", ex.toString());
                ex.printStackTrace();
            }
            if(newMsgs != null && numNewMsgs > 0)
            {
                Alert newMsgAlert = new Alert("New Messages");
                newMsgAlert.setString(numNewMsgs+" new messages");
                try {
                    numNewMsgs = 0;
                    getInbox().updateInbox(newMsgs);
                    if(dispMan.getDisplay().getCurrent() == null || midletPaused)
                    {
                        dispMan.switchDisplayable(newMsgAlert, getInbox());
                    }
                    dispMan.vibrate(400);
                } catch (IOException ex) {
                    Logger.add(getClass().getName(), "checkInbox", ex.getMessage());
                    ex.printStackTrace();
                } catch (Exception ex) {
                    Logger.add(getClass().getName(), "checkInbox", ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }
}


