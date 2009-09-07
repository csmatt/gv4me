/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import java.io.IOException;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Image;

/**
 *
 * @author matt
 */
public class Inbox extends List implements CommandListener {
    private static Vector InboxToItemMap = new Vector(10);
    private Command viewMsgCmd;
    private Command delItemCmd;
    private Command replyCmd;
    private Command refreshCmd;
    private Command backCmd;
    private MsgList msgList;

    public Inbox() throws IOException, Exception
    {
        super("Inbox", Choice.IMPLICIT);
        addCommand(getViewMsgCmd());
        addCommand(getDelItemCmd());
        addCommand(getRefreshCmd());
        addCommand(getReplyCmd());
        addCommand(getBackCmd());
        setCommandListener(this);
        setFitPolicy(Choice.TEXT_WRAP_OFF);
        setSelectCommand(getViewMsgCmd());
        gvInbox.getInbox(this);
    }

    public void updateInbox() throws IOException
    {
        Enumeration inboxListEnum = gvInbox.addItemsToInbox().elements();
        String inboxItem;
        deleteAll();
        while(inboxListEnum.hasMoreElements())
        {
            inboxItem = (String) inboxListEnum.nextElement();
            append(inboxItem, getIcon());
        }
    }

    public Image getIcon() throws IOException
    {
       return Image.createImage("/pics/unread.png");
    }

    public static Vector getInboxToItemMap()
    {
        return InboxToItemMap;
    }

    public static void setInboxToItemMap(Vector InboxToItemMap)
    {
        Inbox.InboxToItemMap = InboxToItemMap;
    }

    public void commandAction(Command command, Displayable displayable) {
        if(displayable == this)
        {
            int selIndex = this.getSelectedIndex();
            textConvo original = null;

            if(command == backCmd)
            {
                gvME.dispMan.showMenu();
            }
            else if(command == refreshCmd)
            {
                try {
                    gvInbox.getInbox(this);
                    //updateInbox();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if(!InboxToItemMap.isEmpty())
            {
               original  = (textConvo) InboxToItemMap.elementAt(selIndex);

                if(command == viewMsgCmd)
                {
                    textConvo crnt = (textConvo) InboxToItemMap.elementAt(selIndex);
                    try {
                        msgList = new MsgList(crnt.getSender(), crnt.getMessages());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    gvME.dispMan.switchDisplayable(null, msgList);
                }
                else if(command == delItemCmd)
                {
                    delMsg.delete(this);
                    InboxToItemMap.removeElement(original);
                    try {
                        updateInbox();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else if(command == replyCmd)
                {
                    WriteMsg wm = new WriteMsg("Reply", original);
                    gvME.dispMan.switchDisplayable(null, wm);
                }
            }
        }
    }

    public Command getViewMsgCmd() {
        if (viewMsgCmd == null) {
            viewMsgCmd = new Command("Read", Command.OK, 1);
        }
        return viewMsgCmd;
    }

    public Command getBackCmd() {
        if (backCmd == null) {
            backCmd = new Command("Back", Command.BACK, 2);
        }
        return backCmd;
    }
    
    public Command getReplyCmd() {
        if (replyCmd == null) {
            replyCmd = new Command("Reply", Command.ITEM, 3);
        }
        return replyCmd;
    }

    public Command getRefreshCmd() {
        if (refreshCmd == null) {
            refreshCmd = new Command("Refresh", Command.ITEM, 4);
        }
        return refreshCmd;
    }
    
    public Command getDelItemCmd() {
        if (delItemCmd == null) {
            delItemCmd = new Command("Delete", Command.ITEM, 5);
        }
        return delItemCmd;
    }
}
