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
    public static Vector list;
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
        setSelectCommand(okCmd);
        setCommandListener(this);
        setFitPolicy(Choice.TEXT_WRAP_OFF);
        this.rsName = rsName;
        initialize();
    }

    private void initialize() throws RecordStoreException, IOException
    {
        this.list = vectFromRS();

        //adds stored/old messages to the MailBox's list and the GUI list
        for(int i = list.size()-1; i >= 0; i--)
        {
            addItemToInbox((textConvo) list.elementAt(i));
        }
    }
    
    public void addItem(textConvo crnt) throws IOException, RecordStoreException
    {
        updateRS(crnt);
        addItemToInbox(crnt);
        list.insertElementAt(crnt, 0);
    }

    public void addItemToInbox(textConvo crnt) throws IOException, RecordStoreException
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
        this.insert(0, new String(itemBuff), null); //TODO: change from null to crnt.isRead();
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

    public void removeRecord(int index) throws RecordStoreException
    {
        int delIndex = ((Integer)(rsMap.elementAt(index))).intValue();
        RecordStore rs = RecordStore.openRecordStore(rsName, true);
        rs.deleteRecord(delIndex);
        rs.closeRecordStore();
        rsMap.removeElementAt(index);
    }
    
    public void updateRS(textConvo crnt) throws IOException, RecordStoreException
    {
        byte[] record = crnt.serialize();
        RecordStore rs = RecordStore.openRecordStore(rsName, true);
        int index = rs.addRecord(record, 0, record.length);
        rs.closeRecordStore();
        rsMap.addElement(new Integer(index));
    }

    public Command getOkCmd() {
        if (okCmd == null) {
            okCmd = new Command("Read", Command.OK, 1);
        }
        return okCmd;
    }

    public Vector vectFromRS() throws InvalidRecordIDException, IOException, RecordStoreException
    {
        Vector vectOfRS = new Vector(10);
        RecordStore rs = RecordStore.openRecordStore(rsName, true);
        RecordEnumeration re = rs.enumerateRecords(null, null, false);

        while(re.hasNextElement())
        {
            textConvo crnt = textConvo.deserialize(re.nextRecord());
            vectOfRS.addElement(crnt);
        }
        rs.closeRecordStore();
        re.destroy();
        return vectOfRS;
    }
    private Form getReadMsg(String title, String msg)
    {
        readMsg = new Form("To: "+title);
        readMsg.addCommand(backCmd);
        readMsg.setCommandListener(this);
        readMsg.append(msg);
        return readMsg;
    }

    public Command getBackCmd() {
        if (backCmd == null) {
            backCmd = new Command("Back", Command.BACK, 2);
        }
        return backCmd;
    }

    public Command getDelItemCmd() {
        if (delItemCmd == null) {
            delItemCmd = new Command("Delete", Command.ITEM, 3);
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
            else if(command == okCmd)
            {
                textConvo crnt = (textConvo)list.elementAt(selIndex);
                String msg = ((textMsg)crnt.getMessages().firstElement()).getMessage();
                gvME.dispMan.switchDisplayable(null, getReadMsg(crnt.getSender(), msg));
            }
        }

    }
}