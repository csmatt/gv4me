/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author matt
 */
public class updateRS {
    public static void removeConvo(textConvo convo, String rsName) throws InvalidRecordIDException, IOException, RecordStoreNotOpenException, RecordStoreException
    {
        //Vector vectOfRS = vectFromRS(rsName);
    //    Vector convos = parseMsgs.getConvosVect();
  //      Enumeration convosEnum = convos.elements();
 /*       textConvo crnt;
        for(int i = 0; i < convos.size(); i++)
        {
            crnt = (textConvo) convos.elementAt(i);
            if(crnt.getMsgID().equals(msgID))
            {
                convos.removeElementAt(i);
            }
        }
*/

     //   parseMsgs.setConvosVect();


    }

    public static Vector vectFromRS(String rsName) throws InvalidRecordIDException, IOException, RecordStoreNotOpenException, RecordStoreException
    {
        RecordStore rs = RecordStore.openRecordStore(rsName, true);
        if(rs.getNumRecords() == 0)
        {
            rs.closeRecordStore();
            return new Vector(10);
        }
        RecordEnumeration re = rs.enumerateRecords(null, null, false);

        Vector vectOfRS = new Vector(10);
        //Hashtable hashOfRS = new Hashtable();
        //add all records to a hashtable so we can work with them
        while(re.hasNextElement())
        {
            textConvo crnt = textConvo.deserialize(re.nextRecord());
            int numMsgs = crnt.getNumMsgs();
            String msgID = crnt.getMsgID();
            String sender = crnt.getSender();
            String replyNum = crnt.getReplyNum();
            String date = crnt.getDate();
            Vector messages = crnt.getMessages();
            textMsg lastMsg = crnt.getLastMsg();

            textConvo newConvo = new textConvo(numMsgs, msgID, sender, replyNum, date, messages, lastMsg);
            vectOfRS.addElement(newConvo);
        }

        parseMsgs.setStoredConvos(vectOfRS);
        rs.closeRecordStore();
//        RecordStore.deleteRecordStore(rsName);
        re.destroy();
        return vectOfRS;
    }
    public static void fromVect(Vector convos, String rsName) throws IOException, RecordStoreException
    {
        textConvo crnt;
        Enumeration convosEnum = convos.elements();
        try{
        RecordStore.deleteRecordStore(rsName);
        }
        catch(Exception e)
        {
            System.out.println("RS not found, so couldn't delete " + rsName);
            //ignore if rs doesn't exist.
        }
        RecordStore rs = RecordStore.openRecordStore(rsName, true);
        for(int i = convos.size()-1; i >= 0; i--)
        {
            crnt = (textConvo) convos.elementAt(i);
            byte[] data = crnt.serialize();
            rs.addRecord(data, 0, data.length);
        }
        rs.closeRecordStore();
    }

    public static Vector serializeVector(Vector vect)
    {
        Vector serializedMsgs = new Vector(20);
        Enumeration vectEnum = vect.elements();

        while(vectEnum.hasMoreElements())
        {
            textMsg crnt = (textMsg) vectEnum.nextElement();
            byte[] crntBytes = crnt.serialize();
            serializedMsgs.addElement(crntBytes);

        }
        return serializedMsgs;
    }
}
