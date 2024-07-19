package codesquad;

import codesquad.config.WasInitializer;
import codesquad.context.WebContext;

public class Main {

    public static void main(String[] args) {
        WebContext webContext = new WebContext();
        WebServer webServer = WebServerFactory.createWebServer(webContext);

        WasInitializer.init();

        webServer.run();
    }
}