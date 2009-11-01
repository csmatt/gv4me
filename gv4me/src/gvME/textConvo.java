/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author Matt Defenthaler
 */
public class textConvo {
    private int numMsgs = 0;
    private Vector messages = new Vector(10);
    private String sender = "";
    private String replyNum = "";
    private String msgID = "";
    private String date = "";
    private textMsg lastMsg;
    private boolean isRead = false;
    private static final int numConvoFields = 4;

    public textConvo(int numMsgs, boolean isRead, String msgID, String sender, String replyNum, String date, Vector messages, textMsg lastMsg)
    {
        this.numMsgs = numMsgs;
        this.msgID = msgID;
        this.sender = sender;
        this.messages = messages;
        this.replyNum = replyNum;
        this.lastMsg = lastMsg;
        this.date = date;
        this.isRead = isRead;
    }

    public textConvo(int numMsgs, boolean isRead, String msgID, String sender, Vector messages, textMsg msg)
    {
        this.numMsgs = numMsgs;
        this.msgID = msgID;
        this.sender = sender;
        this.messages = messages;
        this.lastMsg = msg;
    }

    public textConvo(int numMsgs, String msgID, String replyNum, String date)
    {
        this.numMsgs = numMsgs;
        this.msgID = msgID;
        this.replyNum = replyNum;
        this.date = date;
    }

    public textConvo(String sender, String replyNum, textMsg msg)
    {
        this.numMsgs = 0;
        this.msgID = "";
        this.date = "";
        this.sender = sender;
        this.replyNum = replyNum;
        this.lastMsg = msg;
    }

    public boolean setIsRead(boolean isRead)
    {
        this.isRead = isRead;
        return this.isRead;
    }

    public boolean getIsRead()
    {
        return isRead;
    }

    public void setConvo(textConvo convo)
    {
        this.sender = convo.getSender();

        for(int i = convo.getMessages().size()-1; i >= 0; i--)
        {
            textMsg crnt = (textMsg) convo.getMessages().elementAt(i);
            if(lastMsg != null && crnt.getMessage().equals(lastMsg.getMessage())
                    && lastMsg.getTimeReceived().equals(crnt.getTimeReceived()))
                break;
            messages.addElement(crnt);
            numMsgs++;
        }
        //isRead = false;//sets isRead back to false because we've received a new message
        this.lastMsg = convo.getLastMsg();
    }

    public void addNumMsgs(int addMsgs)
    {
        this.numMsgs += addMsgs;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public void setMessages(Vector messages)
    {
        this.messages = messages;
    }

    public int getNumMsgs()
    {
        return this.numMsgs;
    }
    public Vector getMessages()
    {
        return this.messages;
    }

    public String getSender()
    {
        return this.sender;
    }

    public String getMsgID()
    {
        return this.msgID;
    }

    public textMsg getLastMsg()
    {
        return this.lastMsg;
    }

    public textMsg getMsg()
    {
        return this.lastMsg;
    }

    public String getDate()
    {
        return this.date;
    }
    
    public String getReplyNum()
    {
        return this.replyNum;
    }

    public void setLastMsg(textMsg lastMsg)
    {
        this.lastMsg = lastMsg;
    }

    /**
     * Deserializes textMsg properties from data buffer and returns a new textMsg.
     * @param data - data for creating new setting.
     * @return new setting.
     */
    public static textConvo deserialize(byte[] data)
    {
        String[] fields = new String[numConvoFields];
        String[] textMsgFields = new String[textMsg.numFields];
        textMsg lastMessage, msg = null;
        Vector msgsVect = new Vector(5);
        boolean isRead;
        int numMsgs = 0;
        
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bis);

            numMsgs = Integer.parseInt(dis.readUTF());
            isRead = (Integer.parseInt(dis.readUTF())) == 1;
            
            for(int i=0; i < numConvoFields; i++)
            {
                fields[i] = dis.readUTF();
            }

            for(int i = 0; i < numMsgs; i++)
            {

                textMsgFields[0] = dis.readUTF();
                textMsgFields[1] = dis.readUTF();

                msg = new textMsg(textMsgFields[0], textMsgFields[1]);
                msgsVect.addElement(msg);
            }  

            //gets last message from RS for particular msgID
            textMsgFields[0] = dis.readUTF();
            textMsgFields[1] = dis.readUTF();

            lastMessage = new textMsg(textMsgFields[0], textMsgFields[1]);

            dis.close();
            bis.close();

            textConvo deserialized = new textConvo(numMsgs, isRead, fields[0], fields[1], fields[2], fields[3], msgsVect, lastMessage);
            
            return deserialized;
        }
        catch(IOException ex) {
            Logger.add("textConvo deserialize", ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] serializeMsgs(Vector messages) throws IOException
    {
        if(messages == null)
            return "".getBytes();
        
        Enumeration msgsVect = messages.elements();
        textMsg crnt;
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();

        while(msgsVect.hasMoreElements())
        {
            crnt = (textMsg) msgsVect.nextElement();
            byteOutStream.write(serializeMsg(crnt));
        }
        byte[] data = byteOutStream.toByteArray();
        byteOutStream.close();
        
        return data;
    }

    public static byte[] serializeMsg(textMsg crnt) throws IOException
    {
        if(crnt == null)
            return "".getBytes();
        String[] fields = {crnt.getMessage(), crnt.getTimeReceived()};
        return serial.serialize(fields);
    }

    /**
     * Serializes textMsg properties to an array of bytes.
     * @return array of bytes representing serialized setting.
     */
    public byte[] serialize() throws IOException
    {
        String[] fields = {this.msgID, this.sender, this.replyNum, this.date};
        String[] numMsgField = {String.valueOf(this.messages.size())};
        String[] isReadStr = {String.valueOf(((isRead) ? 1:0))};
        byte[] numMsgs_bytes = serial.serialize(numMsgField);
        byte[] isRead_bytes = serial.serialize(isReadStr);
        byte[] data = serial.serialize(fields);
        byte[] textMsgs = serializeMsgs(this.messages);
        byte[] lastMsg_bytes = serializeMsg(this.lastMsg);
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        byteOutStream.write(numMsgs_bytes);
        byteOutStream.write(isRead_bytes);
        byteOutStream.write(data);
        byteOutStream.write(textMsgs);
        byteOutStream.write(lastMsg_bytes);
        data = byteOutStream.toByteArray();
        byteOutStream.close();

        return data;
    }
}
