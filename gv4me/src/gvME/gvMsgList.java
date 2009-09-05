/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.util.Enumeration;
import java.util.Vector;
import ui.MsgList;

/**
 *
 * @author matt
 */
public class gvMsgList {

    private static Vector MsgListToItemMap = new Vector(10);
    private static int itemLength = 20;
    private static Vector msgList = new Vector(10);

    public static Vector getMsgList(Vector msgs)
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
            MsgListToItemMap.addElement(crntMsg);
            msgList.addElement(new String(itemBuff));
            //msgList.insert(MsgListToItemMap.size()-1, itemBuff.toString(), null);
        }
        MsgList.setMsgListToItemMap(MsgListToItemMap);
        return msgList;
    }

}
