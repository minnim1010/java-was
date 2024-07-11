package codesquad.template;

import codesquad.template.compile.node.EvaluatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEngine {

    private static final Pattern replacePattern = Pattern.compile("\\{\\s*(\\w+)\\s*\\}");

    private final HtmlParser htmlParser = new HtmlParser();
    private final NodeProcessor nodeProcessor = new NodeProcessor();

    public String apply(String template, EvaluatorContext context) {
        StringBuilder result = new StringBuilder();

        Node root = htmlParser.parse(template);
        nodeProcessor.processConditions(root, context);

        String processedHtml = root.toHtml();

        Matcher matcher = replacePattern.matcher(processedHtml);
        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String replacement = context.getValue(key);
            if (replacement == null) {
                throw new IllegalArgumentException("cannot render: " + key + " not found");
            }
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
