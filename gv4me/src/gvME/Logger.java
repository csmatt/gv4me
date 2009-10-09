/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;

/**
 *
 * @author matt
 */
public class Logger extends List implements CommandListener{
    private static Vector log = new Vector();
    private Command OKCmd, backCmd, clearCmd;
    private Form infoForm;
    private List errorForm;
    private static long lastUsedMem = 0;
    private Displayable prev;
    public Logger(Displayable prev)
    {
        super("Debug Info", Choice.IMPLICIT);
        append("Get Info", null);
        append("View Log", null);
        addCommand(getOKCmd());
        addCommand(getBackCmd());
        setSelectCommand(OKCmd);
        setCommandListener(this);
        this.prev = prev;
    }
    public static void add(String className, String method, String info)
    {        
        String[] combined = {className, ".", method, ": ", info};
        String logString = tools.combineStrings(combined);
        log.addElement(logString);
    }

    public static void add(String className, String info)
    {
        String[] combined = {className, ": ", info};
        String logString = tools.combineStrings(combined);
        log.addElement(logString);
    }

    private Form getInfoForm()
    {
        if(infoForm == null)
        {
            infoForm = new Form("Program Info");
            infoForm.addCommand(backCmd);
            infoForm.setCommandListener(this);
        }
        Runtime rt = Runtime.getRuntime();
        infoForm.deleteAll();
        infoForm.append("rnr: "+gvME.getRNR());
        long usedMem = (rt.totalMemory() - rt.freeMemory());
        infoForm.append("used: "+ usedMem);
        infoForm.append("last: "+ lastUsedMem);
        infoForm.append("Diff: "+ (usedMem - lastUsedMem));
        lastUsedMem = usedMem;
        return infoForm;
    }

    private List getErrorList()
    {
        if(errorForm == null)
        {
            errorForm = new List("Errors", Choice.IMPLICIT);
            errorForm.addCommand(OKCmd);
            errorForm.addCommand(getClearCmd());
            errorForm.addCommand(backCmd);
            errorForm.setSelectCommand(OKCmd);
            errorForm.setCommandListener(this);
        }
        errorForm.deleteAll();
        Enumeration vectEnum = log.elements();
        while(vectEnum.hasMoreElements())
        {
            errorForm.append((String)vectEnum.nextElement(), null);
        }
        return errorForm;
    }

    public static Vector getLog()
    {
        return log;
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
        if(backCmd == null)
        {
            backCmd = new Command("Back", Command.BACK, 0);
        }
        return backCmd;
    }

    private Command getClearCmd()
    {
        if(clearCmd == null)
        {
            clearCmd = new Command("Clear", Command.OK, 0);
        }
        return clearCmd;
    }
    
    public void commandAction(Command command, Displayable displayable) {
        if(displayable == this)
        {
            if(command == OKCmd)
            {
                int index = this.getSelectedIndex();
                if(index == 0)
                    gvME.dispMan.switchDisplayable(null, getInfoForm());
                else
                    gvME.dispMan.switchDisplayable(null, getErrorList());
            }
            else if(command == backCmd)
            {
                gvME.dispMan.switchDisplayable(null, gvME.getMenu());
            }
        }
        else if(command == backCmd && displayable != errorForm)// && (displayable == errorForm || displayable == infoForm))
        {
            gvME.dispMan.switchDisplayable(null, this);
        }
        else if(displayable != this && command == OKCmd)
        {
            Form details = new Form("Details");
            details.append((String)log.elementAt(errorForm.getSelectedIndex()));
            details.addCommand(backCmd);
            details.setCommandListener(this);
            gvME.dispMan.switchDisplayable(null, details);
        }
        else if(command == clearCmd)
        {
            log.removeAllElements();
            errorForm.deleteAll();
        }
        else if(command == backCmd && displayable == errorForm)
        {
            gvME.dispMan.switchDisplayable(null, this);
        }
    }
}
