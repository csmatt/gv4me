/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author matt
 */
public class tools {

    public static Vector combineVectors(Vector a, Vector b)
    {
        //Vector newVect = new Vector(a.size() + b.size());
        Enumeration vectEnum = b.elements();
        while(vectEnum.hasMoreElements())
        {
            a.addElement(vectEnum.nextElement());
        }
        return a;
    }

    public static Hashtable combineHashtables(Hashtable a, Hashtable b)
    {
        //Vector newVect = new Vector(a.size() + b.size());
        //Enumeration vectEnum = b.elements();
        Enumeration hashEnum = b.keys();
        while(hashEnum.hasMoreElements())
        {
            String Key = (String) hashEnum.nextElement();
            a.put(Key, b.get(Key));
        }
        return a;
    }

    public static String combineStrings(String[] strings)
    {
        StringBuffer strBuf = new StringBuffer();
        for(int i = 0; i < strings.length; i++)
        {
            strBuf.append(strings[i]);
        }
        return new String(strBuf);
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


