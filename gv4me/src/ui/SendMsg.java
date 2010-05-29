/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;


import gvME.Logger;
import gvME.URLUTF8Encoder;
import gvME.connMgr;
import gvME.gvME;
import gvME.textConvo;
import gvME.textMsg;
import gvME.tools;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.HttpsConnection;
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
 * @author Matt Defenthaler
 */
public class SendMsg extends WaitScreen implements CommandListener, interCom {
    private static final String textURL = "https://www.google.com/voice/sms/send";
    private static final String replyURL = "https://www.google.com/voice/m/sendsms";
    private String msg = "";
    private String contacting = "";
    private String recipient = "";
    private String rnr = "";
    private textConvo original;
    private Vector reqProps = new Vector(5);
    private Image image;
    private Alert sendMsgFailedAlert;
    private Command cancelSendCmd;

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
        addCommand(getCancelSendCmd());
        setImage(getImage());
        setText("Sending Message...");
        this.msg = msg;
        this.rnr = gvME.getRNR();
        setTask(getSimpleCancellableTask());
    }

    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }

    /**
     * Posts data to Google Voice to send a message.
     * @param original The orignal textConvo that this is a reply to or forward of (optionally null if this is a new message)
     * @param sendingTo Recipient's phone number.
     * @param msg Message text.
     * @param rnr rnr token value.
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws Exception
     */
    private void sendMsg(textConvo original, String sendingTo, String msg, String rnr) throws ConnectionNotFoundException, IOException, Exception
    {
//        Vector reqProps = new Vector();
        String[] strings = new String[8];
        String postData = "";
        String url = "";
        String text = URLUTF8Encoder.encode(msg);
        if(original != null) //if this is a reply
        {
            String replyNum = original.getReplyNum();
            sendingTo = replyNum;
            url = replyURL;
            String[] stringBuff = {"&_rnr_se=", rnr, "&number=1", sendingTo, "&id=", original.getMsgID(), "&c=1&smstext=", text};
            strings = stringBuff;
            stringBuff = null;
        }
        else
        { //if this is a forward or new message
            url = textURL;
            String[] stringBuff = {"&id=&phoneNumber=+1", sendingTo, "&text=", text, "&_rnr_se=", rnr};
            strings = stringBuff;
            stringBuff = null;
        }

        postData = tools.combineStrings(strings);
        String[] contentLen = {"Content-Length", String.valueOf(postData.length())};
        reqProps.addElement(contentLen);
//        HttpsConnection sendCon = null;
        try{
            connMgr.open(url, "POST", reqProps, postData);
        }
        catch(ConnectionNotFoundException cnf)
        {
            Logger.add("gvSendMsg", cnf.getMessage());
            throw cnf;
        }
        finally{
            try{
                connMgr.close();
            }
            catch(Exception ignore){}
        }
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
                sendMsg(original, contacting, msg, rnr);
            }
        });
        return task;
    }

    public void commandAction(Command command, Displayable display) {
        textConvo sentMsg = new textConvo(recipient, contacting, new textMsg(msg));
            if (command == WaitScreen.FAILURE_COMMAND || command == cancelSendCmd) {
                try {
                    getTask().cancel();
                    gvME.getOutbox().addItem(sentMsg);
              //      System.out.println("Failed");
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
                    gvME.getSentBox().addItem(sentMsg);
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

    /**
     * Sets the number to contact in compliance with the interCom interface.
     * @param contacting number to contact
     * @param recipient name of recipient (or number if no name specified)
     */
    public void setContacting(String contacting, String recipient) {
        this.contacting = contacting;
        this.recipient = recipient;
    }

    private Command getCancelSendCmd()
    {
        if(cancelSendCmd == null)
        {
            cancelSendCmd = new Command("Cancel", Command.CANCEL, 0);
        }
        return cancelSendCmd;
    }
}
