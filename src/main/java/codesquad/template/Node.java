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