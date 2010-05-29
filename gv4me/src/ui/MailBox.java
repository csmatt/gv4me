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
 * @author Matt Defenthaler
 */
public class MailBox extends List implements CommandListener {
    public Command readCmd, delItemCmd, delAllCmd, backCmd, markMultiCmd;
    public Vector list;
    public String rsName;
    public final int itemLength = 17;
    public int numUnread = 0;
    public final Image msgIsRead = Image.createImage("/pics/read.png");
    public final Image msgIsUnread = Image.createImage("/pics/unread.png");
    private Form readMsg;

    public MailBox(String title, String rsName) throws RecordStoreException, IOException
    {
        super(title, Choice.IMPLICIT);
        addCommand(getReadCmd());
        addCommand(getDelItemCmd());
        addCommand(getBackCmd());
        addCommand(getDelAllCmd());
        setSelectCommand(getReadCmd());
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

    /**
     * Creates a String representation of the textConvo being added to the GUI List
     * @param crnt textConvo being added
     * @param index Location in the GUI List at which to insert the crnt textConvo
     * @throws IOException
     * @throws RecordStoreException
     */
    public void addItemToMailBox(textConvo crnt, int index) throws IOException, RecordStoreException
    {
        Image icon;
        StringBuffer itemBuff = new StringBuffer();
        itemBuff = new StringBuffer(crnt.getSender());
        itemBuff.append(": ");
        itemBuff.append(crnt.getLastMsg().getMessage());
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
    
    /**
     * Intermediate method used to insert brand new textConvo's into the MailBox
     * @param crnt textConvo being added to the list vector
     * @throws IOException
     * @throws RecordStoreException
     */
    public void addItem(textConvo crnt) throws IOException, RecordStoreException
    {
        addItem(crnt, -1);
    }
    
    /**
     * Inserts a textConvo into the specified index of the list vector and GUI List
     * @param crnt textConvo being added
     * @param index Location at which to place crnt textConvo in the list vector and GUI List 
     * (-1 means it's a brand new textConvo and should be placed at the top of the list.)
     * @throws IOException
     * @throws RecordStoreException
     */
    public void addItem(textConvo crnt, int index) throws IOException, RecordStoreException
    {
        addItemToMailBox(crnt, index);//adds item to MailBox GUI List
        if(index >= 0)
            list.setElementAt(crnt, index);
        else
            list.insertElementAt(crnt, 0);//adds item to list vector
        updateRS();
    }

    /**
     * Deletes all textConvo's in the MailBox
     * @throws RecordStoreException
     */
    public void delAll() throws RecordStoreException
    {
        try{
        RecordStore.deleteRecordStore(rsName);
        }
        catch(Exception ignore){}
        this.deleteAll();
        list.removeAllElements();
    }

    /**
     * Deletes a textConvo from the list vector and GUI List at the specified index
     * @param selIndex Index of the item to delete.
     * @throws RecordStoreException
     */
    public void delItem(int selIndex) throws RecordStoreException
    {
        if(!list.isEmpty())
        {
            this.delete(selIndex);
            list.removeElementAt(selIndex);
            updateRS();
        }
    }

    /**
     * Updates the RecordStore of this MailBox
     */
    public void updateRS()
    {
        try{
        RecordStore.deleteRecordStore(rsName);
        }
        catch(Exception ignore){}
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
        catch(Exception ignore){}
        finally{
            try {
                rs.closeRecordStore();
            } catch (Exception ignore){}
        }
    }

    /**
     * Creates a vector from the RecordStore's contents.
     * @return Returns a vectore from the contents of the RecordStore.
     * @throws InvalidRecordIDException
     * @throws IOException
     * @throws RecordStoreException
     */
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
            try{
                rs.closeRecordStore();
                re.destroy();
            }
            catch(Exception ignore){}
            return vectOfRS;
        }
    }

    /**
     * 
     * @param title Sender or Recipient of the message
     * @param msg Text of the message being read
     * @return Returns a GUI Form with the contents of the message
     */
    private Form getReadMsg(String title, String msg)
    {
        readMsg = new Form("To: "+title);
        readMsg.addCommand(backCmd);
        readMsg.setCommandListener(this);
        readMsg.append(msg);
        return readMsg;
    }

    public Command getReadCmd() {
        if (readCmd == null) {
            readCmd = new Command("Read", Command.ITEM, 1);
        }
        return readCmd;
    }

    public Command getBackCmd() {
        if (backCmd == null) {
            backCmd = new Command("Back", Command.BACK, 1);
        }
        return backCmd;
    }

    public Command getMultiMarkCmd()
    {
        if(markMultiCmd == null)
        {
            markMultiCmd = new Command("Mark Multiple", Command.OK, 2);
        }
        return markMultiCmd;
    }

    public Command getDelItemCmd() {
        if (delItemCmd == null) {
            delItemCmd = new Command("Delete", Command.ITEM, 3);
        }
        return delItemCmd;
    }

    public Command getDelAllCmd()
    {
        if (delAllCmd == null)
        {
            delAllCmd = new Command("Delete All", Command.ITEM, 3);
        }
        return delAllCmd;
    }

    public void commandAction(Command command, Displayable displayable) {

        if(command == backCmd)
        {
            if(displayable == this)
            {
                gvME.dispMan.showMenu();
            }
            else
            {
                gvME.dispMan.switchToPreviousDisplayable();
            }
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
            else if(command == readCmd)
            {//if not overridden in a subclass, readCmd shows a form with the contents of the selected message
                textConvo crnt = (textConvo)list.elementAt(selIndex);
                String msg = ((textMsg)crnt.getLastMsg()).getMessage();
                gvME.dispMan.switchDisplayable(null, getReadMsg(crnt.getSender(), msg));
            }
        }
    }
}