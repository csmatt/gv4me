/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Enumeration;
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
//    public static Vector vectFromRS(String rsName) throws InvalidRecordIDException, IOException, RecordStoreNotOpenException, RecordStoreException
//    {
//        Vector vectOfRS = new Vector(10);
//        RecordStore rs = RecordStore.openRecordStore(rsName, true);
//        RecordEnumeration re = rs.enumerateRecords(null, null, false);
//
//        while(re.hasNextElement())
//        {
//            textConvo crnt = textConvo.deserialize(re.nextRecord());
//            vectOfRS.addElement(crnt);
//        }
//        rs.closeRecordStore();
//        re.destroy();
//        return vectOfRS;
//    }
//
//    public static void fromVect(Vector convos, String rsName) throws IOException, RecordStoreException
//    {
//        textConvo crnt;
//        Enumeration convosEnum = convos.elements();
//        try{
//        RecordStore.deleteRecordStore(rsName);
//        }
//        catch(Exception e)
//        {
//            System.out.println("RS not found, so couldn't delete " + rsName);
//            //ignore if rs doesn't exist.
//        }
//        RecordStore rs = RecordStore.openRecordStore(rsName, true);
//
//        for(int i = convos.size()-1; i >= 0; i--)
//        {
//            crnt = (textConvo) convos.elementAt(i);
//            byte[] data = crnt.serialize();
//            rs.addRecord(data, 0, data.length);
//        }
//        rs.closeRecordStore();
//    }
//
//    public static Vector serializeVector(Vector vect)
//    {
//        Vector serializedMsgs = new Vector(20);
//        Enumeration vectEnum = vect.elements();
//
//        while(vectEnum.hasMoreElements())
//        {
//            textMsg crnt = (textMsg) vectEnum.nextElement();
//            byte[] crntBytes = crnt.serialize();
//            serializedMsgs.addElement(crntBytes);
//
//        }
//        return serializedMsgs;
//    }
}
