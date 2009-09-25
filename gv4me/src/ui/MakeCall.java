/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 *
 * @author matt
 */
public class MakeCall extends WaitScreen implements CommandListener, interCom {
    private String contacting = "";
    private Alert noCallFromAlert;
 //   private Image image;

    public MakeCall()
    {
        super(gvME.dispMan.getDisplay());
        setTitle("Making Call");
        setCommandListener(this);
     //   setImage(getImage());
        if(gvME.userSettings.getCallFrom() == null || gvME.userSettings.getCallFrom().equals(""))
        {
            gvME.dispMan.switchDisplayable(getNoCallFromAlert(), gvME.getChangeSettingsMenu());
        }
        setText("Making Call...");
        setTask(getSimpleCancellableTask());
    }

    public SimpleCancellableTask getSimpleCancellableTask() {
        SimpleCancellableTask task = new SimpleCancellableTask();
        task.setExecutable(new org.netbeans.microedition.util.Executable() {
            public void execute() throws Exception {
                gvMakeCall mc = new gvMakeCall();
                mc.makeCall(contacting);
                mc = null;
            }
        });
        return task;
    }

    private Alert getNoCallFromAlert()
    {
        if(noCallFromAlert == null)
        {
            noCallFromAlert = new Alert("Number Not Found", "Enter Your Number", null, AlertType.WARNING);
            noCallFromAlert.setTimeout(2000);
        }
        return noCallFromAlert;
    }

    public void setContacting(String num) {
        this.contacting = num;
    }

    public void commandAction(Command command, Displayable display) {
            if (command == WaitScreen.FAILURE_COMMAND) {
//TODO:         switchDisplayable(getAlert(), getCallWaitScreen());
                System.out.println("Failed");
            } else if (command == WaitScreen.SUCCESS_COMMAND) {
                gvME.dispMan.showMenu();
            }
    }
}
