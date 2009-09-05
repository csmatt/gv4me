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
import javax.microedition.lcdui.Image;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 *
 * @author matt
 */
public class SendMsg extends WaitScreen implements CommandListener, interCom {
    private String msg = "";
    private String contacting = "";
    private String rnr = "";
    private textConvo original;
    private Vector reqProps;
    private gvME midlet;
    private Image image;

    public SendMsg(gvME midlet, textConvo original, String msg, String rnr, Vector reqProps)
    {
        super(midlet.dispMan.getDisplay());
        this.rnr = rnr;
        this.reqProps = reqProps;
        this.msg = msg;
        this.original = original;
        this.midlet = midlet;
        
        setTitle("Sending Message");
        setCommandListener(this);
        setImage(getImage());
        setText("Sending Message...");
        setTask(getSimpleCancellableTask());
    }

    public SimpleCancellableTask getSimpleCancellableTask() {
        SimpleCancellableTask task = new SimpleCancellableTask();
        task.setExecutable(new org.netbeans.microedition.util.Executable() {
            public void execute() throws Exception {
                gvSendMsg.sendMsg(original, contacting, msg, rnr, reqProps);
            }
        });
        return task;
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
    
    /**
     * Returns an initiliazed instance of image component.
     * @return the initialized component instance
     */
    public Image getImage() {
        if (image == null) {
            // write pre-init user code here
            try {
                image = Image.createImage("/pics/sendSMSIcon.png");
            } catch (java.io.IOException e) {//GEN-END:|219-getter|1|219-@java.io.IOException
                e.printStackTrace();
            }
        }
        return image;
    }

    public void setContacting(String num) {
        this.contacting = num;
    }
}
