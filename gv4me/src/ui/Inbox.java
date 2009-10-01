/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.gvME;
import gvME.parseMsgs;
import gvME.textConvo;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author matt
 */
public class Inbox extends MailBox {
    private static final String inboxStore = "inboxStore";
    private static Hashtable inboxHash = new Hashtable();
    private Command replyCmd;
    private Command refreshCmd;
    private Command callCmd;
    private static MsgList msgList;
    private static WriteMsg wm;

    public Inbox() throws IOException, RecordStoreException
    {
        super("Inbox", inboxStore);
        addCommand(getReplyCmd());
        addCommand(getRefreshCmd());
        addCommand(getCallCmd());
        initInboxHash();
    }

    //creates a hashtable of textConvos. Key = textConvo.msgID; Value = textConvo
    //this is done to more quickly check to see if a textConvo with a given msgID already exists
    private void initInboxHash()
    {
        Enumeration listEnum = list.elements();
        while(listEnum.hasMoreElements())
        {
            textConvo crnt = (textConvo) listEnum.nextElement();
            inboxHash.put(crnt.getMsgID(), crnt);
        }
    }

    public void updateInbox(Vector newMsgs) throws IOException, RecordStoreException
    {
        if(newMsgs != null && newMsgs.size() > 0)
        {
            for(int i = newMsgs.size()-1; i >= 0; i--)
            {
                textConvo newConvo = (textConvo) newMsgs.elementAt(i);
                int index = -1;
                if(inboxHash.containsKey(newConvo.getMsgID()))
                {
                    textConvo crnt = (textConvo) inboxHash.get(newConvo.getMsgID());
                    index = list.indexOf(crnt);
                    inboxHash.put(crnt.getMsgID(), crnt);
                    list.setElementAt(crnt, index);
                    addItem(crnt, index);
                }
                else
                {
                    inboxHash.put(newConvo.getMsgID(), newConvo);
                    addItem(newConvo);
                }
            }
        }
    }

    private void refreshInbox()
    {
        Thread refreshThread = new Thread(){
            public void run(){
                try {
                    gvME.getInbox().updateInbox(parseMsgs.readMsgs());
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        refreshThread.start();
    }

    public static Hashtable getInboxHash()
    {
        return inboxHash;
    }

    private Command getReplyCmd() {
        if (replyCmd == null) {
            replyCmd = new Command("Reply", Command.ITEM, 3);
        }
        return replyCmd;
    }

    private Command getRefreshCmd() {
        if (refreshCmd == null) {
            refreshCmd = new Command("Refresh", Command.ITEM, 4);
        }
        return refreshCmd;
    }

    private Command getCallCmd(){
        if (callCmd == null)
        {
            callCmd = new Command("Call", Command.ITEM, 5);
        }
        return callCmd;
    }
    
    public void commandAction(Command command, Displayable displayable) {

        if(command == backCmd || command == delItemCmd || command == delAllCmd)
        {
            super.commandAction(command, displayable);
        }
        else if(command == refreshCmd)
        {
            refreshInbox();
        }
        else if(!list.isEmpty())
        {
            int selIndex = this.getSelectedIndex();
            textConvo original  = (textConvo) list.elementAt(selIndex);

            if(command == okCmd)
            {
                textConvo crnt = (textConvo) list.elementAt(selIndex);
                msgList = new MsgList(crnt);
                gvME.dispMan.switchDisplayable(null, msgList);
            }
            else if(command == replyCmd)
            {
                wm = new WriteMsg("Reply", original);
                gvME.dispMan.switchDisplayable(null, wm);
            }
            else if(command == callCmd)
            {
                MakeCall mc = new MakeCall(original.getReplyNum());
                gvME.dispMan.switchDisplayable(null, mc);
                mc = null;
            }
        }
    }
}
