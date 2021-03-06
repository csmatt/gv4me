/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.io.IOException;

/**
 *
 * @author Matt Defenthaler
 */
public class textMsg {
    private String message;
    private String timeReceived;
    public static final int numFields = 2;

    public textMsg(String message, String timeReceived)
    {
        this.message = message;
        this.timeReceived = timeReceived;
    }

    public textMsg(String msg)
    {
        this.message = msg;
        this.timeReceived = "";
    }
    
    public String getMessage()
    {
        return this.message;
    }

    public String getTimeReceived()
    {
        return this.timeReceived;
    }

    /**
     * Deserializes textMsg properties from data buffer and returns a new textMsg.
     * @param data - data for creating new setting.
     * @return new setting.
     */
    public static textMsg deserialize(byte[] data) throws IOException
    {
        String[] fields = serial.deserialize(numFields, data);
        data = null;
        return new textMsg(fields[0], fields[1]);//, fields[2]);
    }

    /**
     * Serializes textMsg properties to an array of bytes.
     * @return array of bytes representing serialized setting.
     */
    public byte[] serialize() throws IOException
    {
        String[] fields = {this.message, this.timeReceived};
        return serial.serialize(fields);
    }
}
