package SSC;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class SSCClient {

    private final WebClient WEB_CLIENT = new WebClient(BrowserVersion.CHROME);

    public void setSaveAuthentication(boolean saveAuthentication) {
        this.saveAuthentication = saveAuthentication;
    }

    private boolean saveAuthentication;

    public SSCClient(){
        WEB_CLIENT.getCookieManager().setCookiesEnabled(true);
        WEB_CLIENT.getOptions().setCssEnabled(false);                   //disable CSS so faster
        WEB_CLIENT.getOptions().setThrowExceptionOnScriptError(false);  //ignore bad js
        saveAuthentication = false; //by default, request authentication every time
    }

    public String get(SSCURL URL) {
        return get(URL.toString());
    }

    public String get(String URL){
        try {
            return WEB_CLIENT.getPage(URL).getWebResponse().getContentAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Requests a client to be authenticated
     * @param reason    reason to provide the user
     * @return          true if authenticated, false otherwise
     */
    public boolean requestAuthentication(String reason){
        if (isAuthenticated()) {
            return true;    //already authenticated
        }
        AuthenticationPopup popup = new AuthenticationPopup(reason);

        while(true) {
            popup.prompt();
            if (popup.canceled()) {
                System.out.println("Canceled");
                return false;
            }
            boolean attempt = authenticate(popup.getUsername(), popup.getPassword());
            if (attempt) {
                return true;
            }
            System.out.println("Try again");
        }

    }

    /**
     * Attempts to authenticate a user with a given username and password
     * @param user  username to use
     * @param pass  password to use
     * @return      true if authentication successful, otherwise false
     */
    private boolean authenticate(String user, String pass){
        try{
            System.out.println("Attempting to authenticate with user: " + user);
            HtmlPage loginPage = WEB_CLIENT.getPage(SSCURL.LOGIN.toString());
            HtmlForm loginForm = loginPage.getFirstByXPath("//form[@id='fm1']");

            loginForm.getInputByName("username").setValueAttribute(user);
            loginForm.getInputByName("password").setValueAttribute(pass);

            loginForm.getInputByName("submit").click();

            if (isAuthenticated()) {
                System.out.println("Authenticated " + user);
                return true;
            }
        } catch (FailingHttpStatusCodeException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("Failed to authenticate " + user);
        return false;
    }

    /**
     * Get authentication status
     * @return  true if client is authenticated, false otherwise
     */
    private boolean isAuthenticated(){
        try {
            HtmlPage loginPage = WEB_CLIENT.getPage(SSCURL.LOGIN.toString());
            DomElement message = loginPage.getElementById("msg");
            if (message == null) {
                return false;
            }
            if (!message.hasAttribute("class")) {
                return false;
            }
            return message.getAttribute("class").equals("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deAuthenticate(){
        if (saveAuthentication) {
            return;
        }
        forceDeAuthenticate();
    }

    private void forceDeAuthenticate(){
        try {
            WEB_CLIENT.getPage(SSCURL.LOGOUT.toString());
            System.out.println("Deauthenticated client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebClient getClient(){
        return WEB_CLIENT;
    }

    private class AuthenticationPopup{

        private final String reason;

        private String user;
        private String pass;
        private boolean cancel = false;

        AuthenticationPopup(String reason){
            this.reason = reason;
        }

        void prompt() {
            JTextField username = new JTextField();
            JPasswordField password = new JPasswordField();

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JLabel reason = new JLabel(this.reason);
            reason.setFont(new Font("Arial", Font.BOLD, 15));
            panel.add(reason);
            panel.add(new JLabel("Login Name"));
            panel.add(username);
            panel.add(new JLabel("Password"));
            panel.add(password);

            int result = JOptionPane.showConfirmDialog(null, panel, "Campus-Wide Login Authentication",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
            if (result == JOptionPane.OK_OPTION) {
                user = username.getText();
                pass = new String(password.getPassword());
                if (!valid()) {
                    prompt();
                }
            } else {
                cancel = true;
            }
        }

        private boolean valid() {
            return user.length() != 0 && pass.length() != 0;
        }

        String getUsername(){
            return user;
        }

        String getPassword(){
            return pass;
        }

        boolean canceled(){
            return cancel;
        }

    }

}
