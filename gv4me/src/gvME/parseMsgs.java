

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import ui.Inbox;

/**
 *
 * @author Matt Defenthaler
 * parseMsgs retrieves the HTML data containing unread messages and parses out new message data
 */
public class parseMsgs {
    private static final String markReadURL = "https://www.google.com/voice/m/mark?p=1&label=unread&id=";
    private static final String getMsgsURL = "https://www.google.com/voice/inbox/recent/unread";
    private static final String jsonBegin = "<json><![CDATA[{\"messages\":{\"";
    private static final String idToken = "{\"id\":\"";
    private static final String separateToken = "\",\"";
    private static final String phoneNumToken = "phoneNumber\":\"+1";
    private static final String jsonEnd = "</json>";
    private static final String unreadIsZero = "\"unread\":0";
    private static final String beginMsgToken = "<div id=\"";
    private static final String msgFromToken = "<span class=\"gc-message-sms-from\">";
    private static final String endSpan = "</span>";
    private static final String msgTextToken = "<span class=\"gc-message-sms-text\">";
    private static final String msgTimeToken = "<span class=\"gc-message-sms-time\">";
    private static final String endMsgToken = "<table class=\"gc-message-bg-bottom\">";
    private static final String isReadToken = "\"isRead\":";
    private static final String isSpamToken = ",\"isSpam\"";
    private static final String dateToken = "displayStartDateTime\":\"";
//    private static final String noMsgsString = "No unread items in your inbox.";
    private static final String msgBottomToken = "<td class=\"gc-sline-bottom\">";
    private static String[] markReadStr = {markReadURL,"","&read=1"};

    public static Vector readMsgs() throws ConnectionNotFoundException, IOException, Exception
    {
        System.gc();
        Vector newConvos = new Vector(5);
        String html = null;
        try{
            html = getHTML();
        }
        catch(ConnectionNotFoundException cnf)
        {
            Logger.add("parseMsgs", "readMsgs", cnf.getMessage());
            throw cnf;
        }
        //checks to see if new messages have arrived and returns if none have
        if(html.equals(""))// || html.indexOf(noMsgsString) > 0)
        {
            html = null;
            return null;
        }
        //gets message's ID & replyNums from json
        Hashtable convos = getJSONdata(html);

        //goes through xml for each msgID found in json (now existing as a hash enumeration)
        Enumeration convosEnum = convos.keys();
        textConvo crnt;
        String Key = "";
        int newMsgCnt = 0;
        textConvo messages = null;
        while(convosEnum.hasMoreElements())
        {
            Key = (String) convosEnum.nextElement();
            try {
                messages = getMsgs(Key, html);
            } catch (IOException ex) {
                Logger.add("parseMsgs", ex.getMessage());
                ex.printStackTrace();
            } catch (Exception ex) {
                Logger.add("parseMsgs", ex.getMessage());
                ex.printStackTrace();
            }

           if(messages == null)
               continue;

           if(Inbox.getInboxHash().containsKey(Key))
           {
               crnt = (textConvo) Inbox.getInboxHash().get(Key);
               crnt.setConvo(messages);
           }
           else
           {
               crnt = (textConvo) convos.get(Key);
               crnt.setConvo(messages);
           }

           newConvos.addElement(crnt);
           newMsgCnt++;
        }
        gvME.setNumNewMsgs(newMsgCnt);

        return newConvos;
    }

    private static textConvo getMsgs(String msgID, String html) throws ConnectionNotFoundException, IOException, Exception
    {
        Vector msgVect = new Vector();
        int i = 0;
        textMsg crnt = null;
        String beginToken = beginMsgToken + msgID;
        html = (String) regexreplace(i, html, beginToken, endMsgToken).getKey();

        int stop = html.indexOf(msgBottomToken, i);
        String sender = "";
        String message = "";
        String time = "";
//        String lastMessage = "";
        KeyValuePair kvp = null;

        int numMsgs = 0;

        //run until no more senders are found for the current conversation
        while(i > -1 && (checkSender((String) (kvp = regexreplace(i, stop, html, msgFromToken, endSpan)).getKey())) && i < stop && i < html.length())
        {
            i =  castInt(kvp.getValue());
            String hold_Sender = (String) kvp.getKey();

            if(hold_Sender.indexOf("Me:") < 0)
            {
                sender = parseSender(hold_Sender);//hold_Sender prevents garbage from being submitted as the sender of the message

                //gets message text
                kvp = regexreplace(i, html, msgTextToken, endSpan);
                message = (String) kvp.getKey();
                i = castInt(kvp.getValue());

//                if(lastMessage.equals(message))
//                {
//                    markMsgRead(msgID);
//                    return null;
//                }
                //gets time message was sent
                kvp = regexreplace(i, html, msgTimeToken, endSpan);
                time = ((String) kvp.getKey()).trim();

                //creates a new textMsg object and adds it to the list for this textConvo
                crnt = new textMsg(message, time);
                numMsgs++;
                msgVect.addElement(crnt);
            }
            i = castInt(kvp.getValue());
        }
        textConvo getMsgConvo = null;
        if(!sender.equals(""))
        {
            markMsgRead(msgID);
            getMsgConvo = new textConvo(numMsgs, false, msgID, sender, msgVect, crnt);
        }

        return getMsgConvo;
    }

    private static boolean checkSender(String sender)
    {
        if(sender.equals("") || sender.length() > 40)
            return false;
        else
            return true;
    }

