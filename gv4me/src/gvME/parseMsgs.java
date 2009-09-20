/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.HttpsConnection;
import ui.Inbox;

/**
 *
 * @author matt
 */
public class parseMsgs {
    private static final String markReadURL = "https://www.google.com/voice/m/mark?p=1&label=unread&id=";
    private static final String getMsgsURL = "https://www.google.com/voice/inbox/recent/unread";

    private static final String jsonBegin = "<json><![CDATA[{\"messages\":{\"";
    private static final String idToken = "{\"id\":\"";
    private static final String separateToken = "\",\"";
    private static final String phoneNumToken = "phoneNumber\":\"+1";
    private static final String jsonEnd = "}}";
    private static final String beginMsgToken = "<div id=\"";
    private static final String msgFromToken = "<span class=\"gc-message-sms-from\">";
    private static final String endSpan = "</span>";
    private static final String msgTextToken = "<span class=\"gc-message-sms-text\">";
    private static final String msgTimeToken = "<span class=\"gc-message-sms-time\">";
    private static final String endMsgToken = "<table class=\"gc-message-bg-bottom\">";
    private static final String isReadToken = "\"isRead\":";
    private static final String isSpamToken = ",\"isSpam\"";
    private static final String dateToken = "displayStartDateTime\":\"";
    private static final String noMsgsString = "No unread items in your inbox.";
    private static final String msgBottomToken = "<td class=\"gc-sline-bottom\">";

    private static Vector reqProps = new Vector(5);

    public static void setReqProps()
    {
        String[] contentType = {"Content-Type", "application/x-www-form-urlencoded"};
        String[] connection = {"Connection", "keep-alive"};
        reqProps.insertElementAt(contentType, 0);
        reqProps.insertElementAt(connection, 1);
    }

    public static Vector getReqProps()
    {
        return parseMsgs.reqProps;
    }

    public static Vector readMsgs() throws IOException, Exception
    {
        Vector newConvos = new Vector(5);
        String html = getHTML();

        //checks to see if new messages have arrived and returns if none have
        if(html.equals("") || html.indexOf(noMsgsString) > 0)
            return null;
        //gets message's ID & replyNums from json
        Hashtable convos = getJSONdata(html);

        //goes through xml for each msgID found in json (now existing as a hash enumeration)
        Enumeration convosEnum = convos.keys();
        Vector msgVect = null;
        String Key = "";
        int newMsgCnt = 0;
        textConvo messages = null;
        while(convosEnum.hasMoreElements())
        {
           Key = (String) convosEnum.nextElement();
            try {
                messages = getMsgs(Key, html);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
           if(messages == null)
               break;

           textConvo crnt;
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
        html = null;
        gvME.setNumNewMsgs(newMsgCnt);
        return newConvos;
    }

    private static textConvo getMsgs(String msgID, String html) throws IOException, Exception
    {
        Vector msgVect = new Vector(10);
        int i = 0;
        textMsg crnt = null;
        String beginToken = beginMsgToken + msgID;
        html = (String) regexreplace(i, html, beginToken, endMsgToken).getKey();

        int stop = html.indexOf(msgBottomToken, i);
        String sender = "";
        String message = "";
        String time = "";
        String lastMessage = "";
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
                kvp = regexreplace(i, html, msgTextToken, endSpan);
                message = (String) kvp.getKey();
                i = castInt(kvp.getValue());

                if(lastMessage.equals(message))
                {
                    markMsgRead(msgID);
                    return null;
                }
                kvp = regexreplace(i, html, msgTimeToken, endSpan);
                time = ((String) kvp.getKey()).trim();

                crnt = new textMsg(msgID, message, time);
                numMsgs++;
                msgVect.addElement(crnt);
            }
            i = castInt(kvp.getValue());
        }
        markMsgRead(msgID);
        return new textConvo(numMsgs, msgID, sender, msgVect, crnt);
    }

    private static boolean checkSender(String sender)
    {
        if(sender.equals("") || sender.length() > 25)
            return false;
        else
            return true;
    }

    //mark message as read
    private static void markMsgRead(String msgID) throws IOException, IOException, Exception
    {
        String[] strings = {
                            markReadURL,
                            msgID,
                            "&read=1"
                            };
        HttpsConnection markRead = createConnection.open(tools.combineStrings(strings), "GET", reqProps, "");
        createConnection.close(markRead);
        markRead = null;
    }

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

            textConvo newConvo = new textConvo(0, msgID, replyNum, date);
            convoHash.put(msgID, newConvo);
        }
        return convoHash;
    }
    /*
     *  regexreplace
     *  Finds the next occurrence of the regular expression: beginToken(.*)endToken
     *  It returns the index of the last character in the endToken
    */
    private static KeyValuePair regexreplace(int i, String html, String beginToken, String endToken)
    {
        return regexreplace(i, 0, html, beginToken, endToken);
    }
    private static KeyValuePair regexreplace(int i, int stop, String html, String beginToken, String endToken)
    {
        i = html.indexOf(beginToken, i)+beginToken.length();
        int end = html.indexOf(endToken, i);

        if(stop == 0 || (i >= 0 && end >= 0 && end < stop))
            return new KeyValuePair(html.substring(i, end), new Integer(end));
        else
            return new KeyValuePair("", null);
    }

    private static String getHTML() throws IOException
    {
        String html = "";
        DataInputStream dis = null;
        ByteArrayOutputStream baos = null;
        HttpsConnection c = null;
        try{
            c = createConnection.open(getMsgsURL, "GET", reqProps, "");

            //FIXME every 20 connections or so, a 400 HTTP response is returned. This is a temp workaround
            if(c.getResponseCode() != 200)
            {
                createConnection.close(c);
                RMSCookieConnector.removeCookies();
                gvLogin.logIn();
                c = createConnection.open(getMsgsURL, "GET", reqProps, "");
            }

            dis = c.openDataInputStream();
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read = dis.read(buffer);

            while(read != -1)
            {
                baos.write(buffer,0,read);
                read = dis.read(buffer);
            }
            html = new String(baos.toByteArray());
        }
        finally{
            dis.close();
            baos.close();
            createConnection.close(c);
            return html;
        }
    }

    private static String parseSender(String sender)
    {
        while(sender.indexOf('\n') >= 0)
        {
            sender = sender.replace('\n', ' ');
        }
        sender = sender.replace(':', ' ');
        return sender.trim();
    }

    private static String parseDate(String date)
    {
        int slashIndex = date.indexOf("/", date.indexOf("/")+1);
        return date.substring(0, slashIndex+3);
    }

    private static int castInt(Object i)
    {
        return ((Integer) i).intValue();
    }
}