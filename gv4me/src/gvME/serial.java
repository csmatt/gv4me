/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author matt
 */
public class serial {

    public synchronized static Vector deserializeKVPVector(int MAX, byte[] data)
    {
        String[] strArray = null;
        Vector vect = new Vector(10);
        try
        {
            strArray = deserialize(2*MAX, data);
        }
        catch(Exception e)
        {}
        
        for(int i = strArray.length; i >= 0; i--)
        {
            vect.addElement(new KeyValuePair(strArray[i--],strArray[i]));
        }
        return vect;
    }

    public static byte[] serializeKVPVector(Vector vect)
    {
        KeyValuePair kvp;
        int i = vect.size() - 1;
        int j = 0;
        String[] strArray = new String[2*(i+1)];
        while(i >= 0)
        {
            kvp = (KeyValuePair) vect.elementAt(i);
            strArray[i] = (String) kvp.getValue();
            strArray[i-1] = (String) kvp.getKey();
            i-=2;
        }
        return serialize(strArray);
    }

    public synchronized static String[] deserialize(int numFields, byte[] data)
    {
        String[] fields = new String[numFields];
        try {
            ByteArrayInputStream byteInStream = new ByteArrayInputStream(data);
            DataInputStream dataInStream = new DataInputStream(byteInStream);

            for(int i=0; i < numFields; i++)
            {
                fields[i] = dataInStream.readUTF();
            }

            dataInStream.close();
            byteInStream.close();
            return fields;
        }
        catch(IOException exc) {
            exc.printStackTrace();
            return null;
        }
    }

    /*
     * Serializes textMsg properties to an array of bytes.
     * @return array of bytes representing serialized setting.
     */
    public synchronized static byte[] serialize(String[] fields) {
        byte[] data = null;

        try {
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            DataOutputStream dataOutStream = new DataOutputStream(byteOutStream);

            for(int i=0; i < fields.length; i++)
            {
                dataOutStream.writeUTF(fields[i]);
            }

            dataOutStream.flush();
            data = byteOutStream.toByteArray();

            dataOutStream.close();
            byteOutStream.close();

        } catch(IOException exc) {
            return null;
        }
        return data;
    }
}
