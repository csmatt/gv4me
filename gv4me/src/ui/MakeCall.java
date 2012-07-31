/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.connMgr;
import gvME.gvME;
import gvME.settings;
import gvME.tools;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Alert;
import javax.microedition.midlet.MIDlet;
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
    private String rnr;
    private Vector reqProps = new Vector(2);
    private Alert callFailedAlert;
    private final String callURL = "https://www.google.com/voice/call/connect";
 //   private Image image;
    private static MIDlet midlet = null;

    public MakeCall() throws Exception
    {
        super(gvME.dispMan.getDisplay());
        initialize();
    }

    public MakeCall(String contacting, String recipient) throws Exception
    {
        super(gvME.dispMan.getDisplay());
        this.contacting = contacting;
        this.recipient = recipient;
        initialize();
    }

    public static void setMidlet(MIDlet midlet)
    {
        MakeCall.midlet = midlet;
    }

    private void initialize() throws Exception
    {
        if (!settings.callOutInfoExists())
        {
            throw new Exception("no call from");
        }
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
    private void makeDataCall(String contacting) throws ConnectionNotFoundException, IOException, Exception
    {
        String[] strings = {"outgoingNumber=", contacting, "&forwardingNumber=+1", settings.getCallFrom(), "&subscriberNumber=undefined&phoneType=2&remember=0&_rnr_se=", rnr};
        String postData = tools.combineStrings(strings);
        System.out.println(postData);
        String[] contentLen = {"Content-Length", String.valueOf(postData.length())};
        reqProps.insertElementAt(contentLen, 0);
        connMgr.open(callURL, "POST", reqProps, postData);
        String pageData = connMgr.getPageData();
        connMgr.close();
        System.out.println(pageData);
        if(pageData.indexOf("true") < 0)
            throw new Exception("call failed");
    }

    public void makeVoiceCall(String contacting)
    {
        String p = settings.getPauseChar();
        String[] callString = {"tel:", settings.getGVNumber(), ";postd=", settings.getPIN(), p, "2", contacting, "#"};
        contacting = tools.combineStrings(callString);
        try {
            boolean b = midlet.platformRequest(contacting);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SimpleCancellableTask getSimpleCancellableTask() {
        SimpleCancellableTask task = new SimpleCancellableTask();
        task.setExecutable(new org.netbeans.microedition.util.Executable() {
            public void execute() throws Exception {
                if(settings.getCallWith() == settings.getCallWithData())
                    makeDataCall(contacting);
                else if(settings.getCallWith() == settings.getCallWithVoice())
                    makeVoiceCall(contacting);
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
