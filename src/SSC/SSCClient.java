package SSC;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

public class SSCClient {

    private final WebClient WEB_CLIENT = new WebClient(BrowserVersion.CHROME);

    public SSCClient(){
        WEB_CLIENT.getCookieManager().setCookiesEnabled(true);
        WEB_CLIENT.getOptions().setCssEnabled(false);                   //disable CSS so faster
        WEB_CLIENT.getOptions().setThrowExceptionOnScriptError(false);  //ignore bad js
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
     * Attempts to authenticate a user with a given username and password
     * @param user  username to use
     * @param pass  password to use
     * @return      true if authentication successful, otherwise false
     */
    public boolean authenticate(String user, String pass){
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
    public boolean isAuthenticated(){
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

    public WebClient getClient(){
        return WEB_CLIENT;
    }

}
