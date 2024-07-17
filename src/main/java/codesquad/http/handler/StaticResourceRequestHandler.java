package codesquad.http.handler;

import static codesquad.http.header.HeaderField.ACCEPT;
import static codesquad.http.header.HeaderField.CONTENT_TYPE;
import static codesquad.utils.FileUtils.getFileExtension;

import codesquad.http.error.ResourceNotFoundException;
import codesquad.http.error.UnSupportedMediaTypeException;
import codesquad.http.header.ContentType;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.http.session.Session;
import codesquad.template.TemplateContext;
import codesquad.template.TemplateEngine;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

public class StaticResourceRequestHandler implements RequestHandler {

    private final Set<String> staticResourcePaths;
    private final Set<String> defaultPages;

    public StaticResourceRequestHandler(Set<String> staticResourcePaths,
                                        Set<String> defaultPages) {
        this.staticResourcePaths = staticResourcePaths;
        this.defaultPages = defaultPages;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String staticResourcePath = findStaticResourcePath(httpRequest.getUri().getPath());
        byte[] fileContent = readResource(staticResourcePath);
        byte[] renderedFileContent = renderTemplate(httpRequest, staticResourcePath, fileContent);

        httpResponse.setBody(renderedFileContent);
        httpResponse.setHeader(CONTENT_TYPE.getFieldName(), determineContentType(httpRequest, staticResourcePath));
        httpResponse.setStatus(HttpStatus.OK);
    }

    private byte[] renderTemplate(HttpRequest httpRequest,
                                  String staticResourcePath,
                                  byte[] fileContent) {
        if (!staticResourcePath.endsWith("html")) {
            return fileContent;
        }

        TemplateContext templateContext = new TemplateContext();
        Session session = httpRequest.getSession();

        if (session != null && session.getAttribute("userId") != null) {
            templateContext.setValue("user", session.getAttribute("userId"));
        }

        return TemplateEngine.getInstance().render(staticResourcePath, templateContext).getBytes();
    }

    private String findStaticResourcePath(String path) {
        if (staticResourcePaths.contains(path)) {
            return path;
        }

        return defaultPages.stream()
                .map(defaultPage -> path + (path.endsWith("/") ? "" : "/") + defaultPage)
                .filter(staticResourcePaths::contains)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + path));
    }

    private byte[] readResource(String staticResourcePath) throws IOException {
        URL fileUrl = getClass().getClassLoader().getResource("static" + staticResourcePath);
        return loadFile(fileUrl);
    }

    private byte[] loadFile(URL fileUrl) throws IOException {
        try (InputStream inputStream = fileUrl.openStream()) {
            return inputStream.readAllBytes();
        }
    }

    private String determineContentType(HttpRequest httpRequest, String staticFilePath) {
        String fileExtension = getFileExtension(staticFilePath);
        if (fileExtension.isEmpty()) {
            return ContentType.UNKNOWN.getResponseType();
        }

        String contentType = ContentType.getContentTypeByExtension(fileExtension);

        String acceptHeaderValue = httpRequest.getHeader(ACCEPT.getFieldName());
        if (acceptHeaderValue.contains(contentType) || acceptHeaderValue.contains("*/*")) {
            return contentType;
        }

        throw new UnSupportedMediaTypeException("Unsupported Media Type " + fileExtension);
    }
}
