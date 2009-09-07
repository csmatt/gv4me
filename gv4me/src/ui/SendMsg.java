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
    private Image image;

    public SendMsg(textConvo original, String msg)
    {
        super(gvME.dispMan.getDisplay());
        this.rnr = rnr;
        this.reqProps = parseMsgs.getReqProps();
        this.msg = msg;
        this.original = original;        
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
//TODO:         switchDisplayable(getAlert(), getCallWaitScreen());
                System.out.println("Failed");
            } else if (command == WaitScreen.SUCCESS_COMMAND) {
                gvME.dispMan.showMenu();
            }
    }
    
    /**
     * Returns an initiliazed instance of image component.
     * @return the initialized component instance
     */
    public Image getImage() {
        if (image == null) {
            try {
                image = Image.createImage("/pics/sendSMSIcon.png");
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    public void setContacting(String num) {
        this.contacting = num;
    }
}
