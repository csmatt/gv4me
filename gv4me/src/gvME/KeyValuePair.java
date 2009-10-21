/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;

/**
 * Stores two Objects
 * @author Matt Defenthaler
 */
public class KeyValuePair {

    private Object key;
    private Object value;

    /**
     * KeyValuePair constructor. Creates a new KeyValuePair with specified key and value.
     * @param key
     * @param value
     */
    public KeyValuePair(Object key, Object value)
    {
        this.key = key;
        this.value = value;
    }

    /**
     * Getter method for key;
     * @return key
     */
    public Object getKey()
    {
        return this.key;
    }

    /**
     * Getter method for value.
     * @return value.
     */
    public Object getValue()
    {
        return this.value;
    }

    /**
     * Serializes this KeyValuePair into a byte array.
     * @return byte array of this KeyValuePair
     * @throws IOException
     */
    public byte[] serialize() throws IOException
    {
        String[] fields = {this.key.toString(), this.value.toString()};
        return serial.serialize(fields);
    }

    /**
     * Deserializes a KeyValuePair
     * @param data A byte array of data to deserialize.
     * @return A new KeyValuePair containing deserialized key and value.
     * @throws IOException
     */
    public static KeyValuePair deserialize(byte[] data) throws IOException
    {
        String[] fields = serial.deserialize(2, data);
        KeyValuePair kvp = new KeyValuePair(fields[0], fields[1]);

        data = null;
        fields = null;

        return kvp;
    }
}

