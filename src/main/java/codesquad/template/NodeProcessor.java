package codesquad.template;

import codesquad.template.compile.SimpleBinaryExpressionParser;
import codesquad.template.compile.node.ASTNode;
import java.util.ArrayList;
import java.util.List;

public class NodeProcessor {

    private static final String MY_IF_TAG = "my-if";
    private static final String MY_FOR_EACH_TAG = "my-forEach";

    public void processConditions(Node node, TemplateContext context) {
        if (node == null) {
            return;
        }

        List<Node> nodesToRemove = new ArrayList<>();
        for (Node child : node.children) {
            String ifCondition = child.getAttribute(MY_IF_TAG);
            if (ifCondition != null && !evaluateCondition(ifCondition, context)) {
                nodesToRemove.add(child);
                child.removeAttribute(MY_IF_TAG);
            }
            processConditions(child, context);
        }
        node.children.removeAll(nodesToRemove);
    }

    private boolean evaluateCondition(String condition, TemplateContext context) {
        ASTNode ast = SimpleBinaryExpressionParser.parse(condition);
        String evaluate = ast.evaluate(context);

        return Boolean.parseBoolean(evaluate);
    }

    public void processForEach(Node root, TemplateContext context) {
        List<Node> nodesWithForEach = root.findNodesWithAttribute(MY_FOR_EACH_TAG);
        for (Node node : nodesWithForEach) {
            String forEachValue = node.getAttribute(MY_FOR_EACH_TAG);
            String[] parts = forEachValue.split(" in ");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid my-forEach attribute format: " + forEachValue);
            }

            String itemName = parts[0].trim();
            String listName = parts[1].trim();
            Object listObject = context.getValue(listName);
            if (!(listObject instanceof List)) {
                throw new IllegalArgumentException("Cannot render: " + listName + " is not a list");
            }

            List<?> list = (List<?>) listObject;
            StringBuilder repeatedContent = new StringBuilder();
            for (Object item : list) {
                TemplateContext newContext = new TemplateContext(context);
                newContext.setValue(itemName, item);
                String content = node.getInnerHtml();
                String processedContent = TemplateEngine.getInstance().processTemplate(content, newContext);

                Node nestedNode = HtmlParser.parse(processedContent);
                processForEach(nestedNode, newContext);
                repeatedContent.append(nestedNode.toHtml());
            }
            node.setInnerHtml(repeatedContent.toString());
            node.removeAttribute(MY_FOR_EACH_TAG);
        }
    }
}
