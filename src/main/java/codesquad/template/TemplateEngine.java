package codesquad.template;

import codesquad.http.error.CannotRenderTemplateException;
import codesquad.template.compile.node.EvaluatorContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEngine {

    private static TemplateEngine instance;
    private static final Pattern replacePattern = Pattern.compile("\\{\\s*(\\w+)\\s*\\}");

    private final HtmlParser htmlParser;
    private final NodeProcessor nodeProcessor;

    private TemplateEngine(HtmlParser htmlParser,
                           NodeProcessor nodeProcessor) {
        this.htmlParser = htmlParser;
        this.nodeProcessor = nodeProcessor;
    }

    public static TemplateEngine createInstance(HtmlParser htmlParser,
                                                NodeProcessor nodeProcessor) {
        if (instance == null) {
            instance = new TemplateEngine(htmlParser, nodeProcessor);
        }
        return instance;
    }

    public static TemplateEngine getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TemplateEngine instance is not created yet");
        }
        return instance;
    }

    public String render(String templatePath, EvaluatorContext context) {
        try {
            String template = readResource(templatePath);
            String compiledTemplate = compileTemplate(context, template);

            StringBuilder result = new StringBuilder();
            Matcher matcher = replacePattern.matcher(compiledTemplate);
            while (matcher.find()) {
                String key = matcher.group(1).trim();
                Object replacement = context.getValue(key);
                if (replacement == null) {
                    throw new IllegalArgumentException("cannot render: " + key + " not found");
                }
                matcher.appendReplacement(result, replacement.toString());
            }
            matcher.appendTail(result);

            return result.toString();
        } catch (Exception e) {
            throw new CannotRenderTemplateException("cannot render: " + templatePath + " not found", e);
        }
    }

    private String compileTemplate(EvaluatorContext context, String template) {
        Node root = htmlParser.parse(template);
        nodeProcessor.processConditions(root, context);

        return root.toHtml();
    }

    private String readResource(String path) throws IOException {
        URL fileUrl = getClass().getClassLoader().getResource("static" + path);
        try (InputStream inputStream = fileUrl.openStream()) {
            return new String(inputStream.readAllBytes());
        }
    }
}
