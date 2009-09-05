/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.pim.Contact;
import javax.microedition.pim.PIM;
import org.netbeans.microedition.lcdui.pda.PIMBrowser;

/**
 *
 * @author matt
 */
public class ChooseContact extends List implements CommandListener{
    private gvME midlet;
    private Command backCmd;
    private Command OKCmd;
    private Command okRecContactsCmd;
    private Command backFromRecContactsCmd;
    private Command okEnterNumCmd;
    private Command backFromEnterNumCmd;
    private Command backFromPimBrowserCmd;
    private Command okPimBrowserCmd;
    private Command backContactTypeCmd;
    private Command contactTypeCmd;
    private TextBox enterNumBox;
    private PIMBrowser pimBrowser;
    private String contact;

    private interCom next;
    private List recentContacts;
    private Displayable prev;

    public ChooseContact(gvME midlet, WriteMsg writeMsg, SendMsg sendMsg)
    {
        super("Choose Contact", Choice.IMPLICIT);
        this.midlet = midlet;
        this.prev = writeMsg;
       // this.sendMsg = sendMsg;
        this.next = sendMsg;
        initialize();
    }

    public ChooseContact(gvME midlet, MakeCall makeCall)
    {
        super("Choose Contact", Choice.IMPLICIT);
        this.midlet = midlet;
        this.prev = midlet.getMenu();
        this.next = makeCall;
        initialize();
    }

    public void initialize()
    {
     //   append("Recent", null);
        append("Enter Number", null);
        append("Phone Book", null);
        addCommand(getContactTypeCmd());
        addCommand(getBackContactTypeCmd());
        setSelectCommand(contactTypeCmd);
        setCommandListener(this);
    }
    public String getContact()
    {
        return this.contact;
    }
    public Command getContactTypeCmd() {
        if (contactTypeCmd == null) {
            // write pre-init user code here
            contactTypeCmd = new Command("Select", Command.ITEM, 1);
            // write post-init user code here
        }
        return contactTypeCmd;
    }

    public void chooseContactAction() {
        // enter pre-action user code here
        String __selectedString = this.getString(this.getSelectedIndex());
        if (__selectedString != null) {
            if (__selectedString.equals("Recent")) {
                // write pre-action user code here
                midlet.dispMan.switchDisplayable(null, getRecentContactsList());
                // write post-action user code here
            }
            if (__selectedString.equals("Enter Number")) {
                // write pre-action user code here
                enterNumBox = getEnterNumBox();
                midlet.dispMan.switchDisplayable(null, enterNumBox);
                // write post-action user code here
            } else if (__selectedString.equals("Phone Book")) {
                // write pre-action user code here
                pimBrowser = getPimBrowser();
                midlet.dispMan.switchDisplayable(null, pimBrowser);
                // write post-action user code here
            }
        }
    }


    public TextBox getEnterNumBox() {

            enterNumBox = new TextBox("Enter Number", null, 15, TextField.PHONENUMBER);
            enterNumBox.addCommand(getOkEnterNum());
            enterNumBox.addCommand(getBackFromEnterNum());
            enterNumBox.setCommandListener(this);

        return enterNumBox;
    }

    public List getRecentContactsList()
    {
        List recContactList = new List("Recent Contacts", List.IMPLICIT);
        recContactList.addCommand(List.SELECT_COMMAND);
        recContactList.addCommand(getBackFromRecContactsCmd());
        Vector contVect = midlet.userSettings.getRecentContacts();
        int vectSize = contVect.size();
        for(int i = 0; i < vectSize; i++)
        {
            String contactName = (String) ((KeyValuePair)contVect.elementAt(i)).getValue();
            recContactList.append(contactName, null);
        }
        return recContactList;
    }

