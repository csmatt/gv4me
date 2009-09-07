/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
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
    private textConvo original;
    private String title;

    public WriteMsg(String title, textConvo original)
    {
        super(title, "", 160, TextField.ANY);
        this.title = title;
        this.original = original;
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
            OKCmd = new Command("Send", Command.ITEM, 1);
        }
        return OKCmd;
    }

    public Command getBackCmd() {
        if (backCmd == null) {
            backCmd = new Command("Back", Command.BACK, 0);
        }
        return backCmd;
    }
    public void commandAction(Command command, Displayable displayable) {
        if(displayable == this){
            if (command == backCmd) {
                if(this.title.equals("Write New"))
                {
                    gvME.dispMan.switchDisplayable(null, gvME.getMenu());
                }
                else
                {
                    gvME.dispMan.switchToPreviousDisplayable();
                }
            } else if (command == OKCmd) {
                if(!title.equals("Reply")) //if it's a new message or a forwarded message
                {
                    displayChooseContact();
                }
                else
                {
                    SendMsg sm = new SendMsg(original, this.getString());
                    gvME.dispMan.switchDisplayable(null, sm);
                }
            }
        }
    }

    public void displayChooseContact()
    {
            SendMsg sm = new SendMsg(original, this.getString());
            chooseContact = new ChooseContact(this, sm);

            gvME.dispMan.switchDisplayable(null, chooseContact);
    }
}
