/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.util.Vector;

/**
 *
 * @author matt
 */
    public class textMessages
    {
        private int numMsgs = 0;
        private Vector messages = new Vector(10);
        private textMsg lastMsg;
        private String sender;

        public textMessages(int numMsgs, String sender, textMsg lastMsg, Vector messages)
        {
            this.numMsgs = numMsgs;
            this.sender = sender;
            this.lastMsg = lastMsg;
            this.messages = messages;
        }

        public int getNumMsgs()
        {
            return this.numMsgs;
        }
        
        public String getSender()
        {
            return this.sender;
        }

        public textMsg getLastMsg()
        {
            return this.lastMsg;
        }

        public Vector getMessages()
        {
            return this.messages;
        }
    }
