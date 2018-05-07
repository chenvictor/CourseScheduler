package SSC;

import SSC.Exceptions.InvalidLoginException;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.media.jfxmedia.logging.Logger;

import java.io.IOException;

public class SSCClient {

    private final WebClient WEB_CLIENT = new WebClient(BrowserVersion.CHROME);

    private final String USER;
    private final String PASS;

    private boolean loggedIn = false;

    public SSCClient(String user, String pass){
        USER = user;
        PASS = pass;

        WEB_CLIENT.getCookieManager().setCookiesEnabled(true);
        WEB_CLIENT.getOptions().setCssEnabled(false);   //disable CSS so faster
        WEB_CLIENT.getOptions().setThrowExceptionOnScriptError(false);  //ignore bad js
    }

    public void login() throws InvalidLoginException {
        if (isLoggedIn()) {
            Logger.logMsg(Logger.INFO, "Already Logged in");
            return;
        }
        Logger.logMsg(Logger.INFO, "Logging in...");
        try{
            HtmlPage loginPage = WEB_CLIENT.getPage(SSCURL.LOGIN.toString());
            HtmlForm loginForm = loginPage.getFirstByXPath("//form[@id='fm1']");

            loginForm.getInputByName("username").setValueAttribute(USER);
            loginForm.getInputByName("password").setValueAttribute(PASS);

            loginForm.getInputByName("submit").click();

            if (isLoggedIn()) {
                Logger.logMsg(Logger.INFO, "Logged in");
            } else {
                throw new InvalidLoginException(USER, PASS);
            }
        } catch (FailingHttpStatusCodeException | IOException e) {
            e.printStackTrace();
        }
    }

    public String get(SSCURL sscurl){
        return get(sscurl.toString());
    }

    public String get(String url){
        try{
            return WEB_CLIENT.getPage(url).getWebResponse().getContentAsString();
        } catch (FailingHttpStatusCodeException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isLoggedIn(){
        if (loggedIn) {
            return true;
        }
        try {
            HtmlPage loginPage = WEB_CLIENT.getPage(SSCURL.LOGIN.toString());
            DomElement message = loginPage.getElementById("msg");
            if (message == null) {
                return false;
            }
            if (!message.hasAttribute("class")) {
                return false;
            }
            loggedIn = message.getAttribute("class").equals("success");
            return loggedIn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public WebClient getClient(){
        return WEB_CLIENT;
    }

}
