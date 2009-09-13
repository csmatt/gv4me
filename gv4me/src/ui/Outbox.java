/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.io.IOException;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author matt
 */
public class Outbox extends MailBox {
    private static final String outboxStore = "outboxStore";
    public Outbox() throws RecordStoreException, IOException
    {
        super("Outbox", outboxStore);
    }
    public void commandAction(Command command, Displayable displayable) {
    }

}
