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
import javax.microedition.lcdui.Image;
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
    public Command OKCmd;
    public Command delItemCmd;
    public Command backCmd;
    public Command delAllCmd;
    public Command markMultiCmd;
    public Vector list;
    public String rsName;
    public static int itemLength = 17;
    private Form readMsg;
    public int numUnread = 0;
    public final Image msgIsRead = Image.createImage("/pics/read.png");
    public final Image msgIsUnread = Image.createImage("/pics/unread.png");

    public MailBox(String title, String rsName) throws RecordStoreException, IOException
    {
        super(title, Choice.IMPLICIT);
        addCommand(getOKCmd());
        addCommand(getDelItemCmd());
        addCommand(getBackCmd());
        addCommand(getDelAllCmd());
        setSelectCommand(OKCmd);
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
        Image icon;
        StringBuffer itemBuff = new StringBuffer();
        itemBuff = new StringBuffer(crnt.getSender());
        itemBuff.append(": ");
        itemBuff.append(crnt.getLastMsg().getMessage());//getMessages().lastElement()).getMessage());
        if(itemBuff.length() > itemLength)
        {
            itemBuff.setLength(itemLength);
            itemBuff.append("...");
        }
        if(crnt.getIsRead())
            icon = msgIsRead;
        else
            icon = msgIsUnread;
        if(index >= 0)
            this.set(index, new String(itemBuff), icon);
        else
            this.insert(0, new String(itemBuff), icon);
    }
    public void addItem(textConvo crnt) throws IOException, RecordStoreException
    {
        addItem(crnt, -1);
    }
    public void addItem(textConvo crnt, int index) throws IOException, RecordStoreException
    {
        addItemToMailBox(crnt, index);//adds item to MailBox GUI List
        if(index >= 0)
            list.setElementAt(crnt, index);
        else
            list.insertElementAt(crnt, 0);//adds item to list vector
        updateRS();
    }

    public void delAll() throws RecordStoreException
    {
        try{
        RecordStore.deleteRecordStore(rsName);
        }
        catch(Exception e)
        {}
        this.deleteAll();
        list.removeAllElements();
    }

    public void delItem(int selIndex) throws RecordStoreException
    {
        if(!list.isEmpty())
        {
            this.delete(selIndex);
            list.removeElementAt(selIndex);
            updateRS();
        }
    }

    public void updateRS()
    {
        try{
        RecordStore.deleteRecordStore(rsName);
        }
        catch(Exception e)
        {}
        RecordStore rs = null;
        try{
            rs = RecordStore.openRecordStore(rsName, true);
            
            for(int i = list.size()-1; i >= 0; i--)
            {
                textConvo crnt = (textConvo)list.elementAt(i);
                byte[] data = crnt.serialize();
                rs.addRecord(data, 0, data.length);
            }
        }
        catch(Exception e)
        {}
        finally{
            try {
                rs.closeRecordStore();
            } catch (Exception e)
            {}
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
                textConvo crnt = textConvo.deserialize(re.nextRecord());
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
    public Command getOKCmd() {
        if (OKCmd == null) {
            OKCmd = new Command("Read", Command.OK, 1);
        }
        return OKCmd;
    }

    public Command getBackCmd() {
        if (backCmd == null) {
            backCmd = new Command("Back", Command.BACK, 2);
        }
        return backCmd;
    }

    public Command getMultiMarkCmd()
    {
        if(markMultiCmd == null)
        {
            markMultiCmd = new Command("Mark Multiple", Command.OK, 0);
        }
        return markMultiCmd;
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
            else if(command == OKCmd)
            {//if not overridden in a subclass, okCmd shows a form with the contents of the selected message
                textConvo crnt = (textConvo)list.elementAt(selIndex);
                String msg = ((textMsg)crnt.getLastMsg()).getMessage();
                gvME.dispMan.switchDisplayable(null, getReadMsg(crnt.getSender(), msg));
            }
        }
    }
}