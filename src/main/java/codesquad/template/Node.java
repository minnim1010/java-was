package codesquad.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

    String name;
    String textContent;
    Map<String, String> attributes;
    List<Node> children;

    public Node(String name) {
        this.name = name;
        this.attributes = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }

    public String getAttribute(String key) {
        return this.attributes.get(key);
    }

    public List<Node> findNodesWithAttribute(String attribute) {
        List<Node> result = new ArrayList<>();
        findNodesWithAttributeHelper(this, attribute, result, false);
        return result;
    }

    private void findNodesWithAttributeHelper(Node node, String attribute, List<Node> result, boolean foundParent) {
        if (node.attributes.containsKey(attribute)) {
            result.add(node);
            foundParent = true;
        }
        if (!foundParent) {
            for (Node child : node.children) {
                findNodesWithAttributeHelper(child, attribute, result, foundParent);
            }
        }
    }

    public String getInnerHtml() {
        StringBuilder html = new StringBuilder();
        for (Node child : children) {
            html.append(child.toHtml());
        }
        return html.toString();
    }

    public void setInnerHtml(String html) {
        this.children.clear();
        Node parsedNode = HtmlParser.parse(html);
        this.children.addAll(parsedNode.children);
    }

    public String toHtml() {
        StringBuilder html = new StringBuilder();
        toHtml(html);
        return html.toString();
    }

    private void toHtml(StringBuilder html) {
        if (!"root".equals(name)) {
            html.append("<").append(name);
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                html.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
            }
            html.append(">");
        }
        if (textContent != null && !textContent.isEmpty()) {
            html.append(textContent);
        }
        for (Node child : children) {
            child.toHtml(html);
        }
        if (!"root".equals(name)) {
            html.append("</").append(name).append(">");
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", textContent='" + textContent + '\'' +
                ", attributes=" + attributes +
                ", children=" + children +
                '}';
    }
}