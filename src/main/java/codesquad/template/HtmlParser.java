package codesquad.template;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {

    private HtmlParser() {
    }

    public static Node parse(String html) {
        int pos = 0;
        Node root = new Node("root");
        List<Node> stack = new ArrayList<>();
        stack.add(root);

        while (pos < html.length()) {
            if (html.charAt(pos) == '<') {
                int endTagPos = html.indexOf('>', pos);
                if (endTagPos == -1) {
                    break;
                }
                String tagContent = html.substring(pos + 1, endTagPos);
                boolean isClosingTag = tagContent.startsWith("/");
                if (isClosingTag) {
                    stack.remove(stack.size() - 1);
                } else {
                    Node node = createNode(tagContent);
                    stack.get(stack.size() - 1).addChild(node);
                    if (!tagContent.endsWith("/")) {
                        stack.add(node);
                    }
                }
                pos = endTagPos + 1;
            } else {
                int textEnd = html.indexOf('<', pos);
                if (textEnd == -1) {
                    textEnd = html.length();
                }
                String text = html.substring(pos, textEnd).trim();
                if (!text.isEmpty() && !stack.isEmpty()) {
                    stack.get(stack.size() - 1).setTextContent(text);
                }
                pos = textEnd;
            }
        }

        return root;
    }

    private static Node createNode(String tagContent) {
        String[] parts = tagContent.split("\\s+", 2);
        String tagName = parts[0];
        Node node = new Node(tagName);

        if (parts.length > 1) {
            String attributesPart = parts[1];
            Pattern pattern = Pattern.compile("(\\w+-?\\w*)\\s*=\\s*\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(attributesPart);

            while (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher.group(2);
                node.addAttribute(key, value);
            }
        }

        return node;
    }
}
