/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

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
}

