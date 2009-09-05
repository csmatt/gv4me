/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 *
 * @author matt
 */
public class MakeCall extends WaitScreen implements CommandListener, interCom {
    private String contacting = "";
    private gvME midlet;
 //   private Image image;

    public MakeCall(gvME midlet)
    {
        super(midlet.dispMan.getDisplay());
        this.midlet = midlet;
        setTitle("Making Call");
        setCommandListener(this);
     //   setImage(getImage());
        setText("Making Call...");
        setTask(getSimpleCancellableTask());
    }

    public SimpleCancellableTask getSimpleCancellableTask() {

        SimpleCancellableTask task = new SimpleCancellableTask();
        task.setExecutable(new org.netbeans.microedition.util.Executable() {
            public void execute() throws Exception {
                gvMakeCall mc = new gvMakeCall(midlet);
                mc.makeCall(contacting);
            }
        });
        return task;
    }

    public void setContacting(String num) {
        this.contacting = num;
    }

    public void commandAction(Command command, Displayable display) {
            if (command == WaitScreen.FAILURE_COMMAND) {
                // write pre-action user code here
          //      switchDisplayable(getAlert(), getCallWaitScreen());
                System.out.println("Failed");
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {
                // write pre-action user code here
                midlet.dispMan.showMenu();
                // write post-action user code here
            }
    }
}
