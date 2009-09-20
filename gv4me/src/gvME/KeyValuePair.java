/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;

/**
 *
 * @author matt
 */
public class KeyValuePair {

    private Object key;
    private Object value;

    public KeyValuePair(Object key, Object value)
    {
        this.key = key;
        this.value = value;
    }

    public Object getKey()
    {
        return this.key;
    }

    public Object getValue()
    {
        return this.value;
    }

    public byte[] serialize() throws IOException
    {
        String[] fields = {this.key.toString(), this.value.toString()};
        return serial.serialize(fields);
    }

    public static KeyValuePair deserialize(byte[] data) throws IOException
    {
        String[] fields = serial.deserialize(2, data);
        return new KeyValuePair(fields[0], fields[1]);
    }
}