    /**
     * Marks message as read on Google Voice
     */
    private static void markMsgRead(String msgID) throws IOException, IOException, Exception
    {
        markReadStr[1] = msgID; //markReadStr[0] is the base address for marking read
        connMgr.open(tools.combineStrings(markReadStr), "GET", null, "");
        connMgr.close();
    }

    /**
     * Parses data from the JSON block found at the top of the xml
     * @param html A String of HTML code
     * @return Returns a Hashtable: key=msgID; value=[an initial textConvo containing properties found in the JSON block].
     */
    private static Hashtable getJSONdata(String html)
    {
        int i = 0;
        KeyValuePair kvp = regexreplace(i, html, jsonBegin, jsonEnd);
        String json = (String) kvp.getKey();

        String msgID = "";
        String replyNum = "";
        String isRead = "";
        String date = "";
        Hashtable convoHash = new Hashtable();
        textConvo newConvo;

        while(json.indexOf(idToken, i) != -1)
        {
            kvp = regexreplace(i, json, idToken, separateToken);
            msgID = (String) kvp.getKey();
            i = castInt(kvp.getValue());

            kvp = regexreplace(i, json, phoneNumToken, separateToken);
            replyNum = (String) kvp.getKey();
            i = castInt(kvp.getValue());

            kvp = regexreplace(i, json, dateToken, separateToken);
            date = parseDate((String) kvp.getKey());
            i = castInt(kvp.getValue());

            kvp = regexreplace(i, json, isReadToken, isSpamToken);
            isRead = (String) kvp.getKey();
            i = castInt(kvp.getValue());

            if(isRead.equals("true"))
                break;

            newConvo = new textConvo(0, msgID, replyNum, date);
            convoHash.put(msgID, newConvo);
            newConvo = null;
        }

        return convoHash;
    }

    /**
     *  Finds the next occurrence of the regular expression: beginToken(.*)endToken
     * @param i Index of html to begin searching.
     * @param html HTML code to search within.
     * @param beginToken First phrase to look for. Specifies the beginning of the block of interest.
     * @param endToken Last phrase to look for. Specifies the end of the block of interest.
     * @return returns the index of the last character in the endToken
     */
    private static KeyValuePair regexreplace(int i, String html, String beginToken, String endToken)
    {
        return regexreplace(i, 0, html, beginToken, endToken);
    }

    /**
     *  Finds the next occurrence of the regular expression: beginToken(.*)endToken
     * @param i Index of html to begin searching.
     * @param stop Index of html that the search should not go past.
     * @param html HTML code to search within.
     * @param beginToken First phrase to look for. Specifies the beginning of the block of interest.
     * @param endToken Last phrase to look for. Specifies the end of the block of interest.
     * @return returns the index of the last character in the endToken
     */
    private static KeyValuePair regexreplace(int i, int stop, String html, String beginToken, String endToken)
    {
        i = html.indexOf(beginToken, i)+beginToken.length();
        int end = html.indexOf(endToken, i);
        KeyValuePair kvp = new KeyValuePair("", null);
        if(stop == 0 || (i >= 0 && end >= 0 && end < stop))
        {
            kvp = new KeyValuePair(html.substring(i, end), new Integer(end));
        }

        return kvp;
    }

    /**
     * Retrieves the HTML from the unread messages page on Google Voice
     * @return HTML found
     * @throws ConnectionNotFoundException
     * @throws IOException
     */
    private static String getHTML() throws ConnectionNotFoundException, IOException
    {
        String html = "";
        try{
            if(messagesToGet(getMsgsURL))
            {
                connMgr.open(getMsgsURL, "GET", null, "");
                html = connMgr.getPageData();
                connMgr.close();
            }
        }
        catch(ConnectionNotFoundException cnf)
        {
            //Logger.add("getHTML", "connection not found");
            throw cnf;
        }
        finally{
            try{
                connMgr.close();
            }
            catch(Exception ignore)
            {}

            return html;
        }
    }

    private static boolean messagesToGet(String reqString) throws IOException, ConnectionNotFoundException, Exception
    {
        connMgr.open(reqString, "GET", null, "");
        String jsonChunk = connMgr.getPageDataChunk(jsonEnd);
        connMgr.close();
        if(jsonChunk.indexOf(unreadIsZero) >= 0)
            return false;
        return true;
    }

    /**
     * Removes unnecessary characters from the sender String.
     * @param sender The raw String created from the text between the begin and end tokens used for obtaining the message sender.
     * @return Returns a string containing only the characters necessary for specifying the message sender.
     */
    private static String parseSender(String sender)
    {
        while(sender.indexOf('\n') >= 0)
        {
            sender = sender.replace('\n', ' ');
        }
        sender = sender.replace(':', ' ');
        return sender.trim();
    }

   /**
     * Removes unnecessary characters from the date String.
     * @param date The raw String created from the text between the begin and end tokens used for obtaining the date the message was sent.
     * @return Returns a string containing only the characters necessary for specifying the message's sent date.
     */
    private static String parseDate(String date)
    {
        int slashIndex = date.indexOf("/", date.indexOf("/")+1);
        return date.substring(0, slashIndex+3);
    }

    /**
     * Makes a primitive int from an Integer object
     * @param i An Object to be cast to a primitive int
     * @return Returns a primitive int
     */
    private static int castInt(Object i)
    {
        return ((Integer) i).intValue();
    }
}