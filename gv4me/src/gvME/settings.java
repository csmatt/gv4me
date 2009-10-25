/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author Matt Defenthaler
 */
public class settings {
    private static final String userSettingsStore = "userSettingsStore";
    private static Form changeSettingsMenu;
    private static TextField passwordTextField, usernameTextField, callFromTextField, intervalTextField;
    private static Command saveSettingsCmd, backCmd;
    private static String username = "";
    private static String password = "";
    private static String interval = "60";
    private static String callFrom = "";
    private static CommandListener cl;
    private static final int numFields = 4;
    private static final int MAX_CONTACTS = 10;
    private static Vector recentContacts;

    public static void initialize() throws IOException
    {
        settings.cl = cl;
        RecordStore rs = null;
        //getRecentContacts() = new Vector();
        try {
            rs = RecordStore.openRecordStore(userSettingsStore, true);
            if (rs.getNumRecords() != 0) {
                String[] settingsStr = serial.deserialize(numFields, rs.getRecord(1));
                setSettings(settingsStr);
                if (rs.getNumRecords() == 2) {
                    byte[] data = rs.getRecord(2);
                    recentContacts = serial.deserializeKVPVector(MAX_CONTACTS, data);
                }
            }
        } catch (RecordStoreException ex) {
            Logger.add("settings", "initialize", ex.getMessage());
            ex.printStackTrace();
        }
        finally{
            try{
                rs.closeRecordStore();
            }
            catch(Exception e)
            {}
        }
    }

    private static void changeSettings() throws RecordStoreException
    {
        String tfInterval = intervalTextField.getString();
        String tfUsername = usernameTextField.getString();
        String tfPassword = passwordTextField.getString();
        String tfCallFrom = callFromTextField.getString();

        if(!tfInterval.equals(interval))
        {
            interval = tfInterval;
            gvME.cancelTimer();
            if(Integer.parseInt(tfInterval) > 0)
                gvME.createTimer();
        }
        if(!tfUsername.equals(username))
        {
            username = tfUsername;
        }
        if(!tfPassword.equals(""))
        {
            password = tfPassword;
        }
        if(!tfCallFrom.equals(callFrom))
        {
            settings.callFrom = tfCallFrom;
        }
        updateSettings();
    }

    private static Command getSaveSettingsCmd() {
        if (saveSettingsCmd == null) {
            saveSettingsCmd = new Command("Save", Command.OK, 1);
        }
        return saveSettingsCmd;
    }

    private static Command getBackCmd()
    {
        if(backCmd == null)
        {
            backCmd = new Command("Back", Command.BACK, 0);
        }
        return backCmd;
    }

    public static Form getChangeSettingsMenu() {
        if (changeSettingsMenu == null) {
            changeSettingsMenu = new Form("Change Settings", new Item[] { getUsernameTextField(), getPasswordTextField(), getCallFromTextField(), getIntervalTextField() });//GEN-BEGIN:|233-getter|1|233-postInit
            changeSettingsMenu.addCommand(getSaveSettingsCmd());
            changeSettingsMenu.addCommand(getBackCmd());
            changeSettingsMenu.setCommandListener(new CommandListener() {

                public void commandAction(Command command, Displayable displayable) {
                    if(command == saveSettingsCmd)
                    {
                        try {
                            changeSettings();
                            updateSettings();
                            gvME.dispMan.showMenu();
                        } catch (RecordStoreException ex) {
                            ex.printStackTrace();
                        }
                    }
                    else if(command == backCmd)
                    {
                        gvME.dispMan.showMenu();
                    }
                }
            });
        }
        return changeSettingsMenu;
    }

    private static TextField getUsernameTextField() {
        if (usernameTextField == null) {
            usernameTextField = new TextField("Username:", username, 40, TextField.ANY);
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
            intervalTextField = new TextField("Check Inbox (secs). 0 for never", interval, 10, TextField.NUMERIC);
        }
        return intervalTextField;
    }

    private static TextField getCallFromTextField() {
        if (callFromTextField == null) {
            callFromTextField = new TextField("Call From:", callFrom, 15, TextField.PHONENUMBER);
        }
        return callFromTextField;
    }

    public static void setSettings(String[] fields)
    {
        if(fields != null)
        {
            settings.username = fields[0];
            settings.password = fields[1];
            settings.interval = fields[2];
            settings.callFrom = fields[3];
        }
    }

    public static int getNumFields()
    {
        return numFields;
    }

    public static String getCheckInterval()
    {
        return settings.interval;
    }

    public static String getUsername()
    {
        return settings.username;
    }

    public static String getPassword()
    {
        return settings.password;
    }

    public static String getCallFrom()
    {
        return callFrom;
    }

    public static void setCheckInterval(int interval)
    {
        settings.interval = String.valueOf(interval);
    }

    public static void setUsername(String username)
    {
        settings.username = username;
    }

    public static void setPassword(String password)
    {
        settings.password = password;
    }
//
//    public void setCallFrom(String callFrom)
//    {
//        this.callFrom = callFrom;
//    }

    public static Vector getRecentContacts()
    {
        if(recentContacts == null)
        {
            recentContacts = new Vector();
        }
        return recentContacts;
    }

    public static void updateContactOrder(int index) throws RecordStoreException, IOException
    {
        KeyValuePair crnt = (KeyValuePair)getRecentContacts().elementAt(index);
        getRecentContacts().insertElementAt(crnt, 0);
        getRecentContacts().removeElementAt(index+1);
        updateContacts();
    }

    public static void addContact(KeyValuePair contact) throws RecordStoreException, IOException
    {
        if(getRecentContacts().indexOf(contact) < 0)
        {
            getRecentContacts().insertElementAt(contact, 0);
            if(getRecentContacts().size() > MAX_CONTACTS)
                getRecentContacts().setSize(MAX_CONTACTS);
            updateContacts();
        }
    }

    public static void updateContacts() throws RecordStoreException, IOException
    {
        RecordStore rs = null;
        try{
            byte[] data = serial.serializeKVPVector(getRecentContacts());
            rs = RecordStore.openRecordStore(userSettingsStore, true);
            if(rs.getNumRecords() != 0){
                try{
                    rs.setRecord(2, data, 0, data.length);
                }
                catch(InvalidRecordIDException ire)
                {
                    rs.addRecord(data, 0, data.length);
                }
            }
        }
        catch(RecordStoreException rse)
        {
            Logger.add("settings", "updateSettings", rse.getMessage());
            rse.printStackTrace();
        }
        finally{
            try{
                rs.closeRecordStore();
            }
            catch(Exception e)
            {}
        }
    }

    public static void updateSettings() throws RecordStoreException
    {
        RecordStore rs = null;
        try{
            String[] fields = {username, password, interval, callFrom};
            byte[] data = null;
            data = serial.serialize(fields);
            rs = RecordStore.openRecordStore(userSettingsStore, true);
            if(rs.getNumRecords() != 0)
            {
                rs.setRecord(1, data, 0, data.length);
            }
            else
            {
                rs.addRecord(data, 0, data.length);
            }
        }
        catch(Exception ex)
        {
            Logger.add("settings", "updateSettings", ex.getMessage());
            ex.printStackTrace();
        }
        finally{
            rs.closeRecordStore();
        }
    }
}
