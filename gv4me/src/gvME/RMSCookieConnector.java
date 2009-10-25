/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

/*
 * Extended and enhanced by Michael Juntao Yuan and Ju Long for
 * JavaWorld.
 *
 * Sun's original copyright notice is as following:
 *
 * Copyright 1999-2001 Sun Microsystems, Inc. ALL RIGHTS RESERVED
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
 * OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
 * LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of
 * any nuclear facility.
 */

import java.io.*;

import java.util.Enumeration;
import java.util.Hashtable;
//import java.util.Vector;
import javax.microedition.io.*;
//import javax.microedition.rms.*;

/**
 * Simple way of putting persistent cookie support into the framework. Use
 * RMSCookieConnector.open() instead of Connector.open()
 */
public class RMSCookieConnector {

    // The default name of the RMS to store cookies.
    private static String cookieStoreName = "cookieStore";
    private static Hashtable cookieHash = new Hashtable();

    // Use the default RMS cookie store name.
    public static HttpsConnection open(String url) throws ConnectionNotFoundException, Exception {
        return open(url, cookieStoreName);
    }

//    private static Hashtable getCookieHash()
//    {
//        if(cookieHash == null)
//        {
//            cookieHash = new Hashtable();
//        }
//        return cookieHash;
//    }

    // Open a new connection and save cookie into the specified store.
    public static HttpsConnection open(String url, String storeName)
                                                      throws ConnectionNotFoundException, Exception {
        cookieStoreName = storeName;

        // Open a new connection.
        HttpsConnection c = (HttpsConnection) Connector.open(url);
        // Find cookies from the store and add to the connection header.
        addCookie(c, url);
//        c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//        c.setRequestProperty("Content-Language", "en-US");
        // Cookie aware wrapper to the connection object.
        HttpsConnection sc = new HttpsRMSCookieConnection(c);
        return sc;
    }

    // Close the connection and streams.
    public static void close(HttpsConnection conn,
			     InputStream is,
			     OutputStream os) {
	if (is != null) {
	    try {
		is.close();
	    } catch (IOException ignore) {
		// ignore
	    }
	}

	if (os != null) {
	    try {
		os.close();
	    } catch (IOException ignore) {
		// ignore
	    }
	}

	if (conn != null) {
	    try {
		conn.close();
	    } catch (IOException ignore) {
		// ignore
	    }
	}
    }

    // Retreive cookies from the connection header and save
    // them into the cookie store.
    //
    // In the store, each cookie is associated with a domain.
    //
    // The record store format is:
    // cookie1, domain1, cookie2, domain2 ...
    static void getCookie(HttpsConnection c) throws IOException//, RecordStoreNotOpenException, RecordStoreException
    {
      int k = 0;
//      RecordStore rs = null;
      try {
        removeCookies();
//        rs = RecordStore.openRecordStore(cookieStoreName, true);
        // Iterate through connection headers and find "set-cookie" fields.
        while (c.getHeaderFieldKey(k) != null) {
          String key = c.getHeaderFieldKey(k);
          String value = c.getHeaderField(k);
          if (key.toLowerCase().equals("set-cookie")) {
            // Parse the header and get the cookie.
            int j = value.indexOf(";");
            String cValue = value.substring(0, j);
            int index = cValue.indexOf("=");
            String cookieName = cValue.substring(0, index);
            String cookieValue = cValue.substring(index+1);
            
            // Write the cookie into the cookie store.
            cookieHash.put(cookieName, cookieValue);
//            if(!cookieVect.contains(cValue))
//            {
//                String[] strings = {cookieName, "=", cookieValue};
//                cValue = tools.combineStrings(strings);
//                cookieVect.addElement(cValue);
//            }

            // Write the cookie into the cookie store.
//            byte[] cValue_bytes = cValue.getBytes();
//            rs.addRecord(cValue_bytes, 0, cValue_bytes.length);
          }
          k++;
        }
      } catch ( Exception e ) {
          Logger.add("RMSCookieConnector", "getCookie", e.getMessage());
          close(c,null, null);
          throw new IOException( e.getMessage() );
      }
      finally{
        // Close store.
//        rs.closeRecordStore();
        return;
      }
    }

