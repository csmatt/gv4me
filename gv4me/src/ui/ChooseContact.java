/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import java.util.Enumeration;
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
import javax.microedition.rms.RecordStoreException;
import org.netbeans.microedition.lcdui.pda.PIMBrowser;

/**
 *
 * @author matt
 */
public class ChooseContact extends List implements CommandListener{
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
    private Vector contacts = gvME.userSettings.getRecentContacts();;

    public ChooseContact(Displayable prev, interCom com)
    {
        super("Choose Contact", Choice.IMPLICIT);
        this.next = com;
        this.prev = prev;
//        append("Recent", null);
        append("Enter Number", null);
//        append("Phone Book", null);
        addCommand(getContactTypeCmd());
        addCommand(getBackContactTypeCmd());
        setSelectCommand(contactTypeCmd);
        setCommandListener(this);
       // initialize();
    }

    public String getContact()
    {
        return this.contact;
    }

    public void chooseContactAction() {
        String __selectedString = this.getString(this.getSelectedIndex());
        if (__selectedString != null) {
//            if (__selectedString.equals("Recent")) {
//                gvME.dispMan.switchDisplayable(null, getRecentContactsList());
//            }
            if (__selectedString.equals("Enter Number")) {
                enterNumBox = getEnterNumBox();
                gvME.dispMan.switchDisplayable(null, enterNumBox);
            }
//            else if (__selectedString.equals("Phone Book")) {
//                pimBrowser = getPimBrowser();
//                gvME.dispMan.switchDisplayable(null, pimBrowser);
//            }
        }
    }

    public TextBox getEnterNumBox() {
        if(enterNumBox == null)
        {
            enterNumBox = new TextBox("Enter Number", null, 15, TextField.PHONENUMBER);
            enterNumBox.addCommand(getOkEnterNum());
            enterNumBox.addCommand(getBackFromEnterNum());
            enterNumBox.setCommandListener(this);
        }
        return enterNumBox;
    }

//    public List getRecentContactsList()
//    {
//        if(recentContacts == null)
//        {
//            recentContacts = new List("Recent Contacts", List.IMPLICIT);
//            recentContacts.addCommand(List.SELECT_COMMAND);
//            recentContacts.addCommand(getBackFromRecContactsCmd());
//        }
//            recentContacts.deleteAll();
//            contacts = gvME.userSettings.getRecentContacts();
//            //int vectSize = contacts.size();
//            //for(int i = 0; i < vectSize; i++)
//            Enumeration contactsEnum = contacts.elements();
//            {
//                String contactName = (String) ((KeyValuePair)contactsEnum.nextElement()).getKey();
//                recentContacts.append(contactName, null);
//            }
//
//        return recentContacts;
//    }

//    public PIMBrowser getPimBrowser() {
//        if (pimBrowser == null) {
//            pimBrowser = new PIMBrowser(gvME.dispMan.getDisplay(), PIM.CONTACT_LIST);
//            pimBrowser.setTitle("Contacts List");
//            pimBrowser.addCommand(getOkPimBrowserCmd());
//            pimBrowser.addCommand(getBackFromPimBrowserCmd());
//            pimBrowser.setCommandListener(this);
//        }
//        return pimBrowser;
//    }

    public void commandAction(Command command, Displayable display) {
        if(display == this)
        {
            if (command == backContactTypeCmd) {
                gvME.dispMan.switchDisplayable(null, prev);
            } else if (command == contactTypeCmd) {
                chooseContactAction();
            }
        }
//        else if (display == recentContacts)
//        {
//            if(command == backFromRecContactsCmd)
//            {
//                gvME.dispMan.switchToPreviousDisplayable();
//            }
//            else if (command == List.SELECT_COMMAND)
//            {
//                int selIndex = recentContacts.getSelectedIndex();
//                contact = (String) ((KeyValuePair)(gvME.userSettings.getRecentContacts().elementAt(selIndex))).getKey();
//                next.setContacting(contact);
//            }
//        }
        else if (display == enterNumBox)
        {
            if (command == backFromEnterNumCmd) {
                gvME.dispMan.switchToPreviousDisplayable();
            } else if (command == okEnterNumCmd) {
                contact = enterNumBox.getString();
                next.setContacting(contact);
//                try {
//                    gvME.userSettings.addContact(new KeyValuePair(contact, ""));
//                } catch (RecordStoreException ex) {
//                    ex.printStackTrace();
//                }
                gvME.dispMan.switchDisplayable(null,(Displayable) next);
            }
        }
//        else if(display == pimBrowser) //TODO: get PIMBrowser working
//        {
//            if (command == backFromPimBrowserCmd) {
//                gvME.dispMan.switchToPreviousDisplayable();
//            } else if (command == okPimBrowserCmd) {
//                Contact pimContact = (Contact) pimBrowser.getSelectedItem();
//                String pimName = pimContact.getString(Contact.NAME, Contact.NAME_GIVEN);
//                String pimNumber = pimContact.getString(Contact.ATTR_PREFERRED, Contact.TEL);
//                next.setContacting(pimNumber);
////                try {
////                    gvME.userSettings.addContact(new KeyValuePair(pimNumber, pimName));
////                } catch (RecordStoreException ex) {
////                    ex.printStackTrace();
////                }
//                gvME.dispMan.switchDisplayable(null,(Displayable) next);
//            }
//        }
    }

    private Command getContactTypeCmd() {
        if (contactTypeCmd == null) {
            contactTypeCmd = new Command("Select", Command.ITEM, 1);
        }
        return contactTypeCmd;
    }

//    private Command getBackFromRecContactsCmd()
//    {
//        if (backFromRecContactsCmd == null) {
//            backFromRecContactsCmd = new Command("Back", Command.BACK, 0);
//        }
//        return backFromRecContactsCmd;
//    }
//
//    private Command getBackFromPimBrowserCmd()
//    {
//        if (backFromPimBrowserCmd == null) {
//            backFromPimBrowserCmd = new Command("Back", Command.BACK, 0);
//        }
//        return backFromPimBrowserCmd;
//    }

    private Command getBackContactTypeCmd() {
        if (backContactTypeCmd == null) {
            backContactTypeCmd = new Command("Back", Command.BACK, 0);
        }
        return backContactTypeCmd;
    }

    private Command getBackFromEnterNum() {
        if (backFromEnterNumCmd == null) {
            backFromEnterNumCmd = new Command("Back", Command.BACK, 0);
        }
        return backFromEnterNumCmd;
    }

//    private Command getOkPimBrowserCmd() {
//        if (okPimBrowserCmd == null) {
//            okPimBrowserCmd = new Command("Select", Command.OK, 1);
//        }
//        return okPimBrowserCmd;
//    }

    private Command getOkEnterNum() {
        if (okEnterNumCmd == null) {
            okEnterNumCmd = new Command("Ok", Command.OK, 1);
        }
        return okEnterNumCmd;
    }
}
