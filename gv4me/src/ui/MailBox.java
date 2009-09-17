/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.gvME;
import gvME.textConvo;
import gvME.textMsg;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author matt
 */
public class MailBox extends List implements CommandListener {
    public Command okCmd;
    public Command delItemCmd;
    public Command backCmd;
    public Command delAllCmd;
    public Vector list;
    public String rsName;
    public Vector rsMap = new Vector(10);
    public static int itemLength = 20;
    private Form readMsg;

    public MailBox(String title, String rsName) throws RecordStoreException, IOException
    {
        super(title, Choice.IMPLICIT);
        addCommand(getOkCmd());
        addCommand(getDelItemCmd());
        addCommand(getBackCmd());
        addCommand(getDelAllCmd());
        setSelectCommand(okCmd);
        setCommandListener(this);
        setFitPolicy(Choice.TEXT_WRAP_OFF);
        this.rsName = rsName;
        initialize();
    }

    private void initialize() throws RecordStoreException, IOException
    {
        list = vectFromRS(); //initializes list with the vector created from the recordstore

        //adds stored messages to the MailBox's list and the GUI list
        for(int i = list.size()-1; i >= 0; i--)
        {
            addItemToMailBox((textConvo) list.elementAt(i), -1);
        }
    }

    public void addItemToMailBox(textConvo crnt, int index) throws IOException, RecordStoreException
    {
        StringBuffer itemBuff = new StringBuffer();
        itemBuff = new StringBuffer(crnt.getSender());
        itemBuff.append(": ");
        itemBuff.append(((textMsg)crnt.getMessages().lastElement()).getMessage());
        if(itemBuff.length() > itemLength)
        {
            itemBuff.setLength(itemLength);
            itemBuff.append("...");
        }
        if(index >= 0)
            this.set(index, new String(itemBuff), null);
        else
            this.insert(0, new String(itemBuff), null); //TODO: change from null to crnt.isRead();
    }
    public void addItem(textConvo crnt) throws IOException, RecordStoreException
    {
        addItem(crnt, -1);
    }
    public void addItem(textConvo crnt, int index) throws IOException, RecordStoreException
    {
        updateRS(crnt, index);//updates the recordstore by adding the new convo
        addItemToMailBox(crnt, index);//adds item to MailBox GUI List
        if(index >= 0)
            list.setElementAt(crnt, index);
        else
            list.insertElementAt(crnt, 0);//adds item to list vector
    }

    public void delAll() throws RecordStoreException
    {
        this.deleteAll();
        list.removeAllElements();
        RecordStore.deleteRecordStore(rsName);
    }

    public void delItem(int selIndex) throws RecordStoreException
    {
        if(!list.isEmpty())
        {
            this.delete(selIndex);
            list.removeElementAt(selIndex);
            removeRecord(selIndex);
        }
    }

    //deletes a record from the recordstore and the recordID from the rsMap
    public void removeRecord(int index) throws RecordStoreException
    {
        int delIndex = ((Integer)(rsMap.elementAt(index))).intValue();
        RecordStore rs = null;
        try{
            rs = RecordStore.openRecordStore(rsName, true);
            rs.deleteRecord(delIndex);
        }
        finally{
            try{
                rs.closeRecordStore();
                rsMap.removeElementAt(index);
            }
            catch(Exception e)
            {}
        }

    }

    //adds a textConvo to the recordstore
    public void updateRS(textConvo crnt, int setAt) throws IOException, RecordStoreException
    {
        byte[] record = crnt.serialize();
        RecordStore rs = null;
        try{
            rs = RecordStore.openRecordStore(rsName, true);
            if(setAt >= 0)
            {
                setAt = ((Integer)rsMap.elementAt(setAt)).intValue();
                rs.setRecord(setAt, record, 0, record.length);
            }
            else
            {
                int index = rs.addRecord(record, 0, record.length);
                rsMap.addElement(new Integer(index));//rsMap maps recordIDs to items in the list vector
            }
        }
        finally{
            rs.closeRecordStore();
        }
    }

    //creates a vector from the recordstore's contents. it then returns this vector
    public Vector vectFromRS() throws InvalidRecordIDException, IOException, RecordStoreException
    {
        Vector vectOfRS = new Vector(10);
        RecordStore rs = null;
        RecordEnumeration re = null;
        try{
            rs = RecordStore.openRecordStore(rsName, true);
            re = rs.enumerateRecords(null, null, false);

            while(re.hasNextElement())
            {
                int recID = re.nextRecordId();
                rsMap.addElement(new Integer(recID));
                textConvo crnt = textConvo.deserialize(rs.getRecord(recID));
                vectOfRS.addElement(crnt);
            }
        }
        finally{
            rs.closeRecordStore();
            re.destroy();
            return vectOfRS;
        }
    }

    private Form getReadMsg(String title, String msg)
    {
        readMsg = new Form("To: "+title);
        readMsg.addCommand(backCmd);
        readMsg.setCommandListener(this);
        readMsg.append(msg);
        return readMsg;
    }

    public Command getDelAllCmd()
    {
        if (delAllCmd == null)
        {
            delAllCmd = new Command("Delete All", Command.ITEM, 0);
        }
        return delAllCmd;
    }
    public Command getOkCmd() {
        if (okCmd == null) {
            okCmd = new Command("Read", Command.OK, 1);
        }
        return okCmd;
    }

    public Command getBackCmd() {
        if (backCmd == null) {
            backCmd = new Command("Back", Command.BACK, 2);
        }
        return backCmd;
    }

    public Command getDelItemCmd() {
        if (delItemCmd == null) {
            delItemCmd = new Command("Delete", Command.ITEM, 0);
        }
        return delItemCmd;
    }

    public void commandAction(Command command, Displayable displayable) {

        if(command == backCmd)
        {
            if(displayable == this)
                gvME.dispMan.showMenu();
            else
                gvME.dispMan.switchToPreviousDisplayable();
        }
        else if(!list.isEmpty())
        {
            int selIndex = this.getSelectedIndex();
            if(command == delItemCmd)
            {
                try {
                    delItem(selIndex);
                } catch (RecordStoreException ex) {
                    ex.printStackTrace();
                }
            }
            else if(command == delAllCmd)
            {
                try{
                    delAll();
                }
                catch(RecordStoreException ex)
                {
                    ex.printStackTrace();
                }
            }
            else if(command == okCmd)
            {//if not overridden in a subclass, okCmd shows a form with the contents of the selected message
                textConvo crnt = (textConvo)list.elementAt(selIndex);
                String msg = ((textMsg)crnt.getMessages().firstElement()).getMessage();
                gvME.dispMan.switchDisplayable(null, getReadMsg(crnt.getSender(), msg));
            }
        }
    }
}