/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
//import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import ui.Inbox;
//import ui.MsgList;

/**
 *
 * @author matt
 */
public class gvInbox {
    private static String lastMsgStore = "lastMsgStore";
    private static String storedMsgName = "storedMsgs";
    private static Hashtable lastMsgs = new Hashtable();
    private static int newMsgCnt = 0;
    private static int itemLength = 20; //number of characters to display of 'phone number - message' combo
    private static Vector inboxList = new Vector(10);
    private static Inbox inbox;
    private static Vector InboxToItemMap = new Vector(10);

    public static void getInbox(Inbox inbox) throws IOException, Exception
    {
        gvInbox.inbox = inbox;
        parser();
      //  addToInbox();
    }

    public static Vector addItemsToInbox()
    {
        //Vector InboxToItemMap = new Vector(20);

        StringBuffer itemBuff = new StringBuffer();

        try{

        inboxList.removeAllElements();
        InboxToItemMap.removeAllElements();
        Vector convos = parseMsgs.getStoredConvos();
        textConvo crntConvo;
        //adds stored/old messages to the Inbox list and records their place in InboxToItemsMap
        for(int i = convos.size()-1; i >= 0; i--)
        {
            crntConvo = (textConvo) convos.elementAt(i);//newMsgs.get((String) newMsgsEnum.nextElement());
            itemBuff = new StringBuffer(crntConvo.getSender());
            itemBuff.append(": ");
            itemBuff.append(((textMsg)crntConvo.getMessages().lastElement()).getMessage());
            if(itemBuff.length() > itemLength)
            {
                itemBuff.setLength(itemLength);
                itemBuff.append("...");
            }
            inboxList.addElement(new String(itemBuff));
            InboxToItemMap.addElement(crntConvo);
        }
        //Inbox.setInboxToItemMap(InboxToItemMap);
        }
        catch(Exception e)
        {
         System.out.println("error adding to inbox: "+e.toString());
         e.printStackTrace();
        }
        finally{
            Inbox.setInboxToItemMap(InboxToItemMap);
            return gvInbox.inboxList;
        }
    }

    public static void delMsg(Inbox Inbox)
    {
        InboxToItemMap = Inbox.getInboxToItemMap();
        int selIndex = Inbox.getSelectedIndex();
        textConvo crntConvo = (textConvo) InboxToItemMap.elementAt(selIndex);
        Inbox.delete(selIndex);

        parseMsgs.removeConvo(crntConvo);
    }

    public static void parser()
    {
        Thread thread = new Thread(){
        public void run(){
                try {
                    newMsgCnt = parseMsgs.readMsgs();
                    gvInbox.inbox.updateInbox();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
         }
        };
        thread.start();
    }
}