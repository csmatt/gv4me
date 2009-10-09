/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStoreException;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 *
 * @author matt
 */
public class SendMsg extends WaitScreen implements CommandListener, interCom {
    private String msg = "";
    private String contacting = "";
    private String recipient = "";
    private String rnr = "";
    private textConvo original;
    private Vector reqProps;
    private Image image;
    private Alert sendMsgFailedAlert;

    public SendMsg(textConvo original, String msg)
    {
        super(gvME.dispMan.getDisplay());
        if(original != null)
        {
            this.original = original;
            this.recipient = original.getSender();
        }
        initialize(msg);
    }

    public SendMsg(String recipient, String contacting, String msg)
    {
        super(gvME.dispMan.getDisplay());
        this.recipient = recipient;
        this.contacting = contacting;
        initialize(msg);
    }

    private void initialize(String msg)
    {
        setTitle("Sending Message");
        setCommandListener(this);
        setImage(getImage());
        setText("Sending Message...");
        this.msg = msg;
        this.rnr = gvME.getRNR();
        this.reqProps = parseMsgs.getReqProps();
        setTask(getSimpleCancellableTask());
    }

    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }

    private Alert getSendMsgFailedAlert()
    {
        if(sendMsgFailedAlert == null)
        {
            sendMsgFailedAlert = new Alert("Sending Failed");
            sendMsgFailedAlert.setString("Unable to send message.");
            sendMsgFailedAlert.setTimeout(2000);
        }
        return sendMsgFailedAlert;
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
        textConvo sentMsg = new textConvo(recipient, contacting, new textMsg(msg));
            if (command == WaitScreen.FAILURE_COMMAND) {
                try {
                    gvME.outbox.addItem(sentMsg);
                    System.out.println("Failed");
                    gvME.dispMan.switchDisplayable(getSendMsgFailedAlert(), gvME.getMenu());
                } catch (IOException ex) {
                    Logger.add(getClass().getName(), "WS Fail", ex.getMessage());
                    ex.printStackTrace();
                } catch (RecordStoreException ex) {
                    Logger.add(getClass().getName(), "WS Fail", ex.getMessage());
                    ex.printStackTrace();
                }
            } else if (command == WaitScreen.SUCCESS_COMMAND) {
                try {
                    gvME.SentBox.addItem(sentMsg);
                } catch (IOException ex) {
                    Logger.add(getClass().getName(), "WS Success", ex.getMessage());
                    ex.printStackTrace();
                } catch (RecordStoreException ex) {
                    Logger.add(getClass().getName(), "WS Success", ex.getMessage());
                    ex.printStackTrace();
                }
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
            } catch (IOException ex) {
                Logger.add(getClass().getName(), "getImage", ex.getMessage());
                ex.printStackTrace();
            }
        }
        return image;
    }

    public void setContacting(String contacting, String recipient) {
        this.contacting = contacting;
        this.recipient = recipient;
    }
}
