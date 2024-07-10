package codesquad;

import codesquad.context.WebContext;

public class Main {

    public static void main(String[] args) {
        WebContext webContext = new WebContext();
        WebServer webServer = new WebServer(webContext);
        webServer.run();
    }
}