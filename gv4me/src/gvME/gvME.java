

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
import ui.*;

/**
 * @author Matt Defenthaler
 */
public class gvME extends MIDlet implements CommandListener {
    private static final String sentBoxStore = "sentBoxStore";
    private static boolean midletPaused = false;
    private static Timer timer;
    private static long timerDelay = 30000;
    private static String rnr, auth;
    private static Command exitCmd, minimize, backCmd;
    private static Inbox InboxList;
    private static CommandListener cl;
    private static int numNewMsgs;
    private static List menu;
    private static Alert noCallFromAlert;
    public static MailBox SentBox;
    public static Outbox outbox;
    public static DisplayManager dispMan;

    /*
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() throws InvalidRecordIDException, IOException, RecordStoreNotOpenException, RecordStoreException {//GEN-END:|0-initialize|0|0-preInitialize
        settings.initialize();
        dispMan = new DisplayManager(this);
        cl = this; //reference to 'this' for CommandListener of static method getMenu()
        connMgr.initReqProps();
        try {
            RMSCookieConnector.removeCookies();
        } catch (Exception ignore)
        {}
        menu = getMenu();
        InboxList = new Inbox();
    }

    public void startMIDlet() throws IOException, Exception {
        Login login = new Login();
        login = null;
        SentBox = getSentBox();
        outbox = getOutbox();
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

        if (displayable == menu) {
            if (command == List.SELECT_COMMAND) {
                try {
                    menuAction();
                }
                catch (Exception ex) {
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

    /**
     * Sets the auth token in this class to make it available to other classes from one place.
     * The auth token is part of Google's ClientLogin API
     * @param auth
     */
    public static void setAuth(String auth)
    {
        gvME.auth = auth;
    }

    /**
     * Sets the rnr token in this class to make it available to other classes from one place.
     * The rnr token is part of Google Voice's API and identifies a particular user during a session
     * @param rnr
     */
    public static void setRNR(String rnr)
    {
        gvME.rnr = rnr;
    }

    /**
     * Returns the auth token when requested by other classes
     * @return returns the auth token
     */
    public static String getAuth()
    {
        return gvME.auth;
    }

    /**
     * Returns the rnr token when requested by other classes
     * @return returns the rnr token
     */
    public static String getRNR()
    {
        return gvME.rnr;
    }

    /**
     * Creates a timer that will check for new messages at the interval specified by the user in settings.
     * If the interval is set to 0, the timer will not be created and checking for new messages will not occur automatically.
     */
    public static void createTimer()
    {
        if(Integer.parseInt(settings.getCheckInterval()) > 0)
        {
            timer = new Timer();
            timer.schedule(new checkInbox(), timerDelay, Long.parseLong(settings.getCheckInterval())*1000);
        }
    }

    /**
     * Cancels the timer. Checking for new messages is suspended until createTimer() is invoked.
     */
    public static void cancelTimer()
    {
        try{//in case a timer doesn't exist
            timer.cancel();
        }
        catch(Exception ignore){}
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

    /**
     * Changes the text of a main menu item
     * @param itemNum The index of the item to change
     * @param item The new text of the item being changed
     */
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
                WriteMsg newSMS = new WriteMsg("Write New", null);
                dispMan.switchDisplayable(null, newSMS);
            } else if (__selectedString.startsWith("Inbox")) {
                dispMan.switchDisplayable(null, getInbox());
            } else if (__selectedString.equals("Outbox")) {
                dispMan.switchDisplayable(null, getOutbox());
            } else if (__selectedString.equals("Sent Box")) {
                dispMan.switchDisplayable(null, getSentBox());
            } else if (__selectedString.equals("Settings")) {            
                dispMan.switchDisplayable(null, settings.getChangeSettingsMenu());
            } else if (__selectedString.equals("Make Call")) {
                if(settings.getCallFrom().equals(""))
                    gvME.dispMan.switchDisplayable(getNoCallFromAlert(), settings.getChangeSettingsMenu());
                else{
                    MakeCall mc = new MakeCall();
                    ChooseContact cc = new ChooseContact(getMenu(), mc);
                    mc = null;
                    dispMan.switchDisplayable(null, cc);
                }
            }
        }        
    }

    /**
     * Gets the SentBox (MailBox containing a list of messages sent from this phone)
     * @return Returns SentBox
     * @throws RecordStoreException
     * @throws IOException
     */
    public MailBox getSentBox() throws RecordStoreException, IOException
    {
        if(SentBox == null)
        {
            SentBox = new MailBox("Sent Box", sentBoxStore);
        }
        return SentBox;
    }

    /**
     * Gets the Inbox (MailBox containing a list of messages received on this phone)
     * @return Returns Inbox
     * @throws IOException
     * @throws Exception
     */
    public static Inbox getInbox() throws IOException, Exception
    {
        if(InboxList == null)
        {
            InboxList = new Inbox();
        }
        return InboxList;
    }

    /**
     * Gets the Outbox (MailBox containing a list of messages yet to be sent from this phone)
     * @return Returns Outbox
     * @throws RecordStoreException
     * @throws IOException
     */
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

    public static Alert getNoCallFromAlert()
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
        if(timer != null)
            timer.cancel();
      //  try {
      //      userSettings.updateContacts();
      //  } catch (RecordStoreException ex) {
      //      ex.printStackTrace();
     //   }
    }

    /**
     * Sets the number of new messages received.
     * @param newMsgCnt The number of new messages received.
     */
    public static void setNumNewMsgs(int newMsgCnt)
    {
        gvME.numNewMsgs = newMsgCnt;
    }

    /**
     * checkInbox is a class that extends TimerTask in order to execute a check for new messages at the timer's interval
     */
    static class checkInbox extends TimerTask{
        public final void run() {
            Vector newMsgs = null;
            try {
                newMsgs = parseMsgs.readMsgs();
            } catch (ConnectionNotFoundException cnf) {
                //Logger.add(getClass().getName(), cnf.toString());
//                System.out.println("Connection Not Found.");
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


