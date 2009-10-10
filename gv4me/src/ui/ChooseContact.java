/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import java.io.IOException;
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
    private Command backCmd;
    private Command OKCmd;
    private TextBox enterNumBox;
    private PIMBrowser pimBrowser;
    private KeyValuePair nameAndNumber;
    private interCom next;
    private List recentContacts;
    private Displayable prev;
    private Vector contacts = settings.getRecentContacts();;

    public ChooseContact(Displayable prev, interCom com)
    {
        super("Choose Contact", Choice.IMPLICIT);
        this.next = com;
        this.prev = prev;
        append("Recent", null);
        append("Phone Book", null);
        append("Enter Number", null);
        addCommand(getOKCmd());
        addCommand(getBackCmd());
        setSelectCommand(OKCmd);
        setCommandListener(this);
    }

    public KeyValuePair getContact()
    {
        return this.nameAndNumber;
    }

    private void getNumFromPIMBrowser()
    {
        Contact pimContact = (Contact) pimBrowser.getSelectedItem();
        String pimName = pimContact.getString(Contact.FORMATTED_NAME, 0);
        String pimNumber = pimContact.getString(Contact.TEL, 0);
        next.setContacting(pimNumber, pimName);
        try {
            settings.addContact(new KeyValuePair(pimNumber, pimName));
        } catch (RecordStoreException ex) {
            Logger.add(getClass().getName(), "getNumFromPIMBrowser", ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.add(getClass().getName(), "getNumFromPIMBrowser", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void getNumFromEnterNumBox()
    {
        String contact = enterNumBox.getString();

        next.setContacting(contact, contact);
        try {
            settings.addContact(new KeyValuePair(contact, contact));
        } catch (RecordStoreException ex) {
            Logger.add(getClass().getName(), "getNumFromEnterNumBox", ex.getMessage());
            ex.printStackTrace();
        }
        catch(IOException ex)
        {
            Logger.add(getClass().getName(), "getNumFromEnterNumBox", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void getNumFromRecentContacts()
    {
        int selIndex = recentContacts.getSelectedIndex();
         nameAndNumber = (KeyValuePair)(settings.getRecentContacts().elementAt(selIndex));
        next.setContacting((String)nameAndNumber.getKey(), (String)nameAndNumber.getValue());
        gvME.dispMan.switchDisplayable(null,(Displayable) next);
    }

    public void chooseContactAction() {
        String __selectedString = this.getString(this.getSelectedIndex());
        if (__selectedString != null) {
            if (__selectedString.equals("Recent")) {
                gvME.dispMan.switchDisplayable(null, getRecentContactsList());
            }
            if (__selectedString.equals("Enter Number")) {
                gvME.dispMan.switchDisplayable(null, getEnterNumBox());
            }
            else if (__selectedString.equals("Phone Book")) {
                pimBrowser = getPimBrowser();
                gvME.dispMan.switchDisplayable(null, pimBrowser);
            }
        }
    }

    public TextBox getEnterNumBox() {
        if(enterNumBox == null)
        {
            enterNumBox = new TextBox("Enter Number", null, 15, TextField.PHONENUMBER);
            enterNumBox.addCommand(getOKCmd());
            enterNumBox.addCommand(getBackCmd());
            enterNumBox.setCommandListener(this);
        }
        return enterNumBox;
    }

    public List getRecentContactsList()
    {
        if(recentContacts == null)
        {
            recentContacts = new List("Recent Contacts", List.IMPLICIT);
            recentContacts.addCommand(getOKCmd());
            recentContacts.addCommand(getBackCmd());
            recentContacts.setSelectCommand(OKCmd);
            recentContacts.setCommandListener(this);
        }
        else
        {
            recentContacts.deleteAll();
        }
        contacts = settings.getRecentContacts();

        Enumeration contactsEnum = contacts.elements();
        while(contactsEnum.hasMoreElements())
        {
            String contactName = (String) ((KeyValuePair)contactsEnum.nextElement()).getValue();
            recentContacts.append(contactName, null);
        }
        return recentContacts;
    }

    public PIMBrowser getPimBrowser() {
        if (pimBrowser == null) {
            pimBrowser = new PIMBrowser(gvME.dispMan.getDisplay(), PIM.CONTACT_LIST);
            pimBrowser.setTitle("Contacts List");
            pimBrowser.addCommand(getBackCmd());
            pimBrowser.setCommandListener(this);
        }
        return pimBrowser;
    }

    public void commandAction(Command command, Displayable display) {
        if(command == backCmd)
        {
            gvME.dispMan.switchDisplayable(null, prev);
        }
        else if(display == this)
        {
            if (command == OKCmd)
            {
                chooseContactAction();
            }
        }
        else
        {
            if(command == OKCmd || command == PIMBrowser.SELECT_PIM_ITEM)
            {
                if (display == recentContacts)
                {
                    if (command == OKCmd)
                    {
                        getNumFromRecentContacts();
                        gvME.dispMan.switchDisplayable(null,(Displayable) next);
                    }
                }
                else if (display == enterNumBox)
                {
                    if (command == OKCmd) {
                        getNumFromEnterNumBox();
                        gvME.dispMan.switchDisplayable(null,(Displayable) next);
                    }
                }
                else if(display == pimBrowser) //TODO: get PIMBrowser working
                {
                    if (command == PIMBrowser.SELECT_PIM_ITEM || command == PIMBrowser.SELECT_COMMAND) {
                        getNumFromPIMBrowser();
                        gvME.dispMan.switchDisplayable(null,(Displayable) next);
                    }
                }
                
            }
        }
    }
    private Command getOKCmd()
    {
        if(OKCmd == null)
        {
            OKCmd = new Command("OK", Command.ITEM, 1);
        }
        return OKCmd;
    }

    private Command getBackCmd()
    {
        if (backCmd == null) {
            backCmd = new Command("Back", Command.BACK, 0);
        }
        return backCmd;
    }
}
