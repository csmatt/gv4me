

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
public class gvME extends MIDlet implements CommandListener, interCom {
    private static final String sentBoxStore = "sentBoxStore";
    private static boolean midletPaused = false;
    private static Timer timer;
    private static long timerDelay = 3000;
    private static String rnr, auth, contacting, recipient;
    private static Command exitCmd, minimize, okCmd;//, backCmd;
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
        MakeCall.setMidlet(this);
        dispMan = new DisplayManager(this);
        cl = this; //reference to 'this' for CommandListener of static method getMenu()
        connMgr.initReqProps();
        try {
            RMSCookieConnector.removeCookies();
        } catch (Exception ignore)
        {}
        menu = getMenu();
       // InboxList = new Inbox();
    }

    public void startMIDlet() throws IOException, Exception {
        Login login = new Login();
        InboxList = getInbox();
//        SentBox = getSentBox();
//        outbox = getOutbox();
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
            checkInbox checker = new checkInbox();
            timer.schedule(checker, timerDelay, Long.parseLong(settings.getCheckInterval())*1000);
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

    /**
     * Performs an action assigned to the selected list element in the menu component.
     */
    public void menuAction() throws IOException, Exception {
        switch(getMenu().getSelectedIndex())
        {
            case 0:
                WriteMsg newSMS = new WriteMsg("Write New", null);
                dispMan.switchDisplayable(null, newSMS);
                break;
            case 1:
                dispMan.switchDisplayable(null, getInbox());
                break;
            case 2:
                try{
                ChooseContact cc = new ChooseContact(getMenu(), new MakeCall());
                dispMan.switchDisplayable(null, cc);
                }
                catch(Exception ex)
                {
                    if(ex.toString().indexOf("no call from") >= 0)
                    {
                        dispMan.switchDisplayable(getNoCallFromAlert(), settings.getChangeSettingsMenu());
                    }
                }
                break;
            case 3:
                 dispMan.switchDisplayable(null, getSentBox());
                 break;
            case 4:
                dispMan.switchDisplayable(null, getOutbox());
                break;
            case 5:
                dispMan.switchDisplayable(null, settings.getChangeSettingsMenu());
                break;
        }
    }

    /**
     * Gets the SentBox (MailBox containing a list of messages sent from this phone)
     * @return Returns SentBox
     * @throws RecordStoreException
     * @throws IOException
     */
    public static MailBox getSentBox() throws RecordStoreException, IOException
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

//    private static Command getBackCmd() {
//        if (backCmd == null) {
//            backCmd = new Command("Back", Command.BACK, 0);
//
//        }
//        return backCmd;
//    }

    public static Command getMinimize() {
        if (minimize == null) {
            minimize = new Command("Minimize", Command.BACK, 0);        
        }
        return minimize;
    }

    public static Command getOkCmd()
    {
        if (okCmd == null)
        {
            okCmd = new Command("Go", Command.ITEM, 1);
        }
        return okCmd;
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

    public MIDlet getMidlet()
    {
        return this;
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
        try {
            connMgr.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    public void getPlatformRequest(String request) throws ConnectionNotFoundException
    {
        platformRequest(request);
    }

    public void setContacting(String contacting, String recipient) {
        if(contacting.startsWith("1"))
            contacting = contacting.substring(1);
        gvME.contacting = contacting;
        gvME.recipient = recipient;
    }

    /**
     * checkInbox is a class that extends TimerTask in order to execute a check for new messages at the timer's interval
     */
    static class checkInbox extends TimerTask{
        public final void run() {            Vector newMsgs = null;
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


