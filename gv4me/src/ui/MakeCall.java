/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Alert;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 *
 * @author Matt Defenthaler
 * MakeCall handles placing a call through Google Voice.
 */
public class MakeCall extends WaitScreen implements CommandListener, interCom {
    private String contacting = "";
    private String recipient = "";
//    private Alert noCallFromAlert;
    private String rnr;
    private Vector reqProps = new Vector(2);
    private Alert callFailedAlert;
    private final String callURL = "https://www.google.com/voice/call/connect";
 //   private Image image;

    public MakeCall()
    {
        super(gvME.dispMan.getDisplay());
        initialize();
    }

    public MakeCall(String contacting, String recipient)
    {
        super(gvME.dispMan.getDisplay());
        this.contacting = contacting;
        this.recipient = recipient;
        initialize();
    }

    private void initialize()
    {
        setTitle("Making Call");
        setCommandListener(this);
        rnr = gvME.getRNR();
        setText("Making Call...");
        setTask(getSimpleCancellableTask());
    }

    /**
     * Posts data to Google Voice in order to initiate a call
     * @param contacting The number to be dialed.
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws Exception
     */
    private void makeCall(String contacting) throws ConnectionNotFoundException, IOException, Exception
    {
        String[] strings = {"&outgoingNumber=+1", contacting, "&forwardingNumber=+1", settings.getCallFrom(), "&subscriberNumber=undefined&remember=0&_rnr_se=", rnr};
        String postData = tools.combineStrings(strings);
        String[] contentLen = {"Content-Length", String.valueOf(postData.length())};
        reqProps.insertElementAt(contentLen, 0);
        connMgr.open(callURL, "POST", reqProps, postData);
        String pageData = connMgr.getPageData();
        connMgr.close();
        if(pageData.indexOf("true") < 0)
            throw new Exception("call failed");
    }

    public SimpleCancellableTask getSimpleCancellableTask() {
        SimpleCancellableTask task = new SimpleCancellableTask();
        task.setExecutable(new org.netbeans.microedition.util.Executable() {
            public void execute() throws Exception {
                makeCall(contacting);
            }
        });
        return task;
    }

    /**
     * Sets the number to contact in compliance with the interCom interface.
     * @param contacting number to contact
     * @param recipient name of recipient (or number if no name specified)
     */
    public void setContacting(String contacting, String recipient) {
        if(contacting.startsWith("1"))
            contacting = contacting.substring(1);
        this.contacting = contacting;
        this.recipient = recipient;
    }

    private Alert getCallFailedAlert()
    {
        if(callFailedAlert == null)
        {
            callFailedAlert = new Alert("Call Failed");
            callFailedAlert.setString("Unable to complete call.");
            callFailedAlert.setTimeout(2000);
        }
        return callFailedAlert;
    }

    public void commandAction(Command command, Displayable display) {
        if (command == WaitScreen.FAILURE_COMMAND) {
            gvME.dispMan.switchDisplayable(getCallFailedAlert(), gvME.getMenu());
        } else if (command == WaitScreen.SUCCESS_COMMAND) {
            gvME.dispMan.showMenu();
        }     
    }
}
