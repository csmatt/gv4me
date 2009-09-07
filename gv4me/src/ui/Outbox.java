/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 *
 * @author matt
 */
public class Outbox extends List implements CommandListener {
    
    public Outbox()
    {
        super("Outbox", Choice.IMPLICIT);
//        addCommand(getViewMsgCmd());
//        addCommand(getDelItemCmd());
//        addCommand(getBackCmd());
//        setCommandListener(this);
//        setFitPolicy(Choice.TEXT_WRAP_OFF);
//        setSelectCommand(getViewMsgCmd());
    }
    public void commandAction(Command command, Displayable displayable) {
    }

}
