/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author matt
 */
public class settings {
 //   private final String contactRSName = "contactStore";
    private final String userSettingsStore = "userSettingsStore";
    private String username = "";
    private String password = "";
    private String interval = "60";
    private String callFrom = "";
    private final int numFields = 4;
    private final int MAX_CONTACTS = 10;
    private Vector recentContacts = new Vector();
    private RecordStore rs;

    public settings() throws IOException
    {
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
            ex.printStackTrace();
        }
        finally{
            try{
                rs.closeRecordStore();
            }
            catch(Exception e){}
        }
    }
    
    public void setSettings(String[] fields)
    {
        if(fields != null)
        {
            this.username = fields[0];
            this.password = fields[1];
            this.interval = fields[2];
            this.callFrom = fields[3];
        }
    }

    public int getNumFields()
    {
        return numFields;
    }

    public String getCheckInterval()
    {
        return this.interval;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getPassword()
    {
        return this.password;
    }

    public String getCallFrom()
    {
        return callFrom;
    }

    public void setCheckInterval(String interval)
    {
        this.interval = String.valueOf(interval);
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setCallFrom(String callFrom)
    {
        this.callFrom = callFrom;
    }

    public Vector getRecentContacts()
    {
        return this.recentContacts;
    }

    public void addContact(KeyValuePair contact) throws RecordStoreException, IOException
    {
        if(!recentContacts.contains(contact))
        {
            recentContacts.insertElementAt(contact, 0);
            if(recentContacts.size() > MAX_CONTACTS)
                recentContacts.setSize(MAX_CONTACTS);
            updateContacts();
        }
    }

    public void updateContacts() throws RecordStoreException, IOException
    {
        try{
        byte[] data = serial.serializeKVPVector(recentContacts);
        rs = RecordStore.openRecordStore(userSettingsStore, true);
        if(rs.getRecord(2) == null)
            rs.addRecord(data, 0, data.length);
        else
            rs.setRecord(2, data, 0, data.length);
        }
        finally{
            rs.closeRecordStore();
        }
    }

    public void updateSettings() throws RecordStoreException
    {
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
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            rs.closeRecordStore();
        }
    }
}
