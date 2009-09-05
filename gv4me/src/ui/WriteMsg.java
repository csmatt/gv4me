/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author matt
 */
public class WriteMsg extends TextBox implements CommandListener {
    private ChooseContact chooseContact;
    private Command OKCmd;
    private Command backCmd;
    private gvME midlet;
    private textConvo original;
    private String title = "";
    private String rnr = "";
    private Vector reqProps;

    public WriteMsg(gvME midlet, String title, textConvo original)
    {
        super(title, "", 160, TextField.ANY);
        this.midlet = midlet;
        this.title = title;
        this.original = original;
        this.rnr = midlet.rnr;
        this.reqProps = parseMsgs.getReqProps();
        String text = "";
        if(title.equals("Forward"))
        {
            StringBuffer textBuff = new StringBuffer("Fwd: ");
            textBuff.append(original.getMsg().getMessage());
            text = new String(textBuff);
            setString(text);
            this.original = null; //because a forwarded message is nothing more than a new message with default text
        }
        addCommand(getBackCmd());
        addCommand(getOKCmd());
        setCommandListener(this);
    }

    public Command getOKCmd() {
        if (OKCmd == null) {
            // write pre-init user code here
            OKCmd = new Command("Send", Command.ITEM, 1);
            // write post-init user code here
        }
        return OKCmd;
    }

    public Command getBackCmd() {
        if (backCmd == null) {
            // write pre-init user code here
            backCmd = new Command("Back", Command.BACK, 0);
            // write post-init user code here
        }
        return backCmd;
    }
    public void commandAction(Command command, Displayable displayable) {
        if(displayable == this){
        if (command == backCmd) {
            // write pre-action user code here
            if(this.title.equals("Write New"))
            {
                midlet.dispMan.switchDisplayable(null, midlet.getMenu());
            }
            else
            {
                midlet.dispMan.switchToPreviousDisplayable();
            }
            // write post-action user code here
        } else if (command == OKCmd) {
            if(!title.equals("Reply")) //if it's a new message or a forwarded message
            {
                displayChooseContact();
            }
            else
            {
                SendMsg sm = new SendMsg(midlet, original, this.getString(), rnr, reqProps);
                midlet.dispMan.switchDisplayable(null, sm);
            }
        }
        }
    }

    public void displayChooseContact()
    {

            SendMsg sm = new SendMsg(midlet, original, this.getString(), rnr, reqProps);
            chooseContact = new ChooseContact(midlet, this, sm);

            midlet.dispMan.switchDisplayable(null, chooseContact);
    }
}
