/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
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
    private Command sendCmd;
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
        addCommand(getSendCmd());
        setCommandListener(this);
    }

    private Command getSendCmd() {
        if (sendCmd == null) {
            sendCmd = new Command("Send", Command.ITEM, 1);
        }
        return sendCmd;
    }

    private Command getBackCmd() {
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
            } else if (command == sendCmd) {
                //original.setLastMsg(new textMsg(this.getString()));

                if(!title.equals("Reply")) //if it's a new message or a forwarded message
                {
                    SendMsg sm = new SendMsg(original, this.getString());
                    chooseContact = new ChooseContact(this, sm);
                    gvME.dispMan.switchDisplayable(null, chooseContact);
                }
                else
                {
                    SendMsg sm = new SendMsg(original, this.getString());
                    gvME.dispMan.switchDisplayable(null, sm);
                }
            }
        }
    }
}
