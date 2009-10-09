/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.gvME;
import gvME.textConvo;
import java.io.IOException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author matt
 */
public class Outbox extends MailBox {
    private static final String outboxStore = "outboxStore";
    private Command sendCmd, sendAllCmd;

    public Outbox() throws RecordStoreException, IOException
    {
        super("Outbox", outboxStore);
        addCommand(getSendCmd());
        addCommand(getSendAllCmd());
    }

    public void addItem(textConvo convo) throws IOException, RecordStoreException
    {
        if(list.indexOf(convo) < 0)
            super.addItem(convo);
    }

    private Command getSendCmd()
    {
        if(sendCmd == null)
        {
            sendCmd = new Command("Send", Command.ITEM, 1);
        }
        return sendCmd;
    }

    private Command getSendAllCmd()
    {
        if(sendAllCmd == null)
        {
            sendAllCmd = new Command("Send All", Command.ITEM, 2);
        }
        return sendAllCmd;
    }

    public void commandAction(Command command, Displayable displayable) {
        if(command == backCmd || command == delItemCmd || command == delAllCmd)
        {
            super.commandAction(command, displayable);
        }
        else
        {
            if(command == sendCmd)
            {
                int index = getSelectedIndex();
                textConvo crnt = (textConvo) list.elementAt(index);
                SendMsg sm = new SendMsg(crnt, crnt.getLastMsg().getMessage());
                try {
                    this.delItem(index);
                } catch (RecordStoreException ex) {
                    ex.printStackTrace();
                }
                gvME.dispMan.switchDisplayable(null, sm);
            }
            else if(command == sendAllCmd)
            {
                for(int i = list.size()-1; i >= 0; i--)
                {
                    
                }
            }
        }
    }
}
