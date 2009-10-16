///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package gvME;
//
//import java.io.IOException;
//import java.util.Vector;
//import javax.microedition.rms.RecordStoreException;
//import ui.Login;
//
///**
// *
// * @author matt
// */
//public class gvLogin {
//    private static final String rnrURL = "https://www.google.com/voice/m/i/voicemail";
//    private static final String clientLoginURL = "https://www.google.com/accounts/ClientLogin";
////    private static final String postData = "&rememberme=true&ltmpl=mobile&continue=https%3A%2F%2Fwww.google.com%2Fvoice%2Fm&ltmpl=mobile&btmpl=mobile&ltmpl=mobile&rmShown=1&signIn=Sign+in";
//    private static String username;
//    private static String password;
//    private static Login login;
//
//    public gvLogin() throws IOException, Exception
//    {
//        gvLogin.username = settings.getUsername();
//        gvLogin.password = settings.getPassword();
//        login = new Login(username, password);
//        checkLoginInfo();
//    }
//
//    private void checkLoginInfo() throws IOException, Exception {
//        if (!gvLogin.username.equals("") && !gvLogin.password.equals("")) {
//            gvME.dispMan.switchDisplayable(null, login);
//        }
//        else
//        {
//            gvME.dispMan.switchDisplayable(null, login.getLoginScreen());
//        }
//    }
//
//    public static void setLoginInfo(String username, String password)
//    {
//        gvLogin.username = username;
//        gvLogin.password = password;
//    }
//
//
//
//    public static void saveLoginInfo() throws RecordStoreException, RecordStoreException
//    {
//            settings.setUsername(gvLogin.username);
//            settings.setPassword(gvLogin.password);
//            settings.updateSettings();
//    }
//}
