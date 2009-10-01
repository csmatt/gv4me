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
        
        for(int i = 0; i < strArray.length && strArray[i] != null; i++)
        {
            vect.addElement(new KeyValuePair(strArray[i],strArray[i++]));
        }

        data = null;
        strArray = null;
        
        return vect;
    }

    public static byte[] serializeKVPVector(Vector vect) throws IOException
    {
        KeyValuePair kvp;
        int vectSize = vect.size();
        String[] strArray = new String[2*vectSize];
        
        for(int i = 0, j = 0; j < vectSize; i+=2, j++)
        {
            kvp = (KeyValuePair) vect.elementAt(j);
            strArray[i] = (String) kvp.getKey();
            strArray[i+1] = (String) kvp.getValue();
        }
        byte[] data = serialize(strArray);

        kvp = null;
        vect = null;
        strArray = null;
        
        return data;
    }

    public synchronized static String[] deserialize(int numFields, byte[] data) throws IOException
    {
        String[] fields = new String[numFields];
        ByteArrayInputStream bis = null;
        DataInputStream dis = null;
        try {
            bis = new ByteArrayInputStream(data);
            dis = new DataInputStream(bis);

            for(int i=0; i < numFields; i++)
            {
                fields[i] = dis.readUTF();
            }
        }
        catch(IOException exc) {
            throw exc;
        }
        finally{
            dis.close();
            bis.close();
            dis = null;
            bis = null;
            
            return fields;
        }
    }

    /*
     * Serializes textMsg properties to an array of bytes.
     * @return array of bytes representing serialized setting.
     */
    public synchronized static byte[] serialize(String[] fields) throws IOException {
        byte[] data = null;
        ByteArrayOutputStream bos = null;
        DataOutputStream dos = null;
        try {
            bos = new ByteArrayOutputStream();
            dos = new DataOutputStream(bos);

            for(int i=0; i < fields.length; i++)
            {
                dos.writeUTF(fields[i]);
            }
            data = bos.toByteArray();
        } catch(IOException exc) {
            exc.printStackTrace();
            return null;
        }
        finally{
            dos.flush();
            dos.close();
            bos.close();
            dos = null;
            bos = null;
            return data;
        }
    }
}
