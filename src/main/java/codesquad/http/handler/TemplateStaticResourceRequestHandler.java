package codesquad.http.handler;

import static codesquad.http.header.HeaderField.CONTENT_TYPE;

import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.http.session.Session;
import codesquad.template.TemplateEngine;
import codesquad.template.compile.node.EvaluatorContext;
import java.io.IOException;
import java.util.Set;

public class TemplateStaticResourceRequestHandler extends StaticResourceRequestHandler {

    private final TemplateEngine templateEngine;

    public TemplateStaticResourceRequestHandler(Set<String> staticResourcePaths,
                                                Set<String> defaultPages,
                                                TemplateEngine templateEngine) {
        super(staticResourcePaths, defaultPages);
        this.templateEngine = templateEngine;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String staticResourcePath = findStaticResourcePath(httpRequest.getUri().getPath());
        byte[] fileContent = readResource(staticResourcePath);

        if (staticResourcePath.endsWith("html")) {
            EvaluatorContext evaluatorContext = new EvaluatorContext();
            Session session = httpRequest.getSession();

            if (session != null && session.getAttribute("userId") != null) {
                evaluatorContext.setValue("user", session.getAttribute("userId"));
            }

            String templatedFileContent = templateEngine.apply(new String(fileContent), evaluatorContext);
            httpResponse.setBody(templatedFileContent.getBytes());
        } else {
            httpResponse.setBody(fileContent);
        }

        httpResponse.setHeader(CONTENT_TYPE.getFieldName(), determineContentType(httpRequest, staticResourcePath));

        httpResponse.setStatus(HttpStatus.OK);
    }
}
