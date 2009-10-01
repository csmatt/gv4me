/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.gvME;
import gvME.textConvo;
import gvME.textMsg;
import gvME.tools;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;

/**
 *
 * @author matt
 */
public class MsgList extends List implements CommandListener {

    private int itemLength = 20;
    private Vector msgList = new Vector();
    private Vector MsgListToItemMap = new Vector();
    private Command viewMsgCmd;
    private Command replyCmd;
    private Command backCmd;
    private Command fwdCmd;
    private Command callCmd;
    private Command msgPropsCmd;
    private Form readMsg;
    private Form msgProps;
    private textConvo convo;

    //public MsgList(String title, Vector msgs) throws IOException
    public MsgList(textConvo crnt)
    {
        super(crnt.getSender(), List.IMPLICIT);
        addCommand(getViewMsgCmd());
        addCommand(getReplyCmd());
        addCommand(getFwdCmd());
        addCommand(getBackCmd());
        addCommand(getCallCmd());
        setCommandListener(this);
        setFitPolicy(Choice.TEXT_WRAP_OFF);
        setSelectCommand(getViewMsgCmd());

        this.convo = crnt;
        addItemsToMsgList(this.convo.getMessages());
    }
    
    public void addItemsToMsgList(Vector msgs)
    {
        deleteAll();
        getMsgList(msgs);
        Enumeration msgListEnum = msgList.elements();
        
        String msgListItem;
        while(msgListEnum.hasMoreElements())
        {
            msgListItem = (String) msgListEnum.nextElement();
            append(msgListItem, null);
        }
    }

//    public Vector getMsgListToItemMap()
//    {
//        return MsgList.MsgListToItemMap;
//    }
//
//    public static void setMsgListToItemMap(Vector msgListToItemMap)
//    {
//        MsgList.MsgListToItemMap = msgListToItemMap;
//    }

    private Form getReadMsg(String title)
    {
        if(readMsg == null)
        {
            readMsg = new Form("From: "+title);
            readMsg.addCommand(replyCmd);
            readMsg.addCommand(fwdCmd);
            readMsg.addCommand(getMsgPropsCmd());
            readMsg.addCommand(backCmd);
            readMsg.addCommand(getCallCmd());
            readMsg.setCommandListener(this);
        }
        else
        {
            readMsg.deleteAll();
        }
        return readMsg;
    }

    private Form getMsgProps(textConvo msg)
    {
        msgProps = new Form("Properties");
        msgProps.addCommand(getBackCmd());
        msgProps.addCommand(getCallCmd());
        StringItem sender = new StringItem("Sender: ", msg.getSender());
        msgProps.append(sender);
        StringItem replyNum = new StringItem("Phone Number: ", msg.getReplyNum());
        msgProps.append(replyNum);
        StringItem date = new StringItem("Sent: ", msg.getMsg().getTimeReceived()+" "+msg.getDate());
        msgProps.append(date);
        msgProps.setCommandListener(this);
        return msgProps;
    }

    private void getMsgList(Vector msgs)
    {
        Enumeration addMsgEnum = msgs.elements();
        textMsg crntMsg;
        MsgListToItemMap.removeAllElements();
        msgList.removeAllElements();
        StringBuffer itemBuff = new StringBuffer();
        while(addMsgEnum.hasMoreElements())
        {
            crntMsg = (textMsg) addMsgEnum.nextElement();
            itemBuff = new StringBuffer(crntMsg.getMessage());

            if(itemBuff.length() > itemLength)
            {
                itemBuff.setLength(itemLength);
                itemBuff.append("...");
            }
            MsgListToItemMap.insertElementAt(crntMsg, 0);
            msgList.insertElementAt(new String(itemBuff), 0);
        }
        itemBuff = null;
    }

    public void commandAction(Command command, Displayable displayable) {
        int selIndex = this.getSelectedIndex();
        System.out.println(String.valueOf(selIndex));
        textMsg original = null;

        if(displayable == this && command == backCmd)
        {
            try {
                gvME.dispMan.switchDisplayable(null, gvME.getInbox());
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if(displayable == readMsg && command == backCmd)
        {
            gvME.dispMan.switchDisplayable(null, this);
        }
        else if(!MsgListToItemMap.isEmpty())
        {
            original = (textMsg) MsgListToItemMap.elementAt(selIndex);

            if(command == fwdCmd)
            {
                WriteMsg wm = new WriteMsg("Forward", convo);
                gvME.dispMan.switchDisplayable(null, wm);
            }
            else if(command == replyCmd)
            {
                WriteMsg wm = new WriteMsg("Reply", convo);
                gvME.dispMan.switchDisplayable(null, wm);
            }
            else if(command == callCmd)
            {
                MakeCall mc = new MakeCall(convo.getReplyNum());
                gvME.dispMan.switchDisplayable(null, mc);
                mc = null;
            }
            else if(displayable == this && command == viewMsgCmd)
            {
                gvME.dispMan.switchDisplayable(null, getReadMsg(convo.getSender()));
                readMsg.append(tools.decodeString(original.getMessage())); //decodes string from utf8
            }
            else if(displayable == readMsg && command == msgPropsCmd)
            {
                textConvo propsConvo = convo;
                propsConvo.setLastMsg(original);
                gvME.dispMan.switchDisplayable(null, getMsgProps(propsConvo));
            }
            else if(displayable == msgProps && command == backCmd)
            {
                gvME.dispMan.switchDisplayable(null, this);
            }
        }
    }

    private Command getViewMsgCmd() {
        if (viewMsgCmd == null) {
            viewMsgCmd = new Command("Read", Command.OK, 1);
        }
        return viewMsgCmd;
    }

    private Command getCallCmd()
    {
        if(callCmd == null)
        {
            callCmd = new Command("Call", Command.ITEM, 0);
        }
        return callCmd;
    }

    private Command getBackCmd() {
        if (backCmd == null) {
            backCmd = new Command("Back", Command.BACK, 2);
        }
        return backCmd;
    }

    private Command getMsgPropsCmd() {
        if (msgPropsCmd == null) {
            msgPropsCmd = new Command("Properties", Command.ITEM, 4);
        }
        return msgPropsCmd;
    }

    private Command getReplyCmd() {
        if (replyCmd == null) {
            replyCmd = new Command("Reply", Command.ITEM, 3);
        }
        return replyCmd;
    }

    private Command getFwdCmd() {
        if (fwdCmd == null) {
            fwdCmd = new Command("Forward", Command.ITEM, 6);
        }
        return fwdCmd;
    }
}
