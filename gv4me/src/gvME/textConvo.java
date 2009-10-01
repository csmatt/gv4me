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
 * @author matt
 */
public class textConvo {
    private int numMsgs = 0;
    private Vector messages = new Vector(10);
    private String sender = "";
    private String replyNum = "";
    private String msgID = "";
    private String date = "";
    private textMsg lastMsg;
    private static int numConvoFields = 4;
    private static int numMsgFields = 3;

    public textConvo(int numMsgs, String msgID, String sender, String replyNum, String date, Vector messages, textMsg lastMsg)
    {
        this.numMsgs = numMsgs;
        this.msgID = msgID;
        this.sender = sender;
        this.messages = messages;
        this.replyNum = replyNum;
        this.lastMsg = lastMsg;
        this.date = date;
    }

    public textConvo(String sender, String replyNum, textMsg msg)
    {
        this.numMsgs = 0;
        this.msgID = "";
        this.date = "";
        this.messages.addElement(msg);
        this.sender = sender;
        this.replyNum = replyNum;
        this.lastMsg = new textMsg("");
    }
    public textConvo(int numMsgs, String msgID, String replyNum, String date)
    {
        this.numMsgs = numMsgs;
        this.msgID = msgID;
        this.replyNum = replyNum;
        this.date = date;
    }

    public textConvo(int numMsgs, String msgID, String sender, Vector messages, textMsg msg)
    {
        this.numMsgs = numMsgs;
        this.msgID = msgID;
        this.sender = sender;
        this.messages = messages;
        this.lastMsg = msg;
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
        int numMsgs = 0;
        
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bis);

            numMsgs = Integer.parseInt(dis.readUTF());

            for(int i=0; i < numConvoFields; i++)
            {
                fields[i] = dis.readUTF();
            }

            for(int i = 0; i < numMsgs; i++)
            {

                textMsgFields[0] = dis.readUTF();
                textMsgFields[1] = dis.readUTF();
                textMsgFields[2] = dis.readUTF();

                msg = new textMsg(textMsgFields[0], textMsgFields[1], textMsgFields[2]);
                msgsVect.addElement(msg);
            }  

            //gets last message from RS for particular msgID
            textMsgFields[0] = dis.readUTF();
            textMsgFields[1] = dis.readUTF();
            textMsgFields[2] = dis.readUTF();
            lastMessage = new textMsg(textMsgFields[0], textMsgFields[1], textMsgFields[2]);

            dis.close();
            bis.close();

            textConvo deserialized = new textConvo(numMsgs, fields[0], fields[1], fields[2], fields[3], msgsVect, lastMessage);

            dis = null;
            bis = null;
            msg = null;
            data = null;
            fields = null;
            msgsVect = null;
            lastMessage = null;
            textMsgFields = null;
            
            return deserialized;
            
        }
        catch(IOException exc) {
            exc.printStackTrace();
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
        String[] fields = {crnt.getMsgID(), crnt.getMessage(), crnt.getTimeReceived()};
        return serial.serialize(fields);
    }

    /**
     * Serializes textMsg properties to an array of bytes.
     * @return array of bytes representing serialized setting.
     */
    public byte[] serialize() throws IOException
    {
        String[] fields = {this.msgID, this.sender, this.replyNum, this.date};
        String[] numMsgField = {String.valueOf(this.messages.size())};//this.numMsgs)};
        byte[] numMsgs_bytes = serial.serialize(numMsgField);
        byte[] data = serial.serialize(fields);
        byte[] textMsgs = serializeMsgs(this.messages);
        byte[] lastMsg_bytes = serializeMsg(this.lastMsg);
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        byteOutStream.write(numMsgs_bytes);
        byteOutStream.write(data);
        byteOutStream.write(textMsgs);
        byteOutStream.write(lastMsg_bytes);
        data = byteOutStream.toByteArray();
        byteOutStream.close();
        
        fields = null;
        textMsgs = null;
        numMsgField = null;
        byteOutStream = null;
        lastMsg_bytes = null;
        numMsgs_bytes = null;
        
        return data;
    }
}