    // This method matches cookies in the store with the domain
    // of the connection. The matched cookies are set into the
    // headers of the connection.
    static void addCookie(HttpsConnection c, String url) throws Exception {
//        RecordStore rs = null;
//        RecordEnumeration re = null;
        try{

      StringBuffer buff = new StringBuffer();
      try{
//          rs = RecordStore.openRecordStore(cookieStoreName, true);
//          re = rs.enumerateRecords(null, null, false);
          String cookie = "";
          String cookieName = "";

//          while ( re.hasNextElement() ) {
//            cookie = new String(re.nextRecord());
          Enumeration cookieEnum = cookieHash.keys();
          while(cookieEnum.hasMoreElements())
          {
              cookieName = (String)cookieEnum.nextElement();
              cookie = (String)cookieHash.get(cookieName);
              String[] cookieString = {cookieName, "=", cookie};
              cookie = tools.combineStrings(cookieString);
              if(cookie.indexOf("EXPIRED") < 0)
              {
                buff.append( cookie );
                buff.append("; ");
              }
          }
      }
      catch(Exception e)
      {
          Logger.add("RMSCookieConnector", "addCookie", e.getMessage());
          throw new Exception(e.getMessage());
//       System.out.println("error opening record" + e.toString());
      }
      // If we do have cookies to send, set the composed string into
      // "cookie" header.
      String cookieStr = buff.toString();
      if ( cookieStr == null || cookieStr.equals("") ) {
        // Ignore
      } else {
        c.setRequestProperty( "cookie", cookieStr );
//        System.out.println("setting cookies ");
//        System.out.println(cookieStr);
      }
        }
        finally{
            // Close the store.
//            rs.closeRecordStore();
//            re.destroy();
            return;
        }
    }

    // Remove all cookies.
    public static void removeCookies() throws Exception {
//        try{
//        RecordStore.deleteRecordStore(cookieStoreName);
            cookieHash.clear();
//        return;
//        }
//        catch(RecordStoreNotFoundException ex)
//        {}
//        catch(Exception ex)
//        {
//            Logger.add("RMSCookieConnector", "removeCookies", ex.getMessage());
//        }
    }

}

// Wrapper (decorator) class.
class HttpsRMSCookieConnection implements HttpsConnection {
    private HttpsConnection c;

    public HttpsRMSCookieConnection(HttpsConnection c) {
        this.c = c;
    }

    public String getURL() {
        return c.getURL();
    }

    public String getProtocol() {
        return c.getProtocol();
    }

    public String getHost() {
        return c.getHost();
    }

    public String getFile() {
        return c.getFile();
    }

    public String getRef() {
        return c.getRef();
    }

    public String getQuery() {
        return c.getQuery();
    }

    public int getPort() {
        return c.getPort();
    }

    public String getRequestMethod() {
        return c.getRequestMethod();
    }

    public void setRequestMethod(String method) throws IOException {
        c.setRequestMethod(method);
    }

    public String getRequestProperty(String key) {
        return c.getRequestProperty(key);
    }

    public void setRequestProperty(String key, String value)
        throws IOException {
        c.setRequestProperty(key, value);
//        System.out.println("Setting reqProp: "+key+" - "+value);
    }

    public int getResponseCode() throws IOException {
        return c.getResponseCode();
    }

    public String getResponseMessage() throws IOException {
        return c.getResponseMessage();
    }

    public long getExpiration() throws IOException {
        return c.getExpiration();
    }

    public long getDate() throws IOException {
        return c.getDate();
    }

    public long getLastModified() throws IOException {
        return c.getLastModified();
    }

    public String getHeaderField(String name) throws IOException {
        return c.getHeaderField(name);
    }

    public int getHeaderFieldInt(String name, int def)
        throws IOException {
        return c.getHeaderFieldInt(name, def);
    }

    public long getHeaderFieldDate(String name, long def)
        throws IOException {
        return c.getHeaderFieldDate(name, def);
    }

    public String getHeaderField(int n) throws IOException {
        return c.getHeaderField(n);
    }

    public String getHeaderFieldKey(int n) throws IOException  {
        return c.getHeaderFieldKey(n);
    }

    public String getType() {
        return c.getType();
    }

    public String getEncoding() {
        return c.getEncoding();
    }

    public long getLength() {
        return c.getLength();
    }

    public void close() throws IOException {
        c.close();
    }

    // The cookies have to be retrieved when we open the input stream.
    public InputStream openInputStream() throws IOException {
        checkResponseCode();
//        try {
            RMSCookieConnector.getCookie(c);
//        } catch (RecordStoreNotOpenException ex) {
//            ex.printStackTrace();
//        } catch (RecordStoreException ex) {
//            ex.printStackTrace();
//        }

        return c.openInputStream();
    }

    // The cookies have to be retrieved when we open the input stream.
    public DataInputStream openDataInputStream() throws IOException {
        checkResponseCode();
//        try {
            RMSCookieConnector.getCookie(c);
//        } catch (RecordStoreNotOpenException ex) {
//            ex.printStackTrace();
//        } catch (RecordStoreException ex) {
//            ex.printStackTrace();
//        }
        return c.openDataInputStream();
    }

    public OutputStream openOutputStream()
        throws IOException {
        return c.openOutputStream();
    }

    public DataOutputStream openDataOutputStream()
        throws IOException {
        return c.openDataOutputStream();
    }

    private void checkResponseCode() throws IOException {
        try{
//            int code = c.getResponseCode();
//            if (code != c.HTTP_OK) {
//
//            }
        }
        catch(Exception e)
        {}
    }

    public SecurityInfo getSecurityInfo() throws IOException {
        return null;
    }


}
