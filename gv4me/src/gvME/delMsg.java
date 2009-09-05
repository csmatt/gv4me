/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.util.Vector;

import ui.Inbox;

/**
 *
 * @author matt
 */
public class delMsg {
    private static Vector InboxToItemMap;
    private static String storedMsgName = "storedMsgs";

    public static void delete(Inbox Inbox)
    {
        delMsg.InboxToItemMap = Inbox.getInboxToItemMap();
        int selIndex = Inbox.getSelectedIndex();
        textConvo crntConvo = (textConvo) InboxToItemMap.elementAt(selIndex);
        Inbox.delete(selIndex);

        parseMsgs.removeConvo(crntConvo);

    }
}