    public PIMBrowser getPimBrowser() {
        if (pimBrowser == null) {
            // write pre-init user code here
            pimBrowser = new PIMBrowser(midlet.dispMan.getDisplay(), PIM.CONTACT_LIST);
            pimBrowser.setTitle("Contacts List");
            pimBrowser.addCommand(getOkPimBrowserCmd());
            pimBrowser.addCommand(getBackFromPimBrowserCmd());
            pimBrowser.setCommandListener(this);
            // write post-init user code here
        }
        return pimBrowser;
    }

    public void commandAction(Command command, Displayable display) {
        if(display == this)
        {
            if (command == backContactTypeCmd) {
                // write pre-action user code here
                midlet.dispMan.switchDisplayable(null, prev);
                // write post-action user code here
            } else if (command == contactTypeCmd) {
                chooseContactAction();
            }
        }
        else if (display == recentContacts)
        {
            if(command == backFromRecContactsCmd)
            {
                midlet.dispMan.switchToPreviousDisplayable();
            }
            else if (command == List.SELECT_COMMAND)
            {
                int selIndex = recentContacts.getSelectedIndex();
                contact = (String) ((KeyValuePair)(midlet.userSettings.getRecentContacts().elementAt(selIndex))).getKey();
                next.setContacting(contact);
            }
        }
        else if (display == enterNumBox)
        {
            if (command == backFromEnterNumCmd) {
                // write pre-action user code here
                midlet.dispMan.switchToPreviousDisplayable();
                // write post-action user code here
            } else if (command == okEnterNumCmd) {
                contact = enterNumBox.getString();
                next.setContacting(contact);
                midlet.userSettings.addContact(new KeyValuePair(contact, ""));
                midlet.dispMan.switchDisplayable(null,(Displayable) next);
            }
        }
        else if(display == pimBrowser)
        {
            if (command == backFromPimBrowserCmd) {
                // write pre-action user code here
                midlet.dispMan.switchToPreviousDisplayable();
                // write post-action user code here
            } else if (command == okPimBrowserCmd) {
                Contact pimContact = (Contact) pimBrowser.getSelectedItem();
                String pimName = pimContact.getString(Contact.NAME, Contact.NAME_GIVEN);
                String pimNumber = pimContact.getString(Contact.ATTR_PREFERRED, Contact.TEL);
                next.setContacting(pimNumber);
                midlet.userSettings.addContact(new KeyValuePair(pimNumber, pimName));
                midlet.dispMan.switchDisplayable(null,(Displayable) next);
            }
        }
    }

    private Command getBackFromRecContactsCmd()
    {
        if (backFromRecContactsCmd == null) {
            // write pre-init user code here
            backFromRecContactsCmd = new Command("Back", Command.BACK, 0);
            // write post-init user code here
        }
        return backFromRecContactsCmd;
    }

    private Command getBackFromPimBrowserCmd()
    {
        if (backFromPimBrowserCmd == null) {
            // write pre-init user code here
            backFromPimBrowserCmd = new Command("Back", Command.BACK, 0);
            // write post-init user code here
        }
        return backFromPimBrowserCmd;
    }



    private Command getBackContactTypeCmd() {
        if (backContactTypeCmd == null) {
            // write pre-init user code here
            backContactTypeCmd = new Command("Back", Command.BACK, 0);
            // write post-init user code here
        }
        return backContactTypeCmd;
    }

    private Command getBackFromEnterNum() {
        if (backFromEnterNumCmd == null) {
            // write pre-init user code here
            backFromEnterNumCmd = new Command("Back", Command.BACK, 0);
            // write post-init user code here
        }
        return backFromEnterNumCmd;
    }

    public Command getOkPimBrowserCmd() {
        if (okPimBrowserCmd == null) {
            // write pre-init user code here
            okPimBrowserCmd = new Command("Select", Command.OK, 1);
            // write post-init user code here
        }
        return okPimBrowserCmd;
    }

    public Command getOkEnterNum() {
        if (okEnterNumCmd == null) {
            // write pre-init user code here
            okEnterNumCmd = new Command("Ok", Command.OK, 1);
            // write post-init user code here
        }
        return okEnterNumCmd;
    }
}
