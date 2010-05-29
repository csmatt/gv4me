/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Matt Defenthaler
 */
public class tools {

    /**
     * Returns a vector from combining vectors a and b
     * @param a
     * @param b
     * @return
     */
    public static Vector combineVectors(Vector a, Vector b)
    {
        Enumeration vectEnum = b.elements();
        while(vectEnum.hasMoreElements())
        {
            a.addElement(vectEnum.nextElement());
        }
        vectEnum = null;
        b = null;
        return a;
    }

    /**
     * Returns a hashtable from combinging hashtable's a and b
     * @param a
     * @param b
     * @return
     */
    public static Hashtable combineHashtables(Hashtable a, Hashtable b)
    {
        Enumeration hashEnum = b.keys();
        while(hashEnum.hasMoreElements())
        {
            String Key = (String) hashEnum.nextElement();
            a.put(Key, b.get(Key));
        }
        hashEnum = null;
        b = null;
        return a;
    }

    /**
     * Returns a String from concatenating Strings from the strings String array. This is more efficient than using the '+' operator on Strings
     * @param strings
     * @return
     */
    public static String combineStrings(String[] strings)
    {
        StringBuffer strBuf = new StringBuffer();
        int strsLen = strings.length;
        for(int i = 0; i < strsLen; i++)
        {
            strBuf.append(strings[i]);
        }
        String combined = new String(strBuf);
        strBuf = null;
        return combined;
    }

    /**
     * Returns a Vector of byte arrays created from textMsg's within the vect Vector
     * @param vect
     * @return
     * @throws IOException
     */
    public static Vector serializeVector(Vector vect) throws IOException
    {
        Vector serializedMsgs = new Vector(5);
        Enumeration vectEnum = vect.elements();
        byte[] crntBytes;
        textMsg crnt;
        while(vectEnum.hasMoreElements())
        {
            crnt = (textMsg) vectEnum.nextElement();
            crntBytes = crnt.serialize();
            serializedMsgs.addElement(crntBytes);
        }
        crntBytes = null;
        crnt = null;
        vectEnum = null;
        vect = null;
        return serializedMsgs;
    }

    /**
     * Returns a properly formatted String from the source String
     * @param source
     * @return
     */
    public static String decodeString(String source) {
        if (source == null) {
            return "";
        }

        String[] patterns = {"&amp;", "&gt;", "&lt;", "&quot;"};
        String[] replacements = {"&", ">", "<", "\""};

        StringBuffer sb = new StringBuffer();
        int i = 0;
        for(i = patterns.length - 1; i >= 0; i--)
        {
            int idx = -1;
            int patIdx = 0;
            sb = new StringBuffer();
            while ((idx = source.indexOf(patterns[i], patIdx)) != -1) {
                sb.append(source.substring(patIdx, idx));
                sb.append(replacements[i]);
                patIdx = idx + patterns[i].length();
            }
            source = (sb.append(source.substring(patIdx))).toString();
        }
        String decoded = new String(sb);
        sb = null;
        patterns = null;
        replacements = null;
        return decoded;
    }
}


